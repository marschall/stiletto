package com.github.marschall.stiletto.processor;

import static com.github.marschall.stiletto.processor.ProxyGenerator.ADVISE_BY;
import static com.github.marschall.stiletto.processor.ProxyGenerator.ADVISE_BY_ALL;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

@SupportedAnnotationTypes({ADVISE_BY, ADVISE_BY_ALL})
@SupportedSourceVersion(RELEASE_8)
public class ProxyGenerator extends AbstractProcessor {

  static final String ADVISE_BY_ALL = "com.github.marschall.stiletto.api.generation.AdviseByAll";

  static final String ADVISE_BY = "com.github.marschall.stiletto.api.generation.AdviseBy";

  static final String BEFORE = "com.github.marschall.stiletto.api.advice.Before";

  static final String AROUND = "com.github.marschall.stiletto.api.advice.Around";

  static final String AFTER_THROWING = "com.github.marschall.stiletto.api.advice.AfterThrowing";

  static final String AFTER_RETURNING = "com.github.marschall.stiletto.api.advice.AfterReturning";

  static final String AFTER_FINALLY = "com.github.marschall.stiletto.api.advice.AfterFinally";

  private ProcessingEnvironment processingEnv;

  private ExecutableElement adviseByValueMethod;

  // TODO el?
  private NamingStrategy namingStrategy;

  private boolean addGenerated;

  private PrimitiveType intType;

  private PrimitiveType longType;

  private TypeElement before;

  private TypeElement around;

  private TypeElement afterThrowing;

  private TypeElement afterReturning;

  private TypeElement afterFinally;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;

    Elements elementUtils = this.processingEnv.getElementUtils();
    Types typeUtils = this.processingEnv.getTypeUtils();
    TypeElement adviseBy = elementUtils.getTypeElement(ADVISE_BY);
    this.before = elementUtils.getTypeElement(BEFORE);
    this.around = elementUtils.getTypeElement(AROUND);
    this.afterThrowing = elementUtils.getTypeElement(AFTER_THROWING);
    this.afterReturning = elementUtils.getTypeElement(AFTER_RETURNING);
    this.afterFinally = elementUtils.getTypeElement(AFTER_FINALLY);
    this.adviseByValueMethod = getValueMethod(adviseBy);
    this.namingStrategy = s -> s + "_";
    this.addGenerated = true;
    this.intType = typeUtils.getPrimitiveType(TypeKind.INT);
    this.longType = typeUtils.getPrimitiveType(TypeKind.LONG);
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
    TypeElement adviseByAllType = this.processingEnv.getElementUtils().getTypeElement(ADVISE_BY_ALL);
    ExecutableElement adviseByAllValueMethod = getValueMethod(adviseByAllType);

