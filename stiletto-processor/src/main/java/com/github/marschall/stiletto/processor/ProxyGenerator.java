package com.github.marschall.stiletto.processor;

import static com.github.marschall.stiletto.processor.AptUtils.asAnnotationMirror;
import static com.github.marschall.stiletto.processor.AptUtils.asAnnotationValues;
import static com.github.marschall.stiletto.processor.AptUtils.asDeclaredType;
import static com.github.marschall.stiletto.processor.AptUtils.asString;
import static com.github.marschall.stiletto.processor.AptUtils.asTypeElement;
import static com.github.marschall.stiletto.processor.AptUtils.asTypeMirror;
import static com.github.marschall.stiletto.processor.AptUtils.getNonPrivateConstrctors;
import static com.github.marschall.stiletto.processor.AptUtils.getQualifiedName;
import static com.github.marschall.stiletto.processor.AptUtils.getSimpleName;
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
import java.util.EnumSet;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.github.marschall.stiletto.processor.el.JoinPoint;
import com.github.marschall.stiletto.processor.el.TargetClass;
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

  private static final Set<Modifier> NON_OVERRIDABLE = EnumSet.of(PRIVATE, STATIC, FINAL);

  private ProcessingEnvironment processingEnv;

  private ExecutableElement adviseByValueMethod;

  private NamingStrategy namingStrategy;

  private boolean addGenerated;

  private PrimitiveType intType;

  private PrimitiveType longType;

  private TypeMirror objectType;

  private TypeElement evaluateElement;

  private TypeMirror evaluateType;

  // FIXME should all be type mirrors
  private TypeElement before;

  private TypeElement around;

  private TypeElement afterThrowing;

  private TypeElement afterReturning;

  private TypeElement afterFinally;

  private ExpressionEvaluator evaluator;

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
    this.objectType = this.processingEnv.getElementUtils().getTypeElement("java.lang.Object").asType();
    this.evaluateElement = this.processingEnv.getElementUtils().getTypeElement("com.github.marschall.stiletto.api.injection.Evaluate");
    this.evaluateType = this.evaluateElement.asType();
    this.evaluator = new ExpressionEvaluator();
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
    TypeElement adviseByAllElement = this.processingEnv.getElementUtils().getTypeElement(ADVISE_BY_ALL);
    ExecutableElement adviseByAllValueMethod = getValueMethod(adviseByAllElement);

    // handle @AdviseByAll
    Set<ProxyToGenerate> toGenerate = new HashSet<>();
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByAllElement)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror adviseByAll : this.getAnnotationMirrorsOfType(processedClass, adviseByAllElement.asType())) { //@AdviseByAll
        AnnotationValue adviseByAllValue = adviseByAll.getElementValues().get(adviseByAllValueMethod); //@AdviseByAll
        for (AnnotationValue adviseByValue : asAnnotationValues(adviseByAllValue)) { //@AdviseBy
          AnnotationMirror adviseByMirror = asAnnotationMirror(adviseByValue); //@AdviseBy
          ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, adviseByMirror);
          toGenerate.add(proxyToGenerate);
        }
      }
    }

    // handle @AdviseBy
    TypeElement adviseByElement = this.processingEnv.getElementUtils().getTypeElement(ADVISE_BY);
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByElement)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror adviseByMirror : this.getAnnotationMirrorsOfType(processedClass, adviseByElement.asType())) { // @AdviseBy
        ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, adviseByMirror);
        toGenerate.add(proxyToGenerate);
      }
    }

    return toGenerate;
  }

  private List<AnnotationMirror> getAnnotationMirrorsOfType(Element element, TypeMirror type) {
    List<AnnotationMirror> mirrors = new ArrayList<>(1);
    for (AnnotationMirror annotationMirror : this.processingEnv.getElementUtils().getAllAnnotationMirrors(element)) {
      if (this.isSameType(type, annotationMirror.getAnnotationType())) {
        mirrors.add(annotationMirror);
      }
    }
    return mirrors;
  }

  private ProxyToGenerate buildProxyToGenerate(Element processedClass, AnnotationMirror adviseByMirror) {
    String processedClassName = getQualifiedName(processedClass);
    String aspectClassName = extractAspectClassName(adviseByMirror);
    TypeElement typeElement = asTypeElement(processedClass);
    return new ProxyToGenerate(processedClassName, aspectClassName, typeElement, extractAspectClassTypeMirror(adviseByMirror));
  }

  private boolean validateAnnoatedClass(Element element) {
    TypeElement typeElement = asTypeElement(element);
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
        Set<Modifier> modifiers = member.getModifiers();
        if (modifiers.contains(FINAL) && !modifiers.contains(STATIC)) {
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

  private List<ExecutableElement> getAfterReturningMethods(TypeElement aspect) {
    return getMethodsAnnotatedWith(aspect, this.afterReturning);
  }

  private List<ExecutableElement> getAfterFinallyMethods(TypeElement aspect) {
    return getMethodsAnnotatedWith(aspect, this.afterFinally);
  }

  private List<ExecutableElement> getAfterThrowingMethods(TypeElement aspect) {
    return getMethodsAnnotatedWith(aspect, this.afterThrowing);
  }

  private List<ExecutableElement> getMethodsToImplement(TypeElement targetClass) {
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.processingEnv.getElementUtils().getAllMembers(targetClass)) {
      member.accept(new ImplementableMethodExtractor(), methods);
    }
    return methods;
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
    String proxyClassName = buildClassName(targetClass);
    String packageName = getPackageName(targetClass);

    FieldSpec targetObjectField = FieldSpec.builder(TypeName.get(targetClass.asType()), "targetObject", PRIVATE, FINAL)
            .build();
    FieldSpec aspectField = FieldSpec.builder(TypeName.get(aspectToGenerate.getAspect()), "aspect", PRIVATE, FINAL)
            .build();

    Builder proxyClassBilder = TypeSpec.classBuilder(proxyClassName)
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
        proxyClassBilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameters(getParameters(constrctor))
                .addParameter(TypeName.get(targetClass.asType()), "targetObject")
                .addParameter(TypeName.get(aspectToGenerate.getAspect()), "aspect")
                // add super call even for default constructor in oder to aid debugging
                .addStatement(buildSuperCall(constrctor))
                .addStatement("$T.requireNonNull(targetObject, $S)", Objects.class, "targetObject")
                .addStatement("this.targetObject = targetObject")
                .addStatement("$T.requireNonNull(aspect, $S)", Objects.class, "aspect")
                .addStatement("this.aspect = aspect")
                .build());
      }
    }

    for (ExecutableElement method : this.getMethodsToImplement(targetClass)) {
      boolean isVoid = method.getReturnType().getKind() == TypeKind.VOID;

      com.squareup.javapoet.MethodSpec.Builder methodBuilder = MethodSpec.overriding(method);
      // TODO
      Element apectElement = this.processingEnv.getTypeUtils().asElement(aspectToGenerate.getAspect());
      List<ExecutableElement> beforeMethods = getBeforeMethods(asTypeElement(apectElement));
      for (ExecutableElement beforeMethod : beforeMethods) {
        Statement adviceCall = buildAdviceCall(beforeMethod, targetClass, method);
        methodBuilder.addStatement(adviceCall.getFormat(), adviceCall.getArguments());
      }

      methodBuilder.addStatement((isVoid ? "" : "return ") + buildDelegateCall("this.targetObject." + method.getSimpleName(), method));

      proxyClassBilder.addMethod(methodBuilder.build());
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

  private Statement buildAdviceCall(ExecutableElement adviceMethod, TypeElement targetClassElement, ExecutableElement joinpointElement) {
    StringBuilder buffer = new StringBuilder();
    List<Object> arguments = new ArrayList<>(adviceMethod.getParameters().size());
    buffer.append("this.aspect.");
    buffer.append(adviceMethod.getSimpleName());
    buffer.append('(');
        boolean first = true;
        for (VariableElement parameter: adviceMethod.getParameters()) {
          if (!first) {
            buffer.append(", ");
          }
          first = false;
          for (AnnotationMirror mirror : this.processingEnv.getElementUtils().getAllAnnotationMirrors(parameter)) {
            DeclaredType annotationType = mirror.getAnnotationType();
            if (isSameType(annotationType, this.evaluateType)) {
              ExecutableElement valueMethod = this.getValueMethod(this.evaluateElement);
              AnnotationValue annotationValue = mirror.getElementValues().get(valueMethod);
              String expression = asString(annotationValue);
              // TODO check
              buffer.append("$S");
              arguments.add(evaluate(expression, targetClassElement, joinpointElement));
            }
          }
        }
    buffer.append(')');
    return new Statement(buffer.toString(), arguments);
  }

  static final class Statement {

    private final String format;
    private final Object[] arguments;

    Statement(String format, List<Object> arguments) {
      this.format = format;
      this.arguments = arguments.toArray(new Object[0]);
    }

    String getFormat() {
      return this.format;
    }

    Object[] getArguments() {
      return this.arguments;
    }

  }

  private static String buildSuperCall(ExecutableElement constructor) {
    return buildDelegateCall("super", constructor);
  }

  private static String buildDelegateCall(String receiver, ExecutableElement method) {
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

  private String buildClassName(TypeElement targetClass) {
    String simpleName = getSimpleName(targetClass);
    return this.namingStrategy.deriveClassName(simpleName);
  }

  private String extractAspectClassName(AnnotationMirror adviseBy) {
    // @AdviseBy#value()
    TypeMirror aspectClass = extractAspectClassTypeMirror(adviseBy);
    // @AdviseBy#value() = Aspect.class
    DeclaredType declaredType = asDeclaredType(aspectClass);
    return getQualifiedName(declaredType.asElement());
  }

  private TypeMirror extractAspectClassTypeMirror(AnnotationMirror adviseBy) {
    // @AdviseBy#value()
    AnnotationValue annotationValue = adviseBy.getElementValues().get(this.adviseByValueMethod);
    return asTypeMirror(annotationValue);
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

  // generic JavaPoet stuff

  private static Iterable<ParameterSpec> getParameters(ExecutableElement executableElement) {
    return executableElement.getParameters().stream()
            .map(ParameterSpec::get)
            .collect(Collectors.toList());
  }

  // generic APT stuff

  private static boolean isOverriable(Element method) {
    return !containsAny(method.getModifiers(), NON_OVERRIDABLE);
  }

  private static <T> boolean containsAny(Set<T> set, Set<?> elements) {
    for (Object element : elements) {
      if (set.contains(element)) {
        return true;
      }
    }
    return false;
  }

  private String evaluate(String expression, TypeElement targetClassElement, ExecutableElement joinpointElement) {
    TargetClass targetClass = null;
    JoinPoint joinPoint = null;
    return this.evaluator.evaluate(expression, targetClass, joinPoint);
  }

  private List<ExecutableElement> getMethodsAnnotatedWith(TypeElement type, TypeElement annotation) {
    TypeMirror annotationType = annotation.asType();
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.processingEnv.getElementUtils().getAllMembers(type)) {
      if (member.getKind() == ElementKind.METHOD) {
        for (AnnotationMirror mirror : member.getAnnotationMirrors()) {
          if (this.isSameType(mirror.getAnnotationType(), annotationType)) {
            methods.add((ExecutableElement) member);
          }
        }
      }
    }
    return methods;
  }

  private String getPackageName(Element element) {
    PackageElement packageElement = this.processingEnv.getElementUtils().getPackageOf(element);
    return packageElement.getQualifiedName().toString();
  }


  private boolean isOverridableObjectMethod(ExecutableElement method) {
    Name methodName = method.getSimpleName();
    if (methodName.contentEquals("finalize")) {
      return method.getParameters().size() == 0;
    }
    if (methodName.contentEquals("toString")) {
      return method.getParameters().size() == 0;
    }
    if (methodName.contentEquals("clone")) {
      return method.getParameters().size() == 0;
    }
    if (methodName.contentEquals("hashCode")) {
      return method.getParameters().size() == 0;
    }
    if (methodName.contentEquals("equals")) {
      if (method.getParameters().size() != 1) {
        return false;
      }
      return this.isSameType(method.getParameters().get(0), this.objectType);
    }
    return false;
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
        if (this.isSameType(method.getParameters().get(0), this.longType)) {
          // java.lang.Object.wait(long)
          return true;
        }
      } else if (parameterCount == 2) {
        if (this.isSameType(method.getParameters().get(0), this.longType)
                && this.isSameType(method.getParameters().get(1), this.intType)) {
          // java.lang.Object.wait(long, int)
          return true;
        }
      }
    }
    return false;
  }

  private boolean isSameType(Element element, TypeMirror typeMirror) {
    return this.isSameType(element.asType(), typeMirror);
  }

  private boolean isSameType(TypeMirror t1, TypeMirror t2) {
    return this.processingEnv.getTypeUtils().isSameType(t1, t2);
  }

  final class ImplementableMethodExtractor extends SimpleElementVisitor8<Void, List<ExecutableElement>> {

    @Override
    public Void visitExecutable(ExecutableElement e, List<ExecutableElement> p) {
      if (e.getKind() == ElementKind.METHOD && isOverriable(e) && !isOverridableObjectMethod(e)) {
        p.add(e);
      }
      return null;
    }

  }

  static final class MethodContext {

    boolean needsReturnVariable() {
      return false;
    }

    String getReturnVariableName() {
      throw new IllegalStateException("not yet implemented");
    }

    boolean needsExectionTimeMillis() {
      return false;
    }

    String getExectionTimeMillisName() {
      throw new IllegalStateException("not yet implemented");
    }

    boolean needsExectionTimeNanos() {
      return false;
    }

    String getExectionTimeNanosName() {
      throw new IllegalStateException("not yet implemented");
    }

    boolean needsCatch() {
      return false;
    }

    boolean needsFinally() {
      return false;
    }

  }

}
