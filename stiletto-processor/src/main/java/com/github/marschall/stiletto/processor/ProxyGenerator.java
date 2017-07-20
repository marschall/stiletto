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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.github.marschall.stiletto.processor.el.Joinpoint;
import com.github.marschall.stiletto.processor.el.TargetClass;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
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

  // @Evaluate#value()
  private ExecutableElement evaluateValueMethod;

  // @Evaluate
  private TypeMirror evaluateType;

  // @TargetObject
  private TypeMirror targetObjectType;

  // @ReturnValue
  private TypeMirror returnValueType;

  // @Arguments
  private TypeMirror argumentsType;

  // @Joinpoint
  private TypeMirror joinpointType;

  // FIXME should all be type mirrors
  private TypeElement before;

  private TypeElement around;

  private TypeElement afterThrowing;

  private TypeElement afterReturning;

  private TypeElement afterFinally;

  private ExpressionEvaluator evaluator;

  private AptUtils aptUtils;

  private Elements elements;

  private Types types;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;
    this.addGenerated = true;

    this.elements = this.processingEnv.getElementUtils();
    this.types = this.processingEnv.getTypeUtils();
    TypeElement adviseBy = this.elements.getTypeElement(ADVISE_BY);
    this.before = this.elements.getTypeElement(BEFORE);
    this.around = this.elements.getTypeElement(AROUND);
    this.afterThrowing = this.elements.getTypeElement(AFTER_THROWING);
    this.afterReturning = this.elements.getTypeElement(AFTER_RETURNING);
    this.afterFinally = this.elements.getTypeElement(AFTER_FINALLY);
    this.adviseByValueMethod = getValueMethod(adviseBy);
    this.intType = types.getPrimitiveType(TypeKind.INT);
    this.longType = types.getPrimitiveType(TypeKind.LONG);
    this.objectType = this.processingEnv.getElementUtils().getTypeElement("java.lang.Object").asType();

    TypeElement evaluateElement = this.processingEnv.getElementUtils().getTypeElement("com.github.marschall.stiletto.api.injection.Evaluate");
    this.evaluateValueMethod = this.getValueMethod(evaluateElement);
    this.evaluateType = evaluateElement.asType();

    TypeElement targetObjectElement = this.processingEnv.getElementUtils().getTypeElement("com.github.marschall.stiletto.api.injection.TargetObject");
    this.targetObjectType = targetObjectElement.asType();

    TypeElement returnValueElement = this.processingEnv.getElementUtils().getTypeElement("com.github.marschall.stiletto.api.injection.ReturnValue");
    this.returnValueType = returnValueElement.asType();

    TypeElement argumentsElement = this.processingEnv.getElementUtils().getTypeElement("com.github.marschall.stiletto.api.injection.Arguments");
    this.argumentsType = argumentsElement.asType();

    TypeElement joinpointElement = this.processingEnv.getElementUtils().getTypeElement("com.github.marschall.stiletto.api.injection.Joinpoint");
    this.joinpointType = joinpointElement.asType();

    this.namingStrategy = s -> s + "_";
    this.evaluator = new ExpressionEvaluator();

    this.aptUtils = new AptUtils(this.types);
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
    TypeElement adviseByAllElement = this.elements.getTypeElement(ADVISE_BY_ALL);
    ExecutableElement adviseByAllValueMethod = getValueMethod(adviseByAllElement);

    // handle @AdviseByAll
    Set<ProxyToGenerate> toGenerate = new HashSet<>();
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByAllElement)) {
      if (!validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror adviseByAll : this.getAnnotationMirrorsOfType(processedClass, adviseByAllElement.asType())) { //@AdviseByAll
        AnnotationValue adviseByAllValue = adviseByAll.getElementValues().get(adviseByAllValueMethod); //@AdviseByAll
        for (AnnotationValue adviseByValue : this.aptUtils.asAnnotationValues(adviseByAllValue)) { //@AdviseBy
          AnnotationMirror adviseByMirror = this.aptUtils.asAnnotationMirror(adviseByValue); //@AdviseBy
          ProxyToGenerate proxyToGenerate = buildProxyToGenerate(processedClass, adviseByMirror);
          toGenerate.add(proxyToGenerate);
        }
      }
    }

    // handle @AdviseBy
    TypeElement adviseByElement = this.elements.getTypeElement(ADVISE_BY);
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
    for (AnnotationMirror annotationMirror : this.elements.getAllAnnotationMirrors(element)) {
      if (this.isSameType(type, annotationMirror.getAnnotationType())) {
        mirrors.add(annotationMirror);
      }
    }
    return mirrors;
  }

  private ProxyToGenerate buildProxyToGenerate(Element processedClass, AnnotationMirror adviseByMirror) {
    String processedClassName = this.aptUtils.getQualifiedName(processedClass);
    String aspectClassName = extractAspectClassName(adviseByMirror);
    TypeElement typeElement = this.aptUtils.asTypeElement(processedClass);
    return new ProxyToGenerate(processedClassName, aspectClassName, typeElement, extractAspectClassTypeMirror(adviseByMirror));
  }

  private boolean validateAnnoatedClass(Element element) {
    TypeElement typeElement = this.aptUtils.asTypeElement(element);
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
    for (Element member : this.elements.getAllMembers(typeElement)) {
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

  private List<ExecutableElement> getAroundMethods(TypeElement aspect) {
    return getMethodsAnnotatedWith(aspect, this.around);
  }

  private List<ExecutableElement> getAfterThrowingMethods(TypeElement aspect) {
    return getMethodsAnnotatedWith(aspect, this.afterThrowing);
  }

  private List<ExecutableElement> getMethodsToImplement(TypeElement targetClass) {
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.elements.getAllMembers(targetClass)) {
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

  private void generateProxy(ProxyToGenerate proxyToGenerate) throws IOException {
    TypeElement targetClass = proxyToGenerate.getTargetClassElement();
    String proxyClassName = buildClassName(targetClass);
    String packageName = getPackageName(targetClass);

    FieldSpec targetObjectField = FieldSpec.builder(TypeName.get(targetClass.asType()), "targetObject", PRIVATE, FINAL)
            .build();
    FieldSpec aspectField = FieldSpec.builder(TypeName.get(proxyToGenerate.getAspect()), "aspect", PRIVATE, FINAL)
            .build();

    Builder proxyClassBilder = TypeSpec.classBuilder(proxyClassName)
            .addModifiers(PUBLIC, FINAL)
            .addOriginatingElement(targetClass)
            .addField(targetObjectField)
            .addField(aspectField);

    addSuperType(proxyToGenerate, proxyClassBilder);

    addClassHeader(proxyToGenerate, proxyClassBilder);

    addConstructors(proxyToGenerate, proxyClassBilder);

    TargetObjectContext targetObjectContext = new TargetObjectContext();
    List<MethodConstant> methodConstants = new ArrayList<>();
    for (ExecutableElement method : this.getMethodsToImplement(targetClass)) {
      // TODO
      Element apectElement = this.types.asElement(proxyToGenerate.getAspect());
      TypeElement aspectType = this.aptUtils.asTypeElement(apectElement);

      List<ExecutableElement> afterReturningMethods = getAfterReturningMethods(aspectType);
      List<ExecutableElement> afterFinallyMethods = getAfterFinallyMethods(aspectType);
      List<ExecutableElement> aroundMethods = getAroundMethods(aspectType);
      List<ExecutableElement> beforeMethods = getBeforeMethods(aspectType);
      boolean isVoid = method.getReturnType().getKind() == TypeKind.VOID;
      boolean needsReturnValue = !isVoid
              && (!afterReturningMethods.isEmpty()
                      || !afterFinallyMethods.isEmpty()
                      || !aroundMethods.isEmpty());

      JoinpointContext joinpointContext;
      if (needsReturnValue) {
        joinpointContext = new JoinpointContext(targetClass, method, targetObjectContext, "returnValue");
      } else {
        joinpointContext = new JoinpointContext(targetClass, method, targetObjectContext);
      }

      com.squareup.javapoet.MethodSpec.Builder methodBuilder = MethodSpec.overriding(method);

      for (ExecutableElement beforeMethod : beforeMethods) {
        AdviceContext adviceContext = new AdviceContext(beforeMethod, joinpointContext);
        Statement adviceCall = buildAdviceCall(adviceContext);
        methodBuilder.addStatement(adviceCall.getFormat(), adviceCall.getArguments());
      }

      if (needsReturnValue) {
        String returnVariableName = joinpointContext.getReturnVariableName();
        TypeMirror returnType = method.getReturnType();
        String methodName = method.getSimpleName().toString();
        methodBuilder.addStatement("$T $N = " + buildDelegateCall("this.targetObject." + methodName, method), returnType, returnVariableName);
      }


      for (ExecutableElement afterReturningMethod : afterReturningMethods) {
        AdviceContext adviceContext = new AdviceContext(afterReturningMethod, joinpointContext);
        Statement adviceCall = buildAdviceCall(adviceContext);
        methodBuilder.addStatement(adviceCall.getFormat(), adviceCall.getArguments());
      }

      if (needsReturnValue) {
        methodBuilder.addStatement("return " + joinpointContext.getReturnVariableName());
      } else if (isVoid) {
        methodBuilder.addStatement(buildDelegateCall("this.targetObject." + method.getSimpleName(), method));
      } else {
        methodBuilder.addStatement("return " + buildDelegateCall("this.targetObject." + method.getSimpleName(), method));
      }

      proxyClassBilder.addMethod(methodBuilder.build());

      if (joinpointContext.hasMethodConstantName()) {
        methodConstants.add(new MethodConstant(joinpointContext.getMethodConstantName(), method));
      }
    }

    addMethodConstants(proxyClassBilder, proxyToGenerate, methodConstants);

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

  private void addSuperType(ProxyToGenerate proxyToGenerate, Builder proxyClassBilder) {
    TypeElement targetClass = proxyToGenerate.getTargetClassElement();
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
  }

  private void addClassHeader(ProxyToGenerate proxyToGenerate, Builder proxyClassBilder) {
    TypeElement targetClass = proxyToGenerate.getTargetClassElement();
    proxyClassBilder.addJavadoc("Proxy class for {@link $T} being advised by {@link $T}.\n",
            TypeName.get(targetClass.asType()),
            TypeName.get(proxyToGenerate.getAspect()));
    if (this.addGenerated) {
      proxyClassBilder.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.annotation", "Generated"))
              .addMember("value", "$S", this.getClass().getName())
              .build());
    }
  }

  private void addConstructors(ProxyToGenerate proxyToGenerate, Builder proxyClassBilder) {
    TypeElement targetClass = proxyToGenerate.getTargetClassElement();
    List<ExecutableElement> nonPrivateConstrctors = this.aptUtils.getNonPrivateConstrctors(targetClass);
    if (nonPrivateConstrctors.isEmpty()) {
      proxyClassBilder.addMethod(MethodSpec.constructorBuilder()
              .addModifiers(PUBLIC)
              .addParameter(TypeName.get(targetClass.asType()), "targetObject")
              .addParameter(TypeName.get(proxyToGenerate.getAspect()), "aspect")
              .addStatement("$T.requireNonNull($N, $S)", Objects.class, "targetObject", "targetObject")
              .addStatement("this.$N = $N", "targetObject", "targetObject")
              .addStatement("$T.requireNonNull($N, $S)", Objects.class, "aspect", "aspect")
              .addStatement("this.$N = $N", "aspect", "aspect")
              .build());
    } else {
      for (ExecutableElement constrctor : nonPrivateConstrctors) {
        proxyClassBilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameters(getParameters(constrctor))
                .addParameter(TypeName.get(targetClass.asType()), "targetObject")
                .addParameter(TypeName.get(proxyToGenerate.getAspect()), "aspect")
                // add super call even for default constructor in oder to aid debugging
                .addStatement(buildSuperCall(constrctor))
                .addStatement("$T.requireNonNull($N, $S)", Objects.class, "targetObject", "targetObject")
                .addStatement("this.$N = $N", "targetObject", "targetObject")
                .addStatement("$T.requireNonNull($N, $S)", Objects.class, "aspect", "aspect")
                .addStatement("this.$N = $N", "aspect", "aspect")
                .build());
      }
    }
  }

  private void addMethodConstants(Builder proxyClassBilder, ProxyToGenerate proxyToGenerate, List<MethodConstant> methodConstants) {
    if (methodConstants.isEmpty()) {
      return;
    }

    com.squareup.javapoet.CodeBlock.Builder initializerBuilder = CodeBlock.builder()
            .beginControlFlow("try");

    for (MethodConstant methodConstant : methodConstants) {
      ExecutableElement method = methodConstant.getMethod();
      proxyClassBilder.addField(FieldSpec.builder(
              java.lang.reflect.Method.class, methodConstant.getName(), PRIVATE, STATIC, FINAL)
              .addJavadoc("{@link " + method.getEnclosingElement() + "#" + this.aptUtils.getSignature(method) + "}\n")
              .build());
      addMethodConstant(initializerBuilder, proxyToGenerate, methodConstant);
    }

    initializerBuilder.nextControlFlow("catch ($T e)", ClassName.get("java.lang", "NoSuchMethodException"))
      .addStatement("throw new $T(e.getMessage())", ClassName.get("java.lang", "NoSuchMethodError"))
      .endControlFlow();

    proxyClassBilder.addStaticBlock(initializerBuilder.build());
  }

  private void addMethodConstant(com.squareup.javapoet.CodeBlock.Builder initializerBuilder, ProxyToGenerate proxyToGenerate, MethodConstant methodConstant) {
    ExecutableElement method = methodConstant.getMethod();
    List<? extends VariableElement> parameters = method.getParameters();
    String constantName = methodConstant.getName();
    TypeElement targetClass = proxyToGenerate.getTargetClassElement();
    Name methodName = method.getSimpleName();
    if (parameters.isEmpty()) {
      initializerBuilder.addStatement("$N = $T.class.getMethod($S)", constantName, targetClass, methodName);
    } else {
      List<Object> formatArguments = new ArrayList<>(3 + parameters.size());
      formatArguments.add(constantName);
      formatArguments.add(targetClass);
      formatArguments.add(methodName);
      StringBuilder buffer = new StringBuilder();
      buffer.append("$N = $T.class.getMethod($S, new Class[]{");
      boolean first = true;
      for (VariableElement parameter : parameters) {
        if (!first) {
          buffer.append(", ");
        }
        buffer.append("$T.class");
        formatArguments.add(TypeName.get(this.types.erasure(parameter.asType())));
      }
      buffer.append("})");
      initializerBuilder.addStatement(buffer.toString(), formatArguments.toArray());
    }
  }

  private Statement buildAdviceCall(AdviceContext adviceContext) {
    StringBuilder buffer = new StringBuilder();
    ExecutableElement adviceMethod = adviceContext.getAdviceMethod();
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
      Argument argument = buildAdviceCallArgument(parameter, adviceContext);
      if (argument != null) {
        buffer.append(argument.getValue());
        Object formatParameter = argument.getFormatParameter();
        if (formatParameter != null) {
          arguments.add(formatParameter);
        }
      }

    }
    buffer.append(')');
    return new Statement(buffer.toString(), arguments);
  }

  private Argument buildAdviceCallArgument(VariableElement adviceParameter, AdviceContext adviceContext) {
    Argument argument = null;
    for (AnnotationMirror mirror : this.elements.getAllAnnotationMirrors(adviceParameter)) {
      DeclaredType annotationType = mirror.getAnnotationType();
      // unfortunately due to javax.lang.model.type.TypeMirror.equals(Object)
      // we can not use a map here
      if (isSameType(annotationType, this.evaluateType)) { // @Evaluate
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = buildEvaluateArgument(adviceContext, mirror);
      } else if (isSameType(annotationType, this.targetObjectType)) { // @TargetObject
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = buildTargetObjectArgument();
      } else if (isSameType(annotationType, this.argumentsType)) { // @Arguments
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = buildArgumentsArgument(adviceContext);
      } else if (isSameType(annotationType, this.returnValueType)) { // @ReturnValue
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }
        if (hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturning)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "@ReturnValue is only available for @afterReturning");
          return argument;
        }

        argument = buildReturnValueArgument(adviceContext);
      } else if (isSameType(annotationType, this.joinpointType)) { // @Joinpoint
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = buildJoinpointArgument(adviceContext);
      }
    }

    if (argument == null) {
      this.processingEnv.getMessager().printMessage(Kind.ERROR, "no injection annotation present on: " + adviceParameter);
    }
    return argument;
  }

  private Argument buildJoinpointArgument(AdviceContext adviceContext) {
    JoinpointContext joinpointContext = adviceContext.getJoinpointContext();
    String constantName = joinpointContext.getMethodConstantName();
    return new Argument(constantName);
  }

  private Argument buildReturnValueArgument(AdviceContext adviceContext) {
    return new Argument(adviceContext.getJoinpointContext().getReturnVariableName());
  }

  private Argument buildArgumentsArgument(AdviceContext adviceContext) {
    List<? extends VariableElement> parameters = adviceContext.getJoinpointContext().getJoinpointElement().getParameters();
    if (parameters.isEmpty()) {
      // consistent with InvocationHandler
      return new Argument("null");
    } else {
      StringBuilder buffer = new StringBuilder();
      buffer.append("new Object[]{");
      boolean first = true;
      for (VariableElement joinpointParameter : parameters) {
        if (!first) {
          buffer.append(", ");
        }
        first = false;
        buffer.append(joinpointParameter.getSimpleName());
      }
      buffer.append("}");
      return new Argument(buffer.toString());
    }
  }

  private Argument buildTargetObjectArgument() {
    return new Argument("this.targetObject");
  }

  private Argument buildEvaluateArgument(AdviceContext adviceContext, AnnotationMirror mirror) {
    AnnotationValue annotationValue = mirror.getElementValues().get(evaluateValueMethod);
    String expression = this.aptUtils.asString(annotationValue);
    JoinpointContext joinpointContext = adviceContext.getJoinpointContext();

    return new Argument("$S", evaluate(expression, joinpointContext.getTargetClassElement(), joinpointContext.getJoinpointElement()));
  }

  private boolean hasAnnotationMirror(ExecutableElement adviceMethod, TypeElement typeElement) {
    for (AnnotationMirror mirror : this.elements.getAllAnnotationMirrors(typeElement)) {
      TypeMirror expectedAnnotationType = typeElement.asType();
      if (this.types.isSameType(mirror.getAnnotationType(), expectedAnnotationType)) {
        return true;
      }
    }
    return false;
  }

  static final class MethodConstant {

    private final String name;
    private final ExecutableElement method;

    MethodConstant(String name, ExecutableElement method) {
      this.name = name;
      this.method = method;
    }

    String getName() {
      return this.name;
    }

    ExecutableElement getMethod() {
      return this.method;
    }

  }

  static final class Argument {

    private final String value;

    private final Object formatParameter;

    Argument(String value, Object formatParameter) {
      Objects.requireNonNull(value);
      this.value = value;
      this.formatParameter = formatParameter;
    }

    Argument(String value) {
      Objects.requireNonNull(value);
      this.value = value;
      this.formatParameter = null;
    }

    String getValue() {
      return this.value;
    }

    Object getFormatParameter() {
      return this.formatParameter;
    }

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
    String simpleName = this.aptUtils.getSimpleName(targetClass);
    return this.namingStrategy.deriveClassName(simpleName);
  }

  private String extractAspectClassName(AnnotationMirror adviseBy) {
    // @AdviseBy#value()
    TypeMirror aspectClass = extractAspectClassTypeMirror(adviseBy);
    // @AdviseBy#value() = Aspect.class
    DeclaredType declaredType = this.aptUtils.asDeclaredType(aspectClass);
    return this.aptUtils.getQualifiedName(declaredType.asElement());
  }

  private TypeMirror extractAspectClassTypeMirror(AnnotationMirror adviseBy) {
    // @AdviseBy#value()
    AnnotationValue annotationValue = adviseBy.getElementValues().get(this.adviseByValueMethod);
    return this.aptUtils.asTypeMirror(annotationValue);
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
    for (Element element : this.elements.getAllMembers(typeElement)) {
      if (element.getKind() == ElementKind.METHOD && isOverriable(element)) {
        if (element.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
          // TODO check if method overrides any interface method
          //          ExecutableElement interfaceMethod = null;
          //          if (!this.processingEnv.getElementUtils().overrides((ExecutableElement) element, interfaceMethod, typeElement)) {
          //            return false;
          //          }
        }
      }
    }
    // javax.lang.model.util.Elements.overrides(ExecutableElement, ExecutableElement, TypeElement)
    return true;
  }

  // generic JavaPoet stuff

  private static Iterable<ParameterSpec> getParameters(ExecutableElement executableElement) {
    List<? extends VariableElement> parameters = executableElement.getParameters();
    List<ParameterSpec> result = new ArrayList<>();
    for (VariableElement parameter : parameters) {
      result.add(ParameterSpec.get(parameter));
    }
    return result;
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
    TargetClass targetClass = new TargetClass(targetClassElement.getQualifiedName().toString());
    Joinpoint joinpoint = new Joinpoint(joinpointElement.getSimpleName().toString(), this.aptUtils.getSignature(joinpointElement));
    return this.evaluator.evaluate(expression, targetClass, joinpoint);
  }

  private List<ExecutableElement> getMethodsAnnotatedWith(TypeElement type, TypeElement annotation) {
    TypeMirror annotationType = annotation.asType();
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.elements.getAllMembers(type)) {
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
    PackageElement packageElement = this.elements.getPackageOf(element);
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
    return this.types.isSameType(t1, t2);
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

  static final class TargetObjectContext {

    private Set<String> methodConstants;

    private Map<String, Integer> countsByName;

    TargetObjectContext() {
      this.methodConstants = new HashSet<>();
      this.countsByName = new HashMap<>();
    }

    String generateMethodConstantName(String name) {
      int existingCount = this.countsByName.getOrDefault(name, 0);
      String generatedName = "M_" + name + '_' + existingCount;
      if (!this.methodConstants.add(generatedName)) {
        throw new IllegalStateException("name " + generatedName + " already present");
      }
      this.countsByName.merge(name, 1, (existing, increment) -> existing + increment);
      return generatedName;
    }

  }

  static final class JoinpointContext {

    private final TypeElement targetClassElement;
    private final ExecutableElement joinpointElement;
    private final TargetObjectContext targetObjectContext;
    private final String returnVariableName;
    private String methodConstantName;

    JoinpointContext(TypeElement targetClassElement, ExecutableElement joinpointElement, TargetObjectContext targetObjectContext) {
      this(targetClassElement, joinpointElement, targetObjectContext, null);
    }

    JoinpointContext(TypeElement targetClassElement, ExecutableElement joinpointElement, TargetObjectContext targetObjectContext, String returnVariableName) {
      this.targetClassElement = targetClassElement;
      this.joinpointElement = joinpointElement;
      this.targetObjectContext = targetObjectContext;
      this.returnVariableName = returnVariableName;
    }

    String getMethodConstantName() {
      if (this.methodConstantName == null) {
        String joinpointMethodName = this.joinpointElement.getSimpleName().toString();
        this.methodConstantName = this.targetObjectContext.generateMethodConstantName(joinpointMethodName);
      }
      return this.methodConstantName;
    }

    boolean hasMethodConstantName() {
      return this.methodConstantName != null;
    }

    TypeElement getTargetClassElement() {
      return this.targetClassElement;
    }

    ExecutableElement getJoinpointElement() {
      return this.joinpointElement;
    }

    String getReturnVariableName() {
      if (this.returnVariableName == null) {
        throw new IllegalStateException("no return variable present");
      }
      return this.returnVariableName;
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

  static final class AdviceContext {

    private final ExecutableElement adviceMethod;
    private final JoinpointContext joinpointContext;

    AdviceContext(ExecutableElement adviceMethod, JoinpointContext joinpointContext) {
      this.adviceMethod = adviceMethod;
      this.joinpointContext = joinpointContext;
    }

    JoinpointContext getJoinpointContext() {
      return this.joinpointContext;
    }

    ExecutableElement getAdviceMethod() {
      return this.adviceMethod;
    }

  }

}