    // handle @AdviseByAll
    Set<ProxyToGenerate> toGenerate = new HashSet<>();
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByAllType)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror annotationMirror : processedClass.getAnnotationMirrors()) {
        if (annotationMirror.getAnnotationType().equals(adviseByAllType.asType())) { //@AdviseByAll
          AnnotationValue adviseByAllValue = annotationMirror.getElementValues().get(adviseByAllValueMethod); //@AdviseByAll
          for (AnnotationValue adviseByValue : adviseByAllValue.accept(AnnotationsMirrorExtractor.INSTANCE, null)) { //@AdviseBy
            AnnotationMirror adviseByMirror = adviseByValue.accept(AnnotationMirrorExtractor.INSTANCE, null); //@AdviseBy
            ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, adviseByMirror);
            toGenerate.add(proxyToGenerate);
          }
        }
      }
    }

    // handle @AdviseBy
    TypeElement adviseByType = this.processingEnv.getElementUtils().getTypeElement(ADVISE_BY);
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByType)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror annotationMirror : processedClass.getAnnotationMirrors()) {
        if (annotationMirror.getAnnotationType().equals(adviseByType.asType())) { // @AdviseBy
          ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, annotationMirror);
          toGenerate.add(proxyToGenerate);
        }
      }
    }

    return toGenerate;
  }

  private ProxyToGenerate buildProxyToGenerate(Element processedClass, AnnotationMirror adviseByMirror) {
    String processedClassName = getQualifiedName(processedClass);
    String aspectClassName = extractAspectClassName(adviseByMirror);
    TypeElement typeElement = processedClass.accept(TypeElementExtractor.INSTANCE, null);
    return new ProxyToGenerate(processedClassName, aspectClassName, typeElement, extractAspectClassTypeMirror(adviseByMirror));
  }

  private boolean validateAnnoatedClass(Element element) {
    TypeElement typeElement = element.accept(TypeElementExtractor.INSTANCE, null);
    boolean isClass = typeElement.getKind() == ElementKind.CLASS;
    boolean isEnum = typeElement.getKind() == ElementKind.ENUM;
    boolean isTopLevel = typeElement.getNestingKind() == NestingKind.TOP_LEVEL;
    Messager messager = this.processingEnv.getMessager();
    // no need to check for annotations handled by @Target

    boolean valid = true;

    if (!isTopLevel) {
      messager.printMessage(Kind.ERROR, "only top level types can be advised", typeElement);
      valid = false;
    }

    if (isClass) {
      boolean isAbstract = typeElement.getModifiers().contains(ABSTRACT);
      if (isAbstract) {
        messager.printMessage(Kind.ERROR, "advising abstract classes is not supported", typeElement);
        valid = false;
      }
    }

    if (isEnum) {
      messager.printMessage(Kind.ERROR, "advising enums is not supported", typeElement);
      valid = false;
    }

    // no final non-static methods
    for (Element member : this.processingEnv.getElementUtils().getAllMembers(typeElement)) {
      if (member.getKind() == ElementKind.METHOD) {
        if (member.getModifiers().contains(FINAL) && !member.getModifiers().contains(STATIC)) {
          // ignore final non-static methods from java.lang.Object
          ExecutableElement method = (ExecutableElement) member;
          if (!isFinalObjectMethod(method)) {
            messager.printMessage(Kind.ERROR, "final methods can not be proxied", typeElement);
            valid = false;
          }
        }
      }
    }


    return valid;
  }



  private List<ExecutableElement> getBeforeMethods(TypeElement aspect) {
    return getMethodsAnnotatedWith(aspect, this.before);
  }

  private List<ExecutableElement> getMethodsAnnotatedWith(TypeElement aspect, TypeElement annotation) {
    TypeMirror annotationType = annotation.asType();
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.processingEnv.getElementUtils().getAllMembers(aspect)) {
      if (member.getKind() == ElementKind.METHOD) {
        for (AnnotationMirror mirror : member.getAnnotationMirrors()) {
          if (mirror.getAnnotationType().equals(annotationType)) {
            methods.add((ExecutableElement) member);
          }
        }
      }
    }
    return methods;
  }

  private List<ExecutableElement> getMethodsToImplement(TypeElement targetClass) {
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.processingEnv.getElementUtils().getAllMembers(targetClass)) {
      if (member.getKind() == ElementKind.METHOD) {
        Set<Modifier> modifiers = member.getModifiers();
        if (!modifiers.contains(FINAL) && !modifiers.contains(STATIC) && !modifiers.contains(PRIVATE)) {
          ExecutableElement method = (ExecutableElement) member;
          // ignore methods from java.lang.Object like toString and hashCode
          if (!isObjectMethod(method)) {
            methods.add(method);
          }
        }
      }
    }
    return methods;
  }

  private List<ExecutableElement> getOverridableMethods(TypeElement element) {
    List<ExecutableElement> constrctors = new ArrayList<>();
    for (Element member : this.processingEnv.getElementUtils().getAllMembers(element)) {
      member.accept(OverridableMethodExtractor.INSTANCE, constrctors);
    }
    return constrctors;
  }

  private boolean isObjectMethod(ExecutableElement method) {
    // FIXME
    TypeElement object = this.processingEnv.getElementUtils().getTypeElement("java.lang.Object");
    return this.processingEnv.getElementUtils().getAllMembers(object).contains(method);
  }

  private boolean isFinalObjectMethod(ExecutableElement method) {
    Name methodName = method.getSimpleName();
    int parameterCount = method.getParameters().size();
    if (methodName.contentEquals("getClass") && parameterCount == 0) {
      // java.lang.Object.getClass()
      return true;
    }
    if (methodName.contentEquals("notify") && parameterCount == 0) {
      // java.lang.Object.notify()
      return true;
    }
    if (methodName.contentEquals("notifyAll") && parameterCount == 0) {
      // java.lang.Object.notifyAll()
      return true;
    }
    if (methodName.contentEquals("wait")) {
      if (parameterCount == 0) {
        // java.lang.Object.wait()
        return true;
      } else if (parameterCount == 1) {
        if (method.getParameters().get(0).asType().equals(this.longType)) {
          // java.lang.Object.wait(long)
          return true;
        }
      } else if (parameterCount == 2) {
        if (method.getParameters().get(0).asType().equals(this.longType)
                && method.getParameters().get(1).asType().equals(this.intType)) {
          // java.lang.Object.wait(long, int)
          return true;
        }
      }
    }
    return false;
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
    TypeElement targetClass = aspectToGenerate.getTargetClassElement();
    String proxyClassName = generateClassName(targetClass);
    String packageName = getPackageName(targetClass);

    FieldSpec targetObjectField = FieldSpec.builder(TypeName.get(targetClass.asType()), "targetObject", PRIVATE, FINAL)
            .build();
    FieldSpec aspectField = FieldSpec.builder(TypeName.get(aspectToGenerate.getAspect()), "aspect", PRIVATE, FINAL)
            .build();

    // TODO do not proxy equals and hashCode
    // TODO do not proxy finalize
    // TODO do not proxy Cloneable, Serializable, Externalizable, Comparable
    // TODO toString?

    Builder proxyClassBilder = TypeSpec.classBuilder(proxyClassName)
            // TODO always public?
            .addModifiers(PUBLIC, FINAL)
            .addJavadoc("Proxy class for {@link $T} being advised by {@link $T}.\n",
                    TypeName.get(targetClass.asType()),
                    TypeName.get(aspectToGenerate.getAspect()))
            .addOriginatingElement(targetClass)
            .addField(targetObjectField)
            .addField(aspectField);

    if (targetClass.getKind() == ElementKind.INTERFACE) {
      proxyClassBilder.addSuperinterface(TypeName.get(targetClass.asType()));
      List<? extends TypeParameterElement> typeParameters = targetClass.getTypeParameters();
      if (!typeParameters.isEmpty()) {
        for (TypeParameterElement typeParameter : typeParameters) {
          proxyClassBilder.addTypeVariable(TypeVariableName.get(typeParameter));
        }
      }

    } else if (isSubclassingRequired(targetClass)) {
      proxyClassBilder.superclass(TypeName.get(targetClass.asType()));
    } else {
      for (TypeMirror superinterface : targetClass.getInterfaces()) {
        proxyClassBilder.addSuperinterface(TypeName.get(superinterface));
      }
      // TODO check interfaces from superclasses
      List<? extends TypeParameterElement> typeParameters = targetClass.getTypeParameters();
      if (!typeParameters.isEmpty()) {
        for (TypeParameterElement typeParameter : typeParameters) {
          proxyClassBilder.addTypeVariable(TypeVariableName.get(typeParameter));
        }
      }
    }

    if (this.addGenerated) {
      proxyClassBilder.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.annotation", "Generated"))
              .addMember("value", "$S", this.getClass().getName())
              .build());
    }

    List<ExecutableElement> nonPrivateConstrctors = getNonPrivateConstrctors(targetClass);
    if (nonPrivateConstrctors.isEmpty()) {
      proxyClassBilder.addMethod(MethodSpec.constructorBuilder()
              // TODO keep visibility?
              .addModifiers(PUBLIC)
              .addParameter(TypeName.get(targetClass.asType()), "targetObject")
              .addParameter(TypeName.get(aspectToGenerate.getAspect()), "aspect")
              .addStatement("$T.requireNonNull(targetObject, $S)", Objects.class, "targetObject")
              .addStatement("this.targetObject = targetObject")
              .addStatement("$T.requireNonNull(aspect, $S)", Objects.class, "aspect")
              .addStatement("this.aspect = aspect")
              .build());
    } else {
      for (ExecutableElement constrctor : nonPrivateConstrctors) {
        // TODO keep visibility?
        proxyClassBilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameters(getParameters(constrctor))
                .addParameter(TypeName.get(targetClass.asType()), "targetObject")
                .addParameter(TypeName.get(aspectToGenerate.getAspect()), "aspect")
                // add super call even for default constructor in oder to aid debugging
                .addStatement(generateSuperCall(constrctor))
                .addStatement("$T.requireNonNull(targetObject, $S)", Objects.class, "targetObject")
                .addStatement("this.targetObject = targetObject")
                .addStatement("$T.requireNonNull(aspect, $S)", Objects.class, "aspect")
                .addStatement("this.aspect = aspect")
                .build());
      }
    }

    for (ExecutableElement method : this.getMethodsToImplement(targetClass)) {
      boolean isVoid = method.getReturnType().getKind() == TypeKind.VOID;
      proxyClassBilder.addMethod(MethodSpec.overriding(method)
              .addStatement((isVoid ? "" : "return ") + buildCall("this.targetObject." + method.getSimpleName(), method))
              .build());
    }

    TypeSpec proxyClass = proxyClassBilder.build();

    JavaFile javaFile = JavaFile.builder(packageName, proxyClass)
            .build();


    Filer filer = this.processingEnv.getFiler();
    String fullyQualified = getFullyQualifiedProxyClassName(packageName, proxyClassName);
    JavaFileObject javaFileObject = filer.createSourceFile(fullyQualified, targetClass);
    try (Writer writer = javaFileObject.openWriter()) {
      javaFile.writeTo(writer);
    }

  }

  private static Iterable<ParameterSpec> getParameters(ExecutableElement executableElement) {
    return executableElement.getParameters().stream()
            .map(ParameterSpec::get)
            .collect(Collectors.toList());
  }

  private static String generateSuperCall(ExecutableElement constructor) {
    return buildCall("super", constructor);
  }

  private static String buildCall(String receiver, ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    if (parameters.isEmpty()) {
      return receiver + "()";
    }
    StringBuilder buffer = new StringBuilder();
    buffer.append(receiver);
    buffer.append('(');
    boolean first = true;
    for (VariableElement parameter : parameters) {
      if (!first) {
        buffer.append(", ");
      }
      buffer.append(parameter.getSimpleName().toString());
      first = false;
    }
    buffer.append(')');
    return buffer.toString();
  }

  private String getFullyQualifiedProxyClassName(String packageName, String proxyClassName) {
    // FIXME find method
    if (packageName.isEmpty()) {
      return proxyClassName;
    } else {
      return packageName + "." + proxyClassName;
    }
  }

  private String generateClassName(TypeElement targetClass) {
    String simpleName = getSimpleName(targetClass);
    return this.namingStrategy.deriveClassName(simpleName);
  }

  private String extractAspectClassName(AnnotationMirror adviseBy) {
    // @AdviseBy#value()
    TypeMirror aspectClass = extractAspectClassTypeMirror(adviseBy);
    // @AdviseBy#value() = Aspect.class
    DeclaredType declaredType = aspectClass.accept(DeclaredTypeExtractor.INSTANCE, null);
    return getQualifiedName(declaredType.asElement());
  }

  private TypeMirror extractAspectClassTypeMirror(AnnotationMirror adviseBy) {
    // @AdviseBy#value()
    AnnotationValue annotationValue = adviseBy.getElementValues().get(this.adviseByValueMethod);
    return annotationValue.accept(TypeMirrorExtractor.INSTANCE, null);
  }

  private static String getQualifiedName(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null).getQualifiedName().toString();
  }

  private static String getSimpleName(TypeElement element) {
    return element.getSimpleName().toString();
  }

  private static List<ExecutableElement> getNonPrivateConstrctors(TypeElement element) {
    List<ExecutableElement> constrctors = new ArrayList<>(2);
    for (Element member : element.getEnclosedElements()) {
      member.accept(NonPrivateConstructorExtractor.INSTANCE, constrctors);
    }
    return constrctors;
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

  private boolean isSubclassingRequired(TypeElement typeElement) {
    if (typeElement.getKind() == ElementKind.INTERFACE) {
      return false;
    }
    // javax.lang.model.util.Elements.overrides(ExecutableElement, ExecutableElement, TypeElement)
    return true;
  }

  static final class NonPrivateConstructorExtractor extends SimpleElementVisitor8<Void, List<ExecutableElement>> {

    public static final ElementVisitor<Void, List<ExecutableElement>> INSTANCE = new NonPrivateConstructorExtractor();

    @Override
    public Void visitExecutable(ExecutableElement e, List<ExecutableElement> p) {
      if (e.getKind() == ElementKind.CONSTRUCTOR
              && !e.getModifiers().contains(PRIVATE)) {
        p.add(e);
      }
      return null;
    }

  }

  static final class OverridableMethodExtractor extends SimpleElementVisitor8<Void, List<ExecutableElement>> {

    public static final ElementVisitor<Void, List<ExecutableElement>> INSTANCE = new OverridableMethodExtractor();

    @Override
    public Void visitExecutable(ExecutableElement e, List<ExecutableElement> p) {
      Set<Modifier> modifiers = e.getModifiers();
      if (e.getKind() == ElementKind.METHOD) {
        if (!modifiers.contains(PRIVATE)
                && !modifiers.contains(STATIC)
                && !modifiers.contains(FINAL)) {
          p.add(e);
        }
      }
      return null;
    }

  }

  static abstract class ExpectedElementExtractor<R> extends SimpleElementVisitor8<R, Void> {

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

  static abstract class ExpectedTypeExtractor<R> extends SimpleTypeVisitor8<R, Void> {

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

  static abstract class ExpectedValueExtractor<R> extends SimpleAnnotationValueVisitor8<R, Void> {

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
    private final TypeElement targetClassElement;
    private final TypeMirror aspect;

    ProxyToGenerate(String targetClassName, String apectClassName, TypeElement targetClassElement, TypeMirror aspect) {
      this.targetClassName = targetClassName;
      this.aspectClassName = apectClassName;
      this.targetClassElement = targetClassElement;
      this.aspect = aspect;
    }

    TypeElement getTargetClassElement() {
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
      return "aspect: " + this.aspectClassName + " -> target class:" + this.targetClassName;
    }

  }

}
