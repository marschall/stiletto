package com.github.marschall.stiletto.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import com.github.marschall.stiletto.processor.el.Joinpoint;
import com.github.marschall.stiletto.processor.el.TargetClass;

final class ExpressionEvaluator {

  String evaluate(String expression, TargetClass targetClass, Joinpoint joinPoint) {
    Thread currentThread = Thread.currentThread();
    ClassLoader oldTccl = currentThread.getContextClassLoader();
    currentThread.setContextClassLoader(ExpressionEvaluator.class.getClassLoader());
    try {
      // uses Thread.currentThread().getContextClassLoader() for implementation discovery
      // per default does not contain a dependency to the el-impl if the processor
      // is set up via a the maven-compiler-plugin rather than a project dependency
      return evaluateWithTccl(expression, targetClass, joinPoint);
    } finally {
      currentThread.setContextClassLoader(oldTccl);
    }
  }

  String evaluateWithTccl(String expression, TargetClass targetClass, Joinpoint joinPoint) {
    // http://illegalargumentexception.blogspot.ch/2008/04/java-using-el-outside-j2ee.html
    // https://docs.oracle.com/javaee/7/api/javax/el/ELProcessor.html

    ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

//    CompositeELResolver compositeELResolver = new CompositeELResolver();
//    compositeELResolver.add(new BeanELResolver());
//    compositeELResolver.add(new ArrayELResolver());
//    compositeELResolver.add(new ListELResolver());
//    compositeELResolver.add(new MapELResolver());

    VariableMapper variableMapper = new SimpleVariableMapper();
    variableMapper.setVariable("targetClass", expressionFactory.createValueExpression(targetClass, TargetClass.class));
    variableMapper.setVariable("joinpoint", expressionFactory.createValueExpression(joinPoint, Joinpoint.class));
    FunctionMapper functionMapper = new SimpleFunctionMapper();

    ELContext context = new SimpleELContext(new BeanELResolver(), functionMapper, variableMapper);

    ValueExpression valueExpression = expressionFactory.createValueExpression(context, expression, String.class);
    return (String) valueExpression.getValue(context);
  }

  static final class SimpleELContext extends ELContext {

    private final ELResolver elResolver;
    private final FunctionMapper functionMapper;
    private final VariableMapper variableMapper;

    private SimpleELContext(ELResolver elResolver, FunctionMapper functionMapper, VariableMapper variableMapper) {
      this.elResolver = elResolver;
      this.functionMapper = functionMapper;
      this.variableMapper = variableMapper;
    }

    @Override
    public ELResolver getELResolver() {
      return this.elResolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
      return this.functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
      return this.variableMapper;
    }
  }

  static final class SimpleFunctionMapper extends FunctionMapper {

    private Map<Key, Method> functionMap;

    SimpleFunctionMapper() {
      this.functionMap = new HashMap<>();
    }

    @Override
    public Method resolveFunction(String prefix, String localName) {
      Key key = new Key(prefix, localName);
      return this.functionMap.get(key);
    }

    public void addFunction(String prefix, String localName, Method method) {
      Objects.requireNonNull(prefix, "prefix");
      Objects.requireNonNull(localName, "localName");
      Objects.requireNonNull(method, "method");

      int modifiers = method.getModifiers();
      if (!Modifier.isPublic(modifiers)) {
        throw new IllegalArgumentException("method not public");
      }
      if (!Modifier.isStatic(modifiers)) {
        throw new IllegalArgumentException("method not static");
      }
      Class<?> returnType = method.getReturnType();
      if (returnType == Void.TYPE) {
        throw new IllegalArgumentException("method returns void");
      }

      Key key = new Key(prefix, localName);
      this.functionMap.put(key, method);
    }

    static final class Key {

      private final String prefix;
      private final String localName;

      Key(String prefix, String localName) {
        Objects.requireNonNull(prefix, "prefix");
        Objects.requireNonNull(localName, "localName");
        this.prefix = prefix;
        this.localName = localName;
      }

      @Override
      public int hashCode() {
        return Objects.hash(this.prefix, this.localName);
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == this) {
          return true;
        }
        if (!(obj instanceof Key)) {
          return false;
        }
        Key other = (Key) obj;
        return this.prefix.equals(other.prefix)
                && this.localName.equals(other.localName);
      }

    }

  }

  static final class SimpleVariableMapper extends VariableMapper {

    private final Map<String, ValueExpression> expressions;

    SimpleVariableMapper() {
      this.expressions = new HashMap<>();
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
      return this.expressions.get(variable);
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
      return this.expressions.put(variable, expression);
    }

  }

}
