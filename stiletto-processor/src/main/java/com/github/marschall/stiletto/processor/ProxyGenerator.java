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
import java.lang.annotation.Annotation;
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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

@SupportedAnnotationTypes({ADVISE_BY, ADVISE_BY_ALL})
@SupportedSourceVersion(RELEASE_8)
public class ProxyGenerator extends AbstractProcessor {

  static final String METHOD_CONSTANT_PREFIX = "M_";

  static final String ANNOTATION_CONSTANT_PREFIX = "A_";

  private static final String EXECUTION_TIME_NANOS = "com.github.marschall.stiletto.api.injection.ExecutionTimeNanos";

  private static final String EXECUTION_TIME_MILLIS = "com.github.marschall.stiletto.api.injection.ExecutionTimeMillis";

  private static final String JOINPOINT = "com.github.marschall.stiletto.api.injection.Joinpoint";

  private static final String ARGUMENTS = "com.github.marschall.stiletto.api.injection.Arguments";

  private static final String METHOD_CALL = "com.github.marschall.stiletto.api.injection.MethodCall";

  private static final String BEFORE_VALUE = "com.github.marschall.stiletto.api.injection.BeforeValue";

  private static final String DECLARED_ANNOTATION = "com.github.marschall.stiletto.api.injection.DeclaredAnnotation";

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

  // @MethodCall
  private TypeMirror methodCall;

  // @DeclaredAnnotation
  private TypeMirror declaredAnnotationType;

  // @BeforeValue
  private TypeMirror beforeValueType;

  // @Joinpoint
  private TypeMirror joinpointType;

  // @ExecutionTimeMillis
  private TypeMirror executionTimeMillisType;

  // @ExecutionTimeNanos
  private TypeMirror executionTimeNanosType;

  // @AfterReturning
  private TypeMirror afterReturningType;

  // @AfterFinally
  private TypeMirror afterFinallyType;

  // @AfterThrowing
  private TypeMirror afterThrowingType;

  // @Around
  private TypeMirror aroundType;

  // @Before
  private TypeMirror beforeType;

  private ExpressionEvaluator evaluator;

  private AptUtils aptUtils;

  private Elements elements;

  private AnnotationValueUtils annotationValueUtils;

  private Types types;

  private boolean addSpringSupport;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;
    this.elements = this.processingEnv.getElementUtils();
    this.types = this.processingEnv.getTypeUtils();
    this.aptUtils = new AptUtils(this.types);
    this.annotationValueUtils = new AnnotationValueUtils(this.types, this.elements);
    this.addGenerated = true;
    this.addSpringSupport = processingEnv.getOptions().getOrDefault("stiletto.spring", "false").equals("true");

    this.beforeType = this.asTypeMirror(BEFORE);
    this.aroundType = this.asTypeMirror(AROUND);
    this.afterThrowingType = this.asTypeMirror(AFTER_THROWING);
    this.afterReturningType = this.asTypeMirror(AFTER_RETURNING);
    this.afterFinallyType = this.asTypeMirror(AFTER_FINALLY);
    TypeElement adviseBy = this.getTypeElement(ADVISE_BY);
    this.adviseByValueMethod = this.getValueMethod(adviseBy);
    this.intType = this.types.getPrimitiveType(TypeKind.INT);
    this.longType = this.types.getPrimitiveType(TypeKind.LONG);
    this.objectType = this.getTypeElement("java.lang.Object").asType();

    TypeElement evaluateElement = this.getTypeElement(EVALUATE);
    this.evaluateValueMethod = this.getValueMethod(evaluateElement);
    this.evaluateType = evaluateElement.asType();

    this.targetObjectType = this.asTypeMirror(TARGET_OBJECT);
    this.returnValueType = this.asTypeMirror(RETURN_VALUE);
    this.methodCall = this.asTypeMirror(METHOD_CALL);
    this.argumentsType = this.asTypeMirror(ARGUMENTS);
    this.joinpointType = this.asTypeMirror(JOINPOINT);
    this.executionTimeMillisType = this.asTypeMirror(EXECUTION_TIME_MILLIS);
    this.executionTimeNanosType = this.asTypeMirror(EXECUTION_TIME_NANOS);
    this.declaredAnnotationType = this.asTypeMirror(DECLARED_ANNOTATION);
    this.beforeValueType = this.asTypeMirror(BEFORE_VALUE);

