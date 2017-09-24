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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  private static final String EXECUTION_TIME_NANOS = "com.github.marschall.stiletto.api.injection.ExecutionTimeNanos";

  private static final String EXECUTION_TIME_MILLIS = "com.github.marschall.stiletto.api.injection.ExecutionTimeMillis";

  private static final String JOINPOINT = "com.github.marschall.stiletto.api.injection.Joinpoint";

  private static final String ARGUMENTS = "com.github.marschall.stiletto.api.injection.Arguments";

  private static final String RETURN_VALUE = "com.github.marschall.stiletto.api.injection.ReturnValue";

  private static final String TARGET_OBJECT = "com.github.marschall.stiletto.api.injection.TargetObject";

  private static final String EVALUATE = "com.github.marschall.stiletto.api.injection.Evaluate";

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

  // @ExecutionTimeMillis
  private TypeMirror executionTimeMillisType;

  // @ExecutionTimeNanos
  private TypeMirror executionTimeNanosType;

  // @AfterReturning
  private TypeMirror afterReturningType;

  private TypeMirror afterFinallyType;

  private TypeMirror afterThrowingType;

  private TypeMirror aroundType;

  private TypeMirror beforeType;

  private ExpressionEvaluator evaluator;

  private AptUtils aptUtils;

  private Elements elements;

  private Types types;

  private boolean addSpringSupport;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;
    this.elements = this.processingEnv.getElementUtils();
    this.types = this.processingEnv.getTypeUtils();
    this.aptUtils = new AptUtils(this.types);
    this.addGenerated = true;
    this.addSpringSupport = processingEnv.getOptions().getOrDefault("stiletto.spring", "false").equals("true");

    TypeElement adviseBy = this.getTypeElement(ADVISE_BY);
    TypeElement before = this.getTypeElement(BEFORE);
    this.beforeType = before.asType();
    TypeElement around = this.getTypeElement(AROUND);
    this.aroundType = around.asType();
    TypeElement afterThrowing = this.getTypeElement(AFTER_THROWING);
    this.afterThrowingType = afterThrowing.asType();
    TypeElement afterReturning = this.getTypeElement(AFTER_RETURNING);
    this.afterReturningType = afterReturning.asType();
    TypeElement afterFinally = this.getTypeElement(AFTER_FINALLY);
    this.afterFinallyType = afterFinally.asType();
    this.adviseByValueMethod = this.getValueMethod(adviseBy);
    this.intType = this.types.getPrimitiveType(TypeKind.INT);
    this.longType = this.types.getPrimitiveType(TypeKind.LONG);
    this.objectType = this.getTypeElement("java.lang.Object").asType();

    TypeElement evaluateElement = this.getTypeElement(EVALUATE);
    this.evaluateValueMethod = this.getValueMethod(evaluateElement);
    this.evaluateType = evaluateElement.asType();

    TypeElement targetObjectElement = this.getTypeElement(TARGET_OBJECT);
    this.targetObjectType = targetObjectElement.asType();

    TypeElement returnValueElement = this.getTypeElement(RETURN_VALUE);
    this.returnValueType = returnValueElement.asType();

    TypeElement argumentsElement = this.getTypeElement(ARGUMENTS);
    this.argumentsType = argumentsElement.asType();

    TypeElement joinpointElement = this.getTypeElement(JOINPOINT);
    this.joinpointType = joinpointElement.asType();

    TypeElement executionTimeMillisElement = this.getTypeElement(EXECUTION_TIME_MILLIS);
    this.executionTimeMillisType = executionTimeMillisElement.asType();

    TypeElement executionTimeNanosElement = this.getTypeElement(EXECUTION_TIME_NANOS);
    this.executionTimeNanosType = executionTimeNanosElement.asType();

    if (this.addSpringSupport) {
      // look like a cglib proxy to spring
      this.namingStrategy = s -> s + "$$_";
    } else {
      this.namingStrategy = s -> s + "_";
    }
    this.evaluator = new ExpressionEvaluator();
  }

  private TypeElement getTypeElement(CharSequence name) {
    return this.processingEnv.getElementUtils().getTypeElement(name);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<ProxyToGenerate> toGenerate = this.extractAspectsToGenerate(roundEnv);
    this.generateProxies(toGenerate);
    return true;
  }

  private void generateProxies(Set<ProxyToGenerate> toGenerate) {
    for (ProxyToGenerate proxyToGenerate : toGenerate) {
      this.generateProxyProtected(proxyToGenerate);
    }
  }

  private Set<ProxyToGenerate> extractAspectsToGenerate(RoundEnvironment roundEnv) {
    TypeElement adviseByAllElement = this.getTypeElement(ADVISE_BY_ALL);
    ExecutableElement adviseByAllValueMethod = this.getValueMethod(adviseByAllElement);

    // handle @AdviseByAll
    Set<ProxyToGenerate> toGenerate = new HashSet<>();
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByAllElement)) {
      if (!this.validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror adviseByAll : this.getAnnotationMirrorsOfType(processedClass, adviseByAllElement.asType())) { //@AdviseByAll
        AnnotationValue adviseByAllValue = adviseByAll.getElementValues().get(adviseByAllValueMethod); //@AdviseByAll
        for (AnnotationValue adviseByValue : this.aptUtils.asAnnotationValues(adviseByAllValue)) { //@AdviseBy
          AnnotationMirror adviseByMirror = this.aptUtils.asAnnotationMirror(adviseByValue); //@AdviseBy
          ProxyToGenerate proxyToGenerate = this.buildProxyToGenerate(processedClass, adviseByMirror);
          toGenerate.add(proxyToGenerate);
        }
      }
    }

    // handle @AdviseBy
    TypeElement adviseByElement = this.getTypeElement(ADVISE_BY);
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(adviseByElement)) {
      if (!this.validateAnnoatedClass(processedClass)) {
        continue;
      }
      for (AnnotationMirror adviseByMirror : this.getAnnotationMirrorsOfType(processedClass, adviseByElement.asType())) { // @AdviseBy
        ProxyToGenerate proxyToGenerate = this.buildProxyToGenerate(processedClass, adviseByMirror);
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
    String aspectClassName = this.extractAspectClassName(adviseByMirror);
    TypeElement typeElement = this.aptUtils.asTypeElement(processedClass);
    return new ProxyToGenerate(processedClassName, aspectClassName, typeElement, this.extractAspectClassTypeMirror(adviseByMirror));
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
          ExecutableElement method = this.aptUtils.asExecutableElement(member);
          if (!this.isFinalObjectMethod(method)) {
            messager.printMessage(Kind.ERROR, "final methods can not be proxied", typeElement);
            valid = false;
          }
        }
      }
    }


    return valid;
  }

  private List<ExecutableElement> getBeforeMethods(TypeElement aspect) {
    return this.getMethodsAnnotatedWith(aspect, this.beforeType);
  }

  private List<ExecutableElement> getAfterReturningMethods(TypeElement aspect) {
    return this.getMethodsAnnotatedWith(aspect, this.afterReturningType);
  }

  private List<ExecutableElement> getAfterFinallyMethods(TypeElement aspect) {
    return this.getMethodsAnnotatedWith(aspect, this.afterFinallyType);
  }

  private List<ExecutableElement> getAroundMethods(TypeElement aspect) {
    return this.getMethodsAnnotatedWith(aspect, this.aroundType);
  }

  private List<ExecutableElement> getAfterThrowingMethods(TypeElement aspect) {
    return this.getMethodsAnnotatedWith(aspect, this.afterThrowingType);
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
    String proxyClassName = this.buildClassName(targetClass);
    String packageName = this.getPackageName(targetClass);

    FieldSpec targetObjectField = FieldSpec.builder(TypeName.get(targetClass.asType()), "targetObject", PRIVATE, FINAL)
            .build();
    FieldSpec aspectField = FieldSpec.builder(TypeName.get(proxyToGenerate.getAspect()), "aspect", PRIVATE, FINAL)
            .build();

    Builder proxyClassBilder = TypeSpec.classBuilder(proxyClassName)
            .addModifiers(PUBLIC, FINAL)
            .addOriginatingElement(targetClass)
            .addField(targetObjectField)
            .addField(aspectField);

    this.addSuperType(proxyToGenerate, proxyClassBilder);

    this.addClassHeader(proxyToGenerate, proxyClassBilder);

    this.addConstructors(proxyToGenerate, proxyClassBilder);

    TargetObjectContext targetObjectContext = new TargetObjectContext();
    List<MethodConstant> methodConstants = new ArrayList<>();
    for (ExecutableElement method : this.getMethodsToImplement(targetClass)) {
      JoinpointContext joinpointContext = this.implementMethod(proxyToGenerate, proxyClassBilder, targetObjectContext, method);

      if (joinpointContext.hasMethodConstantName()) {
        methodConstants.add(new MethodConstant(joinpointContext.getMethodConstantName(), method));
      }
    }

    this.addMethodConstants(proxyClassBilder, proxyToGenerate, methodConstants);

    TypeSpec proxyClass = proxyClassBilder.build();

    JavaFile javaFile = JavaFile.builder(packageName, proxyClass)
            .build();


    Filer filer = this.processingEnv.getFiler();
    String fullyQualified = this.getFullyQualifiedProxyClassName(packageName, proxyClassName);
    JavaFileObject javaFileObject = filer.createSourceFile(fullyQualified, targetClass);
    try (Writer writer = javaFileObject.openWriter()) {
      javaFile.writeTo(writer);
    }

  }

  private LocalVariableContext buildLocalVariables(ExecutableElement joinpointElement, AdviceMethods adviceMethods) {
    boolean needsReturnValue;
    if (isVoid(joinpointElement)) {
      needsReturnValue = false;
    } else {
      needsReturnValue = this.doMethodsNeedReturnValue(adviceMethods);
    }
    Set<String> parameterNames = getParameterNames(joinpointElement);
    String returnVariableName = needsReturnValue ? makeUnique("returnValue", parameterNames) : null;
    boolean hasExectionTimeMillis = this.hasParameterWithAnnotation(adviceMethods.getAfterReturningMethods(), this.executionTimeMillisType);
    boolean hasExectionTimeNanos = this.hasParameterWithAnnotation(adviceMethods.getAfterReturningMethods(), this.executionTimeNanosType);

    String exectionTimeMillisStartName = hasExectionTimeMillis ? makeUnique("exectionTimeMillisStart", parameterNames) : null;
    String exectionTimeMillisName = hasExectionTimeMillis ? makeUnique("exectionTimeMillis", parameterNames) : null;
    String exectionTimeNanosStartName = hasExectionTimeNanos ? makeUnique("exectionTimeNanosStart", parameterNames) : null;
    String exectionTimeNanosName = hasExectionTimeNanos ? makeUnique("exectionTimeNanos", parameterNames) : null;
    return new LocalVariableContext(returnVariableName,
            exectionTimeMillisStartName, exectionTimeMillisName,
            exectionTimeNanosStartName, exectionTimeNanosName);
  }

  private static String makeUnique(String s, Set<String> set) {
    String unique = s;
    while (set.contains(unique)) {
      unique = unique + '_';
    }
    return unique;
  }

  private JoinpointContext implementMethod(ProxyToGenerate proxyToGenerate,
          Builder proxyClassBilder, TargetObjectContext targetObjectContext, ExecutableElement joinpointElement) {

    TypeElement targetClass = proxyToGenerate.getTargetClassElement();

    AdviceMethods adviceMethods = this.getAdviceMethods(proxyToGenerate);
    LocalVariableContext localVariables = this.buildLocalVariables(joinpointElement, adviceMethods);

    com.squareup.javapoet.MethodSpec.Builder methodBuilder = MethodSpec.overriding(joinpointElement);
    JoinpointContext joinpointContext = new JoinpointContext(targetClass, joinpointElement, targetObjectContext, localVariables, methodBuilder);
    // @Before
    this.addBeforeMethods(adviceMethods, joinpointContext);

    // millis and nanos
    this.recordStartTime(joinpointContext);

    if (localVariables.hasReturnValueVariable()) {
      // returnVariableName = this.targetObject.joinpoint();
      String returnVariableName = joinpointContext.getLocalVariables().getReturnVariableName();
      TypeMirror returnType = joinpointElement.getReturnType();
      String methodName = joinpointElement.getSimpleName().toString();
      if (adviceMethods.getAroundMethods().isEmpty()) {
        // returnVariableName = this.targetObject.joinpoint();
        methodBuilder.addStatement("$T $N = this.$N." + buildDelegateCall(methodName, joinpointElement), returnType, returnVariableName, "targetObject");
      } else {
        ExecutableElement aroundMethod = adviceMethods.getAroundMethods().get(0);

        // returnVariableName = this.aspect.joinpoint(new ActualMethodCall() {
        //     @Override
        //     public R invoke() {
        //       return TargetClass.this.joinpoint();
        //     }
        // });
        AdviceContext adviceContext = new AdviceContext(aroundMethod, joinpointContext);
        Statement buildAdviceCall = this.buildAdviceCall(adviceContext);
        methodBuilder.addStatement("$T $N = this.$N" + buildDelegateCall(methodName, joinpointElement), returnType, returnVariableName, "aspect");
      }
    }

    // millis and nanos

    this.recordEndTime(joinpointContext);
    // @AfterReturningMethod
    this.addAfterReturningMethods(adviceMethods, joinpointContext);

    // return
    if (localVariables.hasReturnValueVariable()) {
      // return returnVariableName;
      methodBuilder.addStatement("return " + joinpointContext.getLocalVariables().getReturnVariableName());
    } else {
      String delegateCall = buildDelegateCall("this.targetObject." + joinpointElement.getSimpleName(), joinpointElement);
      if (joinpointContext.isVoid()) {
        // this.targetObject.joinpoint();
        methodBuilder.addStatement(delegateCall);
      } else {
        // return this.targetObject.joinpoint();
        methodBuilder.addStatement("return " + delegateCall);
      }
    }

    proxyClassBilder.addMethod(methodBuilder.build());
    return joinpointContext;
  }

  private void recordEndTime(JoinpointContext joinpointContext) {
    LocalVariableContext localVariables = joinpointContext.getLocalVariables();
    if (localVariables.hasExectionTimeMillis()) {
      String startName = localVariables.getExectionTimeMillisStartName();
      String variableName = localVariables.getExectionTimeMillisName();
      joinpointContext.getMethodBuilder()
        .addStatement("$T $N = $T.currentTimeMillis() - $N", long.class, variableName, java.lang.System.class, startName);
    }
    if (localVariables.hasExectionTimeNanos()) {
      String startName = localVariables.getExectionTimeNanosStartName();
      String variableName = localVariables.getExectionTimeNanosName();
      joinpointContext.getMethodBuilder()
        .addStatement("$T $N = $T.nanoTime() - $N", long.class, variableName, java.lang.System.class, startName);
    }
  }

  private void recordStartTime(JoinpointContext joinpointContext) {
    LocalVariableContext localVariables = joinpointContext.getLocalVariables();
    if (localVariables.hasExectionTimeMillis()) {
      String variableName = localVariables.getExectionTimeMillisStartName();
      joinpointContext.getMethodBuilder()
        .addStatement("$T $N = $T.currentTimeMillis()", long.class, variableName, java.lang.System.class);
    }
    if (localVariables.hasExectionTimeNanos()) {
      String variableName = localVariables.getExectionTimeNanosStartName();
      joinpointContext.getMethodBuilder()
        .addStatement("$T $N = $T.nanoTime()", long.class, variableName, java.lang.System.class);
    }
  }

  private void addAfterReturningMethods(AdviceMethods adviceMethods, JoinpointContext joinpointContext) {
    for (ExecutableElement afterReturningMethod : adviceMethods.getAfterReturningMethods()) {
      AdviceContext adviceContext = new AdviceContext(afterReturningMethod, joinpointContext);
      Statement adviceCall = this.buildAdviceCall(adviceContext);
      joinpointContext.getMethodBuilder().addStatement(adviceCall.getFormat(), adviceCall.getArguments());
    }
  }

  private void addBeforeMethods(AdviceMethods adviceMethods, JoinpointContext joinpointContext) {
    for (ExecutableElement beforeMethod : adviceMethods.getBeforeMethods()) {
      AdviceContext adviceContext = new AdviceContext(beforeMethod, joinpointContext);
      Statement adviceCall = this.buildAdviceCall(adviceContext);
      joinpointContext.getMethodBuilder().addStatement(adviceCall.getFormat(), adviceCall.getArguments());
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

    } else if (this.isSubclassingRequired(targetClass)) {
      proxyClassBilder.superclass(TypeName.get(targetClass.asType()));
    } else {
      for (TypeMirror superinterface : this.collectSuperInterfaces(targetClass)) {
        proxyClassBilder.addSuperinterface(TypeName.get(superinterface));
      }
      List<? extends TypeParameterElement> typeParameters = targetClass.getTypeParameters();
      if (!typeParameters.isEmpty()) {
        for (TypeParameterElement typeParameter : typeParameters) {
          proxyClassBilder.addTypeVariable(TypeVariableName.get(typeParameter));
        }
      }
    }
  }

  private List<TypeMirror> collectSuperInterfaces(TypeElement typeElement) {
    List<TypeMirror> interfaces = new ArrayList<>(2);
    Set<String> alreadyAdded = new HashSet<>(4);

    TypeElement clazz = typeElement;
    while (!this.isSameType(clazz, this.objectType)) {
      for (TypeMirror superinterface : clazz.getInterfaces()) {
        String qualifiedName = this.aptUtils.getQualifiedName(this.types.asElement(superinterface));
        if (alreadyAdded.add(qualifiedName)) {
          interfaces.add(superinterface);
        }
      }

      clazz = this.aptUtils.asTypeElement(this.types.asElement(clazz.getSuperclass()));
    }

    return interfaces;
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

  private void addSpringSupport(Builder proxyClassBilder, ProxyToGenerate proxyToGenerate) {
    proxyClassBilder.addSuperinterface(this.asTypeName("org.springframework.aop.SpringProxy"));
    proxyClassBilder.addSuperinterface(this.asTypeName("org.springframework.aop.framework.Advised"));
  }

  private TypeName asTypeName(CharSequence name) {
    return TypeName.get(this.elements.getTypeElement(name).asType());
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
      this.addMethodConstant(initializerBuilder, proxyToGenerate, methodConstant);
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
    List<Object> arguments = new ArrayList<>(adviceMethod.getParameters().size() + 1);
    buffer.append("this.$N.");
    arguments.add("aspect");
    buffer.append(adviceMethod.getSimpleName());
    buffer.append('(');
    boolean first = true;
    for (VariableElement parameter: adviceMethod.getParameters()) {
      if (!first) {
        buffer.append(", ");
      }
      first = false;
      Argument argument = this.buildAdviceCallArgument(parameter, adviceContext);
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
      // we can not use a java.util.Map here
      if (this.isSameType(annotationType, this.evaluateType)) { // @Evaluate
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildEvaluateArgument(adviceContext, mirror);
      } else if (this.isSameType(annotationType, this.targetObjectType)) { // @TargetObject
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildTargetObjectArgument();
      } else if (this.isSameType(annotationType, this.argumentsType)) { // @Arguments
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildArgumentsArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.returnValueType)) { // @ReturnValue
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }
        if (!this.hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturningType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "@ReturnValue is only available for @AfterReturning");
          return argument;
        }
        argument = this.buildReturnValueArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.executionTimeMillisType)) { // @ExecutionTimeMillis
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }
        if (!this.hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturningType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "@ExecutionTimeMillis is only available for @AfterReturning");
          return argument;
        }
        if (!this.isSameType(adviceParameter, this.longType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "@ExecutionTimeMillis must be of type long");
          return argument;
        }

        argument = this.buildExectionTimeMillisArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.executionTimeNanosType)) { // @ExecutionTimeNanos
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }
        if (!this.hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturningType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "@ExecutionTimeNanos is only available for @AfterReturning");
          return argument;
        }
        if (!this.isSameType(adviceParameter, this.longType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "@ExecutionTimeNanos must be of type long");
          return argument;
        }

        argument = this.buildExectionTimeNanosArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.joinpointType)) { // @Joinpoint
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildJoinpointArgument(adviceContext);
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
    LocalVariableContext localVariables = adviceContext.getJoinpointContext().getLocalVariables();
    return new Argument(localVariables.getReturnVariableName());
  }

  private Argument buildExectionTimeMillisArgument(AdviceContext adviceContext) {
    LocalVariableContext localVariables = adviceContext.getJoinpointContext().getLocalVariables();
    return new Argument(localVariables.getExectionTimeMillisName());
  }
  private Argument buildExectionTimeNanosArgument(AdviceContext adviceContext) {
    LocalVariableContext localVariables = adviceContext.getJoinpointContext().getLocalVariables();
    return new Argument(localVariables.getExectionTimeNanosName());
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
    AnnotationValue annotationValue = mirror.getElementValues().get(this.evaluateValueMethod);
    String expression = this.aptUtils.asString(annotationValue);
    JoinpointContext joinpointContext = adviceContext.getJoinpointContext();

    return new Argument("$S", this.evaluate(expression, joinpointContext.getTargetClassElement(), joinpointContext.getJoinpointElement()));
  }

  private boolean doMethodsNeedReturnValue(AdviceMethods adviceMethods) {
    return !adviceMethods.getAfterReturningMethods().isEmpty();
  }

  private boolean hasAnnotationMirror(Element element, TypeMirror expectedAnnotationType) {
    for (AnnotationMirror mirror : this.elements.getAllAnnotationMirrors(element)) {
      if (this.types.isSameType(mirror.getAnnotationType(), expectedAnnotationType)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasParameterWithAnnotation(List<ExecutableElement> methods, TypeMirror expectedAnnotation) {
    for (ExecutableElement method : methods) {
      if (this.hasParameterWithAnnotation(method, expectedAnnotation)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasParameterWithAnnotation(ExecutableElement method, TypeMirror expectedAnnotation) {
    for (VariableElement parameter : method.getParameters()) {
      if (this.hasAnnotationMirror(parameter, expectedAnnotation)) {
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
    return "super" + buildDelegateCall("", constructor);
  }

  private static String buildDelegateCall(String methodName, ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    if (parameters.isEmpty()) {
      return methodName + "()";
    }
    StringBuilder buffer = new StringBuilder();
    buffer.append(methodName);
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
    // FIXME find method provided by APT API
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
    TypeMirror aspectClass = this.extractAspectClassTypeMirror(adviseBy);
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
      if ((member.getKind() == ElementKind.METHOD) && member.getSimpleName().contentEquals(methodName)) {
        ExecutableElement method = this.aptUtils.asExecutableElement(member);
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
    Map<String, Set<ExecutableElement>> interfaceMethods = this.collectInterfaceMethods(typeElement);
    for (Element member : this.elements.getAllMembers(typeElement)) {
      if ((member.getKind() == ElementKind.METHOD) && isOverriable(member)) {
        if (member.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
          ExecutableElement method = this.aptUtils.asExecutableElement(member);
          if (!this.isOverridableObjectMethod(method)) {
            String methodName = method.getSimpleName().toString();

            Set<ExecutableElement> methodsWithSameName = interfaceMethods.getOrDefault(methodName, Collections.emptySet());
            boolean overrides = false;

            for (ExecutableElement interfaceMethod : methodsWithSameName) {
              // linear scan, doesn't scale well, let's hope we don't have too many overloaded methods
              if (this.elements.overrides(method, interfaceMethod, typeElement)) {
                overrides = true;
                break;
              }
            }

            if (!overrides) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private Map<String, Set<ExecutableElement>> collectInterfaceMethods(TypeElement clazz) {
    Map<String, Set<ExecutableElement>> interfaceMethods = new HashMap<>();
    this.addInterfaceMethods(clazz, interfaceMethods);
    return interfaceMethods;
  }

  private void addInterfaceMethods(TypeElement clazz, Map<String, Set<ExecutableElement>> interfaceMethods) {
    if (this.isSameType(clazz, this.objectType)) {
      return;
    }

    for (TypeMirror iface : clazz.getInterfaces()) {
      this.addMethods(this.asTypeElement(iface), interfaceMethods);
    }

    this.addInterfaceMethods(this.asTypeElement(clazz.getSuperclass()), interfaceMethods);
  }

  private TypeElement asTypeElement(TypeMirror typeMirror) {
    return this.aptUtils.asTypeElement(this.types.asElement(typeMirror));
  }

  private void addMethods(TypeElement typeElement, Map<String, Set<ExecutableElement>> interfaceMethods) {
    for (Element member : typeElement.getEnclosedElements()) {
      if ((member.getKind() == ElementKind.METHOD) && isOverriable(member)) {
        ExecutableElement method = this.aptUtils.asExecutableElement(member);
        String methodName = method.getSimpleName().toString();
        Set<ExecutableElement> methodsWithSameName = interfaceMethods.computeIfAbsent(methodName, (key) -> new HashSet<>());
        methodsWithSameName.add(method);
      }
    }
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

  private List<ExecutableElement> getMethodsAnnotatedWith(TypeElement type, TypeMirror annotationType) {
    List<ExecutableElement> methods = new ArrayList<>();
    for (Element member : this.elements.getAllMembers(type)) {
      if (member.getKind() == ElementKind.METHOD) {
        for (AnnotationMirror mirror : member.getAnnotationMirrors()) {
          if (this.isSameType(mirror.getAnnotationType(), annotationType)) {
            methods.add(this.aptUtils.asExecutableElement(member));
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


  static boolean isVoid(ExecutableElement method) {
    return method.getReturnType().getKind() == TypeKind.VOID;
  }

  private static Set<String> getParameterNames(ExecutableElement method) {
    return method.getParameters().stream()
            .map(v -> v.getSimpleName().toString())
            .collect(Collectors.toSet());

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
    if (methodName.contentEquals("getClass") && (parameterCount == 0)) {
      // java.lang.Object.getClass()
      return true;
    }
    if (methodName.contentEquals("notify") && (parameterCount == 0)) {
      // java.lang.Object.notify()
      return true;
    }
    if (methodName.contentEquals("notifyAll") && (parameterCount == 0)) {
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

  private AdviceMethods getAdviceMethods(ProxyToGenerate proxyToGenerate) {
    // TODO probably make aspectType an accessor method
    Element apectElement = this.types.asElement(proxyToGenerate.getAspect());
    TypeElement aspectType = this.aptUtils.asTypeElement(apectElement);

    List<ExecutableElement> beforeMethods = this.getBeforeMethods(aspectType);
    List<ExecutableElement> aroundMethods = this.getAroundMethods(aspectType);
    List<ExecutableElement> afterReturningMethods = this.getAfterReturningMethods(aspectType);
    List<ExecutableElement> afterFinallyMethods = this.getAfterFinallyMethods(aspectType);
    List<ExecutableElement> afterThrowingMethods = this.getAfterThrowingMethods(aspectType);

    return new AdviceMethods(beforeMethods, aroundMethods, afterReturningMethods, afterFinallyMethods, afterThrowingMethods);
  }

  static final class AdviceMethods {

    private final List<ExecutableElement> beforeMethods;
    private final List<ExecutableElement> aroundMethods;
    private final List<ExecutableElement> afterReturningMethods;
    private final List<ExecutableElement> afterFinallyMethods;
    private final List<ExecutableElement> afterThrowingMethods;

    AdviceMethods(List<ExecutableElement> beforeMethods, List<ExecutableElement> aroundMethods, List<ExecutableElement> afterReturningMethods, List<ExecutableElement> afterFinallyMethods, List<ExecutableElement> afterThrowingMethods) {
      this.beforeMethods = beforeMethods;
      this.aroundMethods = aroundMethods;
      this.afterReturningMethods = afterReturningMethods;
      this.afterFinallyMethods = afterFinallyMethods;
      this.afterThrowingMethods = afterThrowingMethods;
    }

    List<ExecutableElement> getBeforeMethods() {
      return this.beforeMethods;
    }

    List<ExecutableElement> getAroundMethods() {
      return this.aroundMethods;
    }

    List<ExecutableElement> getAfterReturningMethods() {
      return this.afterReturningMethods;
    }

    List<ExecutableElement> getAfterFinallyMethods() {
      return this.afterFinallyMethods;
    }

    public List<ExecutableElement> getAfterThrowingMethods() {
      return this.afterThrowingMethods;
    }

  }

  final class ImplementableMethodExtractor extends SimpleElementVisitor8<Void, List<ExecutableElement>> {

    @Override
    public Void visitExecutable(ExecutableElement e, List<ExecutableElement> p) {
      if ((e.getKind() == ElementKind.METHOD) && isOverriable(e) && !ProxyGenerator.this.isOverridableObjectMethod(e)) {
        p.add(e);
      }
      return null;
    }

  }

  static final class TargetObjectContext {

    private final Set<String> methodConstants;

    private final Map<String, Integer> methodCountsByName;

    TargetObjectContext() {
      this.methodConstants = new HashSet<>();
      this.methodCountsByName = new HashMap<>();
    }

    String generateMethodConstantName(String name) {
      int existingCount = this.methodCountsByName.getOrDefault(name, 0);
      String generatedName = "M_" + name + '_' + existingCount;
      if (!this.methodConstants.add(generatedName)) {
        throw new IllegalStateException("name " + generatedName + " already present");
      }
      this.methodCountsByName.merge(name, 1, (existing, increment) -> existing + increment);
      return generatedName;
    }

  }

  static final class LocalVariableContext {

    private final String returnVariableName;

    private final String exectionTimeMillisStartName;

    private final String exectionTimeMillisName;

    private final String exectionTimeNanosStartName;

    private final String exectionTimeNanosName;

    LocalVariableContext(String returnVariableName,
            String exectionTimeMillisStartName, String exectionTimeMillisName,
            String exectionTimeNanosStartName, String exectionTimeNanosName) {
      this.returnVariableName = returnVariableName;
      this.exectionTimeMillisStartName = exectionTimeMillisStartName;
      this.exectionTimeMillisName = exectionTimeMillisName;
      this.exectionTimeNanosStartName = exectionTimeNanosStartName;
      this.exectionTimeNanosName = exectionTimeNanosName;
    }

    String getReturnVariableName() {
      if (!this.hasReturnValueVariable()) {
        throw new IllegalStateException("no return variable present");
      }
      return this.returnVariableName;
    }

    boolean hasReturnValueVariable() {
      return this.returnVariableName != null;
    }

    boolean hasExectionTimeMillisStart() {
      return this.exectionTimeMillisStartName != null;
    }

    String getExectionTimeMillisStartName() {
      if (!this.hasExectionTimeMillis()) {
        throw new IllegalStateException("no execution time millis variable present");
      }
      return this.exectionTimeMillisStartName;
    }

    boolean hasExectionTimeMillis() {
      return this.exectionTimeMillisName != null;
    }

    String getExectionTimeMillisName() {
      if (!this.hasExectionTimeMillis()) {
        throw new IllegalStateException("no execution time millis variable present");
      }
      return this.exectionTimeMillisName;
    }

    boolean hasExectionTimeNanosStart() {
      return this.exectionTimeNanosStartName != null;
    }

    String getExectionTimeNanosStartName() {
      if (!this.hasExectionTimeNanos()) {
        throw new IllegalStateException("no execution time nanos variable present");
      }
      return this.exectionTimeNanosStartName;
    }

    boolean hasExectionTimeNanos() {
      return this.exectionTimeNanosName != null;
    }

    String getExectionTimeNanosName() {
      if (!this.hasExectionTimeNanos()) {
        throw new IllegalStateException("no execution time nanos variable present");
      }
      return this.exectionTimeNanosName;
    }

  }

  static final class JoinpointContext {

    private final TypeElement targetClassElement;
    private final ExecutableElement joinpointElement;
    private final TargetObjectContext targetObjectContext;
    private final LocalVariableContext localVariables;
    private String methodConstantName;
    private final com.squareup.javapoet.MethodSpec.Builder methodBuilder;

    JoinpointContext(TypeElement targetClassElement, ExecutableElement joinpointElement, TargetObjectContext targetObjectContext, LocalVariableContext localVariables, com.squareup.javapoet.MethodSpec.Builder methodBuilder) {
      this.targetClassElement = targetClassElement;
      this.joinpointElement = joinpointElement;
      this.targetObjectContext = targetObjectContext;
      this.localVariables = localVariables;
      this.methodBuilder = methodBuilder;
    }

    String getMethodConstantName() {
      if (this.methodConstantName == null) {
        String joinpointMethodName = this.joinpointElement.getSimpleName().toString();
        this.methodConstantName = this.targetObjectContext.generateMethodConstantName(joinpointMethodName);
      }
      return this.methodConstantName;
    }

    boolean isVoid() {
      return ProxyGenerator.isVoid(this.joinpointElement);
    }

    LocalVariableContext getLocalVariables() {
      return this.localVariables;
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


    boolean needsCatch() {
      return false;
    }

    boolean needsFinally() {
      return false;
    }

    com.squareup.javapoet.MethodSpec.Builder getMethodBuilder() {
      return this.methodBuilder;
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
