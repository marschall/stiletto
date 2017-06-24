package com.github.marschall.stiletto.processor;

import static com.github.marschall.stiletto.processor.Processor.APPLY_ASPECT;
import static com.github.marschall.stiletto.processor.Processor.APPLY_ASPECTS;
import static javax.lang.model.SourceVersion.RELEASE_8;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes({APPLY_ASPECT, APPLY_ASPECTS})
@SupportedSourceVersion(RELEASE_8)
public class Processor extends AbstractProcessor {

  static final String APPLY_ASPECTS = "com.github.marschall.stiletto.api.generation.ApplyAspects";

  static final String APPLY_ASPECT = "com.github.marschall.stiletto.api.generation.ApplyAspect";

  private ProcessingEnvironment processingEnv;

  private ExecutableElement applyAspectValueMethod;

  private NamingStrategy namingStrategy;

  private boolean addGenerated;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;


    TypeElement applyAspect = this.processingEnv.getElementUtils().getTypeElement(APPLY_ASPECT);
    this.applyAspectValueMethod = getValueMethod(applyAspect);
    this.namingStrategy = s -> s + "_";
    this.addGenerated = true;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<ProxyToGenerate> toGenerate = extractAspectsToGenerate(roundEnv);
    generateProxies(toGenerate);
    return true;
  }

  private void generateProxies(Set<ProxyToGenerate> toGenerate) {
    for (ProxyToGenerate proxyToGenerate : toGenerate) {
      generateProxyProtected(proxyToGenerate);
    }
  }

  private Set<ProxyToGenerate> extractAspectsToGenerate(RoundEnvironment roundEnv) {
    TypeElement applyAspectsType = this.processingEnv.getElementUtils().getTypeElement(APPLY_ASPECTS);
    ExecutableElement applyAspectsValueMethod = getValueMethod(applyAspectsType);

    // handle @ApplyAspects
    Set<ProxyToGenerate> toGenerate = new HashSet<>();
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(applyAspectsType)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror applyAspectsMirror : processedClass.getAnnotationMirrors()) { //@ApplyAspects
        AnnotationValue applyAspectsValue = applyAspectsMirror.getElementValues().get(applyAspectsValueMethod); //@ApplyAspects
        for (AnnotationValue applyAspectValue : applyAspectsValue.accept(AnnotationsMirrorExtractor.INSTANCE, null)) { //@ApplyAspect
          AnnotationMirror applyAspectMirror = applyAspectValue.accept(AnnotationMirrorExtractor.INSTANCE, null); //@ApplyAspect
          ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, applyAspectMirror);
          toGenerate.add(proxyToGenerate);
        }
      }
    }

    // handle @ApplyAspect
    TypeElement applyAspectType = this.processingEnv.getElementUtils().getTypeElement(APPLY_ASPECT);
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(applyAspectType)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror applyAspectMirror : processedClass.getAnnotationMirrors()) { // @ApplyAspect
        ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, applyAspectMirror);
        toGenerate.add(proxyToGenerate);
      }
    }

    return toGenerate;
  }

  private ProxyToGenerate buildProxyToGenerate(Element processedClass, AnnotationMirror applyAspectMirror) {
    String processedClassName = getQualifiedName(processedClass);
    String aspectClassName = extractAspectClassName(applyAspectMirror);
    return new ProxyToGenerate(processedClassName, aspectClassName, processedClass, extractAspectClassTypeMirror(applyAspectMirror));
  }

  private boolean validateAnnoatedClass(Element element) {
    TypeElement typeElement = element.accept(TypeElementExtractor.INSTANCE, null);
    boolean valid = typeElement.getNestingKind() == NestingKind.TOP_LEVEL;
    if (!valid) {
      Messager messager = this.processingEnv.getMessager();
      messager.printMessage(Kind.ERROR, "only top level types are supported", typeElement);
    }
    return valid;
  }

  private void generateProxyProtected(ProxyToGenerate proxyToGenerate) {
    try {
      this.generateProxy(proxyToGenerate);
    } catch (IOException e) {
      Messager messager = this.processingEnv.getMessager();
      messager.printMessage(Kind.ERROR, e.getMessage(), proxyToGenerate.getTargetClassElement());
    }
  }

  private void generateProxy(ProxyToGenerate aspectToGenerate) throws IOException {
    Filer filer = this.processingEnv.getFiler();
    Element annotatedClass = aspectToGenerate.getTargetClassElement();
    String newClassName = generateClassName(annotatedClass);
    String packageName = getPackageName(annotatedClass);

    String fullyQualified; // FIXME find method
    if (packageName.isEmpty()) {
      fullyQualified = newClassName;
    } else {
      fullyQualified = packageName + "." + newClassName;
    }
    JavaFileObject classFile = filer.createSourceFile(fullyQualified, annotatedClass);

    FieldSpec delegateField = FieldSpec.builder(TypeName.get(annotatedClass.asType()), "delegate", Modifier.PRIVATE, Modifier.FINAL)
            .build();
    FieldSpec aspectField = FieldSpec.builder(TypeName.get(aspectToGenerate.getAspect()), "aspect", Modifier.PRIVATE, Modifier.FINAL)
            .build();

    MethodSpec constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.get(annotatedClass.asType()), "delegate")
            .addParameter(TypeName.get(aspectToGenerate.getAspect()), "aspect")
            .addStatement("this.delegate = delegate")
            .addStatement("this.aspect = aspect")
            .build();

    // TODO Generated
    // TODO JavaDoc
    TypeSpec generatedAspect = TypeSpec.classBuilder(newClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addOriginatingElement(annotatedClass)
            .addField(delegateField)
            .addField(aspectField)
            .addMethod(constructor)
            .build();

    JavaFile javaFile = JavaFile.builder(packageName, generatedAspect)
            .build();

    try (Writer writer = classFile.openWriter()) {
      javaFile.writeTo(writer);
    }

  }

  private String generateClassName(Element originalClass) {
    String simpleName = getSimpleName(originalClass);
    return this.namingStrategy.deriveClassName(simpleName);
  }

  private String extractAspectClassName(AnnotationMirror applyAspect) {
    // @ApplyAspect#value()
    TypeMirror aspectClass = extractAspectClassTypeMirror(applyAspect);
    // @ApplyAspect#value() = Aspect.class
    DeclaredType declaredType = aspectClass.accept(DeclaredTypeExtractor.INSTANCE, null);
    return getQualifiedName(declaredType.asElement());
  }

  private TypeMirror extractAspectClassTypeMirror(AnnotationMirror applyAspect) {
    // @ApplyAspect#value()
    AnnotationValue annotationValue = applyAspect.getElementValues().get(this.applyAspectValueMethod);
    return annotationValue.accept(TypeMirrorExtractor.INSTANCE, null);
  }

  private static String getQualifiedName(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null).getQualifiedName().toString();
  }

  private static String getSimpleName(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null).getSimpleName().toString();
  }

  private String getPackageName(Element element) {
    PackageElement packageElement = this.processingEnv.getElementUtils().getPackageOf(element);
    return packageElement.getQualifiedName().toString();
  }

  private ExecutableElement getValueMethod(TypeElement typeElement) {
    String methodName = "value";
    for (Element member : typeElement.getEnclosedElements()) {
      if (member.getKind() == ElementKind.METHOD && member.getSimpleName().contentEquals(methodName)) {
        ExecutableElement method = (ExecutableElement) member;
        if (method.getParameters().isEmpty()) {
          return method;
        }
      }
    }
    throw new NoSuchElementException("no method named: " + methodName);
  }

  private boolean isSubclassingRequired() {
    // TODO is interface
    // javax.lang.model.util.Elements.overrides(ExecutableElement, ExecutableElement, TypeElement)
    return true;
  }

  static class ExpectedElementExtractor<R> extends SimpleElementVisitor8<R, Void> {

    @Override
    protected R defaultAction(Element e, Void p) {
      throw new IllegalArgumentException("unknown element type");
    }

  }

  static final class TypeElementExtractor extends ExpectedElementExtractor<TypeElement> {

    static final ElementVisitor<TypeElement, Void> INSTANCE = new TypeElementExtractor();

    @Override
    public TypeElement visitType(TypeElement e, Void p) {
      return e;
    }

  }

  static final class PackageElementExtractor extends ExpectedElementExtractor<PackageElement> {

    static final ElementVisitor<PackageElement, Void> INSTANCE = new PackageElementExtractor();

    @Override
    public PackageElement visitPackage(PackageElement e, Void p) {
      return e;
    }

  }

  static class ExpectedTypeExtractor<R> extends SimpleTypeVisitor8<R, Void> {

    @Override
    protected R defaultAction(TypeMirror e, Void p) {
      throw new IllegalArgumentException("unknown element type");
    }

  }

  static final class DeclaredTypeExtractor extends ExpectedTypeExtractor<DeclaredType> {

    static final TypeVisitor<DeclaredType, Void> INSTANCE = new DeclaredTypeExtractor();

    @Override
    public DeclaredType visitDeclared(DeclaredType t, Void p) {
      return t;
    }

  }

  static class ExpectedValueExtractor<R> extends SimpleAnnotationValueVisitor8<R, Void> {

    @Override
    protected R defaultAction(Object o, Void p) {
      throw new IllegalArgumentException("unknown element type");
    }

  }

  static final class TypeMirrorExtractor extends ExpectedValueExtractor<TypeMirror> {

    static final AnnotationValueVisitor<TypeMirror, Void> INSTANCE = new TypeMirrorExtractor();

    @Override
    public TypeMirror visitType(TypeMirror t, Void p) {
      return t;
    }

  }

  static final class AnnotationMirrorExtractor extends ExpectedValueExtractor<AnnotationMirror> {

    static final AnnotationValueVisitor<AnnotationMirror, Void> INSTANCE = new AnnotationMirrorExtractor();

    @Override
    public AnnotationMirror visitAnnotation(AnnotationMirror a, Void p) {
      return a;
    }

  }

  static final class AnnotationsMirrorExtractor extends ExpectedValueExtractor<List<? extends AnnotationValue>> {

    static final AnnotationValueVisitor<List<? extends AnnotationValue>, Void> INSTANCE = new AnnotationsMirrorExtractor();

    @Override
    public List<? extends AnnotationValue> visitArray(List<? extends AnnotationValue> vals, Void p) {
      return vals;
    }

  }

  static final class ProxyToGenerate {

    private final String targetClassName;
    private final String aspectClassName;
    private final Element targetClassElement;
    private final TypeMirror aspect;

    ProxyToGenerate(String targetClassName, String apectClassName, Element targetClassElement, TypeMirror aspect) {
      this.targetClassName = targetClassName;
      this.aspectClassName = apectClassName;
      this.targetClassElement = targetClassElement;
      this.aspect = aspect;
    }

    Element getTargetClassElement() {
      return this.targetClassElement;
    }

    TypeMirror getAspect() {
      return this.aspect;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof ProxyToGenerate)) {
        return false;
      }
      ProxyToGenerate other = (ProxyToGenerate) obj;
      return this.targetClassName.equals(other.targetClassName)
              && this.aspectClassName.equals(other.aspectClassName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.targetClassName, this.aspectClassName);
    }

    @Override
    public String toString() {
      return "aspect: " + this.aspectClassName + " -> " + this.targetClassName;
    }

  }

}