    if (this.addSpringSupport) {
      // look like a cglib proxy to spring
      this.namingStrategy = s -> s + "$$_";
    } else {
      this.namingStrategy = s -> s + "_";
    }
    this.evaluator = new ExpressionEvaluator();
  }

  private TypeMirror asTypeMirror(String className) {
    TypeElement typeElement = this.getTypeElement(className);
    return typeElement.asType();
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

    TargetObjectContext targetObjectContext = new TargetObjectContext(proxyClassName, this.types, this.annotationValueUtils);

    List<MethodConstant> methodConstants = new ArrayList<>();
    for (ExecutableElement method : this.getMethodsToImplement(targetClass)) {
      JoinpointContext joinpointContext = this.implementMethod(proxyToGenerate, proxyClassBilder, targetObjectContext,
              method);

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

  private LocalVariables buildLocalVariables(ExecutableElement joinpointElement, AdviceMethods adviceMethods) {
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
    return new LocalVariables(returnVariableName,
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
          Builder proxyClassBilder, TargetObjectContext targetObjectContext,
          ExecutableElement joinpointElement) {

    TypeElement targetClass = proxyToGenerate.getTargetClassElement();

    AdviceMethods adviceMethods = this.getAdviceMethods(proxyToGenerate);
    LocalVariables localVariables = this.buildLocalVariables(joinpointElement, adviceMethods);

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

        AdviceContext adviceContext = new AdviceContext(aroundMethod, joinpointContext);

        Statement adviceCall = this.buildAdviceCall("$T $N = ", adviceContext, returnType, returnVariableName);
        methodBuilder.addStatement(adviceCall.getFormat(), adviceCall.getArguments());
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
      String methodName = joinpointElement.getSimpleName().toString();
      if (adviceMethods.getAroundMethods().isEmpty()) {
        if (joinpointContext.isVoid()) {
          methodBuilder.addStatement(buildDelegateCall("this.$N." + methodName, joinpointElement), "targetObject");
        } else {
          methodBuilder.addStatement(buildDelegateCall("return this.$N." + methodName, joinpointElement), "targetObject");
        }
      } else {
        ExecutableElement aroundMethod = adviceMethods.getAroundMethods().get(0);

        AdviceContext adviceContext = new AdviceContext(aroundMethod, joinpointContext);

        Statement adviceCall;
        if (joinpointContext.isVoid()) {
          adviceCall = this.buildAdviceCall("", adviceContext);
        } else {
          adviceCall = this.buildAdviceCall("return ", adviceContext);
        }
        methodBuilder.addStatement(adviceCall.getFormat(), adviceCall.getArguments());
      }
    }

    proxyClassBilder.addMethod(methodBuilder.build());
    return joinpointContext;
  }

  private void recordEndTime(JoinpointContext joinpointContext) {
    LocalVariables localVariables = joinpointContext.getLocalVariables();
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
    LocalVariables localVariables = joinpointContext.getLocalVariables();
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
      if (isVoid(beforeMethod)) {
        Statement adviceCall = this.buildAdviceCall(adviceContext);
        joinpointContext.getMethodBuilder().addStatement(adviceCall.getFormat(), adviceCall.getArguments());
      } else {

      }

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
    return this.buildAdviceCall(null, adviceContext);
  }

  private Statement buildAdviceCall(String prefix, AdviceContext adviceContext, Object... additionalArguments) {
    StringBuilder buffer = new StringBuilder();
    ExecutableElement adviceMethod = adviceContext.getAdviceMethod();
    List<Object> arguments = new ArrayList<>(adviceMethod.getParameters().size() + 1 + additionalArguments.length);
    if (prefix != null) {
      buffer.append(prefix);
      for (Object additionalArgument : additionalArguments) {
        arguments.add(additionalArgument);
      }
    }
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
        argument.addValueTo(buffer);
        argument.addParameterTo(arguments);
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
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildEvaluateArgument(adviceContext, mirror);
      } else if (this.isSameType(annotationType, this.targetObjectType)) { // @TargetObject
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildTargetObjectArgument();
      } else if (this.isSameType(annotationType, this.argumentsType)) { // @Arguments
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present on: " + adviceParameter);
          return argument;
        }

        argument = this.buildArgumentsArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.returnValueType)) { // @ReturnValue
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present", adviceParameter);
          return argument;
        }
        if (!this.hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturningType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "@ReturnValue is only available for @AfterReturning", adviceParameter);
          return argument;
        }
        argument = this.buildReturnValueArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.executionTimeMillisType)) { // @ExecutionTimeMillis
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present", adviceParameter);
          return argument;
        }
        if (!this.hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturningType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "@ExecutionTimeMillis is only available for @AfterReturning", adviceParameter);
          return argument;
        }
        if (!this.isSameType(adviceParameter, this.longType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "@ExecutionTimeMillis must be of type long", adviceParameter);
          return argument;
        }

        argument = this.buildExectionTimeMillisArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.executionTimeNanosType)) { // @ExecutionTimeNanos
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present on: " + adviceParameter, adviceParameter);
          return argument;
        }
        if (!this.hasAnnotationMirror(adviceContext.getAdviceMethod(), this.afterReturningType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "@ExecutionTimeNanos is only available for @AfterReturning");
          return argument;
        }
        if (!this.isSameType(adviceParameter, this.longType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "@ExecutionTimeNanos must be of type long", adviceParameter);
          return argument;
        }

        argument = this.buildExectionTimeNanosArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.joinpointType)) { // @Joinpoint
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present", adviceParameter);
          return argument;
        }

        argument = this.buildJoinpointArgument(adviceContext);
      } else if (this.isSameType(annotationType, this.methodCall)) { // @MethodCall
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present", adviceParameter);
          return argument;
        }
        TypeElement parameterType = this.asTypeElement(adviceParameter.asType());
        if (!this.elements.isFunctionalInterface(parameterType)) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR, "not a functional interface", adviceParameter);
          return argument;
        }

        argument = buildMethodCallArgument(parameterType, adviceContext);
      } else if (this.isSameType(annotationType, this.beforeValueType)) { // @BeforeValue
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present", adviceParameter);
          return argument;
        }
        // TODO implement
        argument = new ConstantArgument("null");
      } else if (this.isSameType(annotationType, this.declaredAnnotationType)) { // @DeclaredAnnotation
        if (argument != null) {
          this.processingEnv.getMessager().printMessage(Kind.ERROR,
                  "more than one injection annotation present", adviceParameter);
          return argument;
        }
        argument = this.buildDeclaredAnnotationArgument(adviceContext, adviceParameter);
      }
    }

    if (argument == null) {
      this.processingEnv.getMessager().printMessage(Kind.ERROR, "no injection annotation present on: " + adviceParameter);
    }
    return argument;
  }

  private Argument buildMethodCallArgument(TypeElement parameterType, AdviceContext adviceContext) {
    TargetObjectContext targetObjectContext = adviceContext.getJoinpointContext().getTargetObjectContext();
    ExecutableElement joinpointElement = adviceContext.getJoinpointContext().getJoinpointElement();
    String methodName = joinpointElement.getSimpleName().toString();
    ExecutableElement aroundMethod = getSoleNonDefaultMethod(parameterType);

    // returnVariableName = this.aspect.joinpoint(new ActualMethodCall<R>() {
    //     @Override
    //     public R invoke() {
    //       return TargetClass.this.joinpoint();
    //     }
    // });

    ParameterizedTypeName superInterface = ParameterizedTypeName.get(
            ClassName.get(parameterType),
            TypeName.get(joinpointElement.getReturnType()));

    DeclaredType callArgumentTtype = this.types.getDeclaredType(
            parameterType, joinpointElement.getReturnType());

    return new FormattedArgument("$L", TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(superInterface)
            .addMethod(MethodSpec.overriding(aroundMethod, callArgumentTtype, this.types)
                    // return this.targetObject.joinpoint();
                    .addStatement("return $N.this.$N." + buildDelegateCall(methodName, joinpointElement), targetObjectContext.getProxyClassName(), "targetObject")
                    .build())
            .build());
  }

  private ExecutableElement getSoleNonDefaultMethod(Element adviceParameter) {
    List<ExecutableElement> methods = getMethodsToImplement(this.aptUtils.asTypeElement(adviceParameter));
    // TODO visitor
    for (ExecutableElement method : methods) {
      if (!method.isDefault()) {
        return method;
      }
    }
    // TODO messager?
    throw new IllegalStateException("no non-default method found in");
  }

  private Argument buildJoinpointArgument(AdviceContext adviceContext) {
    JoinpointContext joinpointContext = adviceContext.getJoinpointContext();
    String constantName = joinpointContext.getMethodConstantName();
    return new ConstantArgument(constantName);
  }

  private Argument buildDeclaredAnnotationArgument(AdviceContext adviceContext, VariableElement adviceParameter) {
    JoinpointContext joinpointContext = adviceContext.getJoinpointContext();
    ExecutableElement joinpointElement = joinpointContext.getJoinpointElement();
    TypeMirror annotationType = adviceParameter.asType();
    for (AnnotationMirror annotationMirror : this.elements.getAllAnnotationMirrors(joinpointElement)) {
      if (this.types.isSameType(annotationMirror.getAnnotationType(), annotationType)) {
        String constantName = joinpointContext.getTargetObjectContext().addAnnotationMirror(annotationMirror);
        return new ConstantArgument(constantName);
      }
    }
    // not found on method, search on class
    for (AnnotationMirror annotationMirror : this.elements.getAllAnnotationMirrors(joinpointElement.getEnclosingElement())) {
      if (this.types.isSameType(annotationMirror.getAnnotationType(), annotationType)) {
        String constantName = joinpointContext.getTargetObjectContext().addAnnotationMirror(annotationMirror);
        return new ConstantArgument(constantName);
      }
    }
    // TODO error handling
    return null;
  }

  private Argument buildReturnValueArgument(AdviceContext adviceContext) {
    LocalVariables localVariables = adviceContext.getJoinpointContext().getLocalVariables();
    return new ConstantArgument(localVariables.getReturnVariableName());
  }

  private Argument buildExectionTimeMillisArgument(AdviceContext adviceContext) {
    LocalVariables localVariables = adviceContext.getJoinpointContext().getLocalVariables();
    return new ConstantArgument(localVariables.getExectionTimeMillisName());
  }
  private Argument buildExectionTimeNanosArgument(AdviceContext adviceContext) {
    LocalVariables localVariables = adviceContext.getJoinpointContext().getLocalVariables();
    return new ConstantArgument(localVariables.getExectionTimeNanosName());
  }

  private Argument buildArgumentsArgument(AdviceContext adviceContext) {
    List<? extends VariableElement> parameters = adviceContext.getJoinpointContext().getJoinpointElement().getParameters();
    if (parameters.isEmpty()) {
      // consistent with InvocationHandler
      return new ConstantArgument("null");
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
      return new ConstantArgument(buffer.toString());
    }
  }

  private Argument buildTargetObjectArgument() {
    return new ConstantArgument("this.targetObject");
  }

  private Argument buildEvaluateArgument(AdviceContext adviceContext, AnnotationMirror mirror) {
    AnnotationValue annotationValue = mirror.getElementValues().get(this.evaluateValueMethod);
    String expression = this.aptUtils.asString(annotationValue);
    JoinpointContext joinpointContext = adviceContext.getJoinpointContext();

    TypeElement targetClass = joinpointContext.getTargetClassElement();
    ExecutableElement joinpoint = joinpointContext.getJoinpointElement();
    return new FormattedArgument("$S", this.evaluate(expression, targetClass, joinpoint));
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

  static abstract class Argument {

    abstract void addValueTo(StringBuilder buffer);

    abstract void addParameterTo(List<Object> arguments);

  }

  static final class FormattedArgument extends Argument {

    private final String value;

    private final Object formatParameter;

    FormattedArgument(String value, Object formatParameter) {
      Objects.requireNonNull(value);
      this.value = value;
      this.formatParameter = formatParameter;
    }

    @Override
    void addValueTo(StringBuilder buffer) {
      buffer.append(this.value);
    }

    @Override
    void addParameterTo(List<Object> arguments) {
      arguments.add(this.formatParameter);
    }

    @Override
    public String toString() {
      return this.value + "%" + this.formatParameter;
    }

  }

  static final class ConstantArgument extends Argument {

    private final String value;

    ConstantArgument(String value) {
      Objects.requireNonNull(value);
      this.value = value;
    }

    @Override
    void addValueTo(StringBuilder buffer) {
      buffer.append(this.value);
    }

    @Override
    void addParameterTo(List<Object> arguments) {
      // nothing
    }

    @Override
    public String toString() {
      return this.value;
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
      return packageName + '.' + proxyClassName;
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
        if (this.hasAnnotationMirror(member, annotationType)) {
          methods.add(this.aptUtils.asExecutableElement(member));
          continue;
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

  private boolean returnsAnnotation(ExecutableElement method) {
    TypeMirror returnType = method.getReturnType();
    if (returnType.getKind() != TypeKind.DECLARED) {
      return false;
    }
    return isAnnotation(returnType);
  }

  private boolean returnsAnnotationArray(ExecutableElement method) {
    TypeMirror returnType = method.getReturnType();
    if (returnType.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = this.aptUtils.asDeclaredType(returnType);
    Element element = declaredType.asElement();
    TypeElement typeElement = this.aptUtils.asTypeElement(element);
    List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();
    if (typeParameters.size() != 1) {
      return false;
    }
    TypeParameterElement typeParameter = typeParameters.get(0);
    // FIXME
//    return isSameType(this.elements.getTypeElement(Annotation.class.getName()), typeParameter.getSuperclass());
    return false;
  }

  private boolean isAnnotation(TypeMirror typeMirror) {
    DeclaredType declaredType = this.aptUtils.asDeclaredType(typeMirror);
    TypeElement typeElement = this.aptUtils.asTypeElement(declaredType.asElement());
    TypeMirror superclass = typeElement.getSuperclass();
    return isSameType(this.elements.getTypeElement(Annotation.class.getName()), superclass);
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

    private final MethodConstants methodConstantContext;

    private final String proxyClassName;

    private final AnnotationConstants annotationConstants;

    TargetObjectContext(String proxyClassName, Types types, AnnotationValueUtils utils) {
      this.proxyClassName = proxyClassName;
      this.methodConstantContext = new MethodConstants();
      this.annotationConstants = new AnnotationConstants(types, utils);
    }

    String getProxyClassName() {
      return this.proxyClassName;
    }

    String generateMethodConstantName(String name) {
      return this.methodConstantContext.generateMethodConstantName(name);
    }

    String addAnnotationMirror(AnnotationMirror annotationMirror) {
      return this.annotationConstants.addAnnotationMirror(annotationMirror);
    }

  }

  static final class MethodConstants {

    // constants for java.lang.reflect.Method
    private final Set<String> methodConstants;

    private final Map<String, Integer> methodCountsByName;

    MethodConstants() {
      this.methodConstants = new HashSet<>();
      this.methodCountsByName = new HashMap<>();
    }

    String generateMethodConstantName(String name) {
      int existingCount = this.methodCountsByName.getOrDefault(name, 0);
      String generatedName = METHOD_CONSTANT_PREFIX + name + '_' + existingCount;
      if (!this.methodConstants.add(generatedName)) {
        throw new IllegalStateException("name " + generatedName + " already present");
      }
      this.methodCountsByName.merge(name, 1, (existing, increment) -> existing + increment);
      return generatedName;
    }

  }

  static final class LocalVariables {

    private final String returnVariableName;

    private final String exectionTimeMillisStartName;

    private final String exectionTimeMillisName;

    private final String exectionTimeNanosStartName;

    private final String exectionTimeNanosName;

    LocalVariables(String returnVariableName,
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
    private final LocalVariables localVariables;
    private String methodConstantName;
    private final com.squareup.javapoet.MethodSpec.Builder methodBuilder;

    JoinpointContext(TypeElement targetClassElement, ExecutableElement joinpointElement, TargetObjectContext targetObjectContext, LocalVariables localVariables, com.squareup.javapoet.MethodSpec.Builder methodBuilder) {
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

    LocalVariables getLocalVariables() {
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

    TargetObjectContext getTargetObjectContext() {
      return this.targetObjectContext;
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

  static final class AnnotationConstants {

    private final List<AnnotationConstant> annotationConstants;
    private final Types types;
    private final AnnotationValueUtils utils;
    private final Set<String> constantNames;

    AnnotationConstants(Types types, AnnotationValueUtils utils) {
      this.types = types;
      this.utils = utils;
      this.annotationConstants = new ArrayList<>(2);
      this.constantNames = new HashSet<>(2);
    }

    String addAnnotationMirror(AnnotationMirror annotationMirror) {

      for (AnnotationConstant constant : this.annotationConstants) {
        AnnotationMirror exiting = constant.getAnnotationMirror();
        if (!this.types.isSameType(exiting.getAnnotationType(), annotationMirror.getAnnotationType())) {
          break;
        }
        if (this.utils.isEqual(exiting, annotationMirror)) {
          return constant.getName();
        }
      }
      String name = this.generateConstantName(annotationMirror);
      this.annotationConstants.add(new AnnotationConstant(annotationMirror, name));
      return name;
    }

    private String generateConstantName(AnnotationMirror annotationMirror) {
      String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
      String candidate = null;
      boolean added = false;
      int i = 1;
      while (!added) {
        candidate = ANNOTATION_CONSTANT_PREFIX + name + '_' + i++;
        added = this.constantNames.add(candidate);
      }
      return candidate;
    }

  }

  static final class AnnotationConstant {

    private final AnnotationMirror annotationMirror;
    private final String name;

    AnnotationConstant(AnnotationMirror annotationMirror, String name) {
      this.annotationMirror = annotationMirror;
      this.name = name;
    }

    AnnotationMirror getAnnotationMirror() {
      return annotationMirror;
    }

    String getName() {
      return name;
    }

  }

}
