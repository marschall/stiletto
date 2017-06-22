package com.github.marschall.stiletto.processor;

import static com.github.marschall.stiletto.processor.Processor.GENERATE_ASPECT;
import static com.github.marschall.stiletto.processor.Processor.GENERATE_ASPECTS;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes({GENERATE_ASPECT, GENERATE_ASPECTS})
@SupportedSourceVersion(RELEASE_8)
public class Processor extends AbstractProcessor {

  static final String GENERATE_ASPECTS = "com.github.marschall.stiletto.api.generation.GenerateAspects";

  static final String GENERATE_ASPECT = "com.github.marschall.stiletto.api.generation.GenerateAspect";

  private ProcessingEnvironment processingEnv;

  private ExecutableElement generateAspectValueMethod;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;


    TypeElement generateAspect = this.processingEnv.getElementUtils().getTypeElement(GENERATE_ASPECT);
    this.generateAspectValueMethod = getValueMethod(generateAspect);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    TypeElement generateAspectsType = this.processingEnv.getElementUtils().getTypeElement(GENERATE_ASPECTS);
    TypeElement generateAspectType = this.processingEnv.getElementUtils().getTypeElement(GENERATE_ASPECT);
    ExecutableElement generateAspectsValueMethod = getValueMethod(generateAspectsType);

    Set<AspectToGenerate> toGenerate = new HashSet<>();

    for (Element processedClass : roundEnv.getElementsAnnotatedWith(generateAspectsType)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      String processedClassName = getQualifiedName(processedClass);
      for (AnnotationMirror generateAspectsMirror : processedClass.getAnnotationMirrors()) { //@GenerateAspects
        AnnotationValue generateAspectsValue = generateAspectsMirror.getElementValues().get(generateAspectsValueMethod); //@GenerateAspects
        for (AnnotationValue generateAspectValue : generateAspectsValue.accept(AnnotationsMirrorExtractor.INSTANCE, null)) { //@GenerateAspect
          AnnotationMirror generateAspectMirror = generateAspectValue.accept(AnnotationMirrorExtractor.INSTANCE, null); //@GenerateAspect
          String aspectClassName = extractAspectClassName(generateAspectMirror);
          toGenerate.add(new AspectToGenerate(processedClassName, aspectClassName, processedClass));
        }
      }
    }
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(generateAspectType)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      String processedClassName = getQualifiedName(processedClass);
      for (AnnotationMirror generateAspectAnnotation : processedClass.getAnnotationMirrors()) { // @GenerateAspect
        String aspectClassName = extractAspectClassName(generateAspectAnnotation);
        toGenerate.add(new AspectToGenerate(processedClassName, aspectClassName, processedClass));
      }
    }
    for (AspectToGenerate aspectToGenerate : toGenerate) {
      generateAspectProtected(aspectToGenerate);
    }
    return true;
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

  private void generateAspectProtected(AspectToGenerate aspectToGenerate) {
    try {
      this.generateAspect(aspectToGenerate);
    } catch (IOException e) {
      Messager messager = this.processingEnv.getMessager();
      messager.printMessage(Kind.ERROR, e.getMessage(), aspectToGenerate.getOrignalElement());
    }
  }

  private void generateAspect(AspectToGenerate aspectToGenerate) throws IOException {
    Filer filer = this.processingEnv.getFiler();
    Element orignalElement = aspectToGenerate.getOrignalElement();
    String newClassName = generateClassName(orignalElement);
    String packageName = getPackageName(orignalElement);

    String fullyQualified;
    if (packageName.isEmpty()) {
      fullyQualified = newClassName;
    } else {
      fullyQualified = packageName + "." + newClassName;
    }
    JavaFileObject classFile = filer.createSourceFile(fullyQualified, orignalElement);

    TypeSpec helloWorld = TypeSpec.classBuilder(newClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addOriginatingElement(orignalElement)
            .build();

    JavaFile javaFile = JavaFile.builder(packageName, helloWorld)
            .build();

    try (Writer writer = classFile.openWriter()) {
      javaFile.writeTo(writer);
    }

  }

  private String generateClassName(Element originalClass) {
    return getSimpleName(originalClass) +"_";
  }

  private String extractAspectClassName(AnnotationMirror generateAspect) {
    // @GenerateAspect#value()
    AnnotationValue annotationValue = generateAspect.getElementValues().get(this.generateAspectValueMethod);
    TypeMirror aspectClass = annotationValue.accept(TypeMirrorExtractor.INSTANCE, null);
    // @GenerateAspect#value() = Aspect.class
    DeclaredType declaredType = aspectClass.accept(DeclaredTypeExtractor.INSTANCE, null);
    return getQualifiedName(declaredType.asElement());
  }

  private static String getQualifiedName(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null).getQualifiedName().toString();
  }

  private static String getSimpleName(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null).getSimpleName().toString();
  }

  private static String getPackageName(Element element) {
    String qualifiedName = getQualifiedName(element);
    String simpleName = getSimpleName(element);
    if (qualifiedName.length() == simpleName.length()) {
      return "";
    }
    // FIXME charsequence#length()
    return qualifiedName.substring(0, qualifiedName.length() - simpleName.length() - 1);
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

  static final class AspectToGenerate {

    private final String className;
    private final String apectClassName;
    private final Element orignalElement;

    AspectToGenerate(String className, String apectClassName, Element orignalElement) {
      this.className = className;
      this.apectClassName = apectClassName;
      this.orignalElement = orignalElement;
    }

    Element getOrignalElement() {
      return this.orignalElement;
    }

    String getClassName() {
      return this.className;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof AspectToGenerate)) {
        return false;
      }
      AspectToGenerate other = (AspectToGenerate) obj;
      return this.className.equals(other.className)
              && this.apectClassName.equals(other.apectClassName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.className, this.apectClassName);
    }

    @Override
    public String toString() {
      return "aspect: " + this.apectClassName + " -> " + this.className;
    }

  }

}
