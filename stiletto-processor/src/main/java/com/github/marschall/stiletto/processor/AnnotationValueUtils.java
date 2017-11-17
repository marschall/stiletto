package com.github.marschall.stiletto.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractAnnotationValueVisitor8;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.Types;

public class AnnotationValueUtils {

  private final Types types;
  private final Elements elements;

  AnnotationValueUtils(Types types, Elements elements) {
    this.types = types;
    this.elements = elements;
  }

  boolean isEqual(AnnotationMirror a, AnnotationMirror b) {
    return new EqualsAnnotationMirror(this.types, this.elements)
            .visitAnnotation(a, b);
  }

  boolean isAnnotation(AnnotationValue annotationValue) {
    return annotationValue.accept(IsAnnotation.INSTANCE, null);
  }

  boolean isArray(AnnotationValue annotationValue) {
    return annotationValue.accept(IsArray.INSTANCE, null);
  }

  static Map<String, AnnotationValue> getElementValuesWithDefaults(Elements elements, AnnotationMirror annotationMirror) {
    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = elements.getElementValuesWithDefaults(annotationMirror);
    Map<String, AnnotationValue> result = new HashMap<>(elementValues.size());
    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
      result.put(entry.getKey().getSimpleName().toString(), entry.getValue());
    }
    return result;
  }

  Map<String, AnnotationValue> getElementValuesWithDefaults(AnnotationMirror annotationMirror) {
    return getElementValuesWithDefaults(this.elements, annotationMirror);
  }

  static final class IsAnnotationValueEqual extends AbstractAnnotationValueVisitor8<Boolean, AnnotationValue> {

    private final Types types;
    private final Elements elements;

    IsAnnotationValueEqual(Types types, Elements elements) {
      this.types = types;
      this.elements = elements;
    }

    @Override
    public Boolean visitBoolean(boolean b, AnnotationValue p) {
      return EqualsBoolean.INSTANCE.visit(p, b);
    }

    @Override
    public Boolean visitByte(byte b, AnnotationValue p) {
      return EqualsByte.INSTANCE.visit(p, b);
    }

    @Override
    public Boolean visitChar(char c, AnnotationValue p) {
      return EqualsChar.INSTANCE.visit(p, c);
    }

    @Override
    public Boolean visitDouble(double d, AnnotationValue p) {
      return EqualsDouble.INSTANCE.visit(p, d);
    }

    @Override
    public Boolean visitFloat(float f, AnnotationValue p) {
      return EqualsFloat.INSTANCE.visit(p, f);
    }

    @Override
    public Boolean visitInt(int i, AnnotationValue p) {
      return EqualsInt.INSTANCE.visit(p, i);
    }

    @Override
    public Boolean visitLong(long l, AnnotationValue p) {
      return EqualsLong.INSTANCE.visit(p, l);
    }

    @Override
    public Boolean visitShort(short s, AnnotationValue p) {
      return EqualsShort.INSTANCE.visit(p, s);
    }

    @Override
    public Boolean visitString(String s, AnnotationValue p) {
      return EqualsString.INSTANCE.visit(p, s);
    }

    @Override
    public Boolean visitType(TypeMirror t, AnnotationValue p) {
      return new EqualsType(this.types).visit(p, t);
    }

    @Override
    public Boolean visitEnumConstant(VariableElement c, AnnotationValue p) {
      return new EqualsEnumConstant(this.types).visit(p, c);
    }

    @Override
    public Boolean visitAnnotation(AnnotationMirror a, AnnotationValue p) {
      return new EqualsAnnotationMirror(this.types, this.elements).visit(p, a);
    }

    @Override
    public Boolean visitArray(List<? extends AnnotationValue> values, AnnotationValue p) {
      return new EqualsArray(this.types, this.elements).visit(p, values);
    }


  }

  static abstract class EqualsValue<V> extends SimpleAnnotationValueVisitor8<Boolean, V> {

    @Override
    protected Boolean defaultAction(Object o, V p) {
      return false;
    }

  }

  static final class EqualsBoolean extends EqualsValue<Boolean> {

    static final AnnotationValueVisitor<Boolean, Boolean> INSTANCE = new EqualsBoolean();

    private EqualsBoolean() {
      super();
    }

    @Override
    public Boolean visitBoolean(boolean b, Boolean p) {
      return b == p.booleanValue();
    }

  }

  static final class EqualsByte extends EqualsValue<Byte> {

    static final AnnotationValueVisitor<Boolean, Byte> INSTANCE = new EqualsByte();

    private EqualsByte() {
      super();
    }

    @Override
    public Boolean visitByte(byte b, Byte p) {
      return b == p.byteValue();
    }

  }

  static final class EqualsChar extends EqualsValue<Character> {

    static final AnnotationValueVisitor<Boolean, Character> INSTANCE = new EqualsChar();

    private EqualsChar() {
      super();
    }

    @Override
    public Boolean visitChar(char c, Character p) {
      return c == p.charValue();
    }

  }

  static final class EqualsDouble extends EqualsValue<Double> {

    static final AnnotationValueVisitor<Boolean, Double> INSTANCE = new EqualsDouble();

    private EqualsDouble() {
      super();
    }

    @Override
    public Boolean visitDouble(double d, Double p) {
      return d == p.doubleValue();
    }

  }

  static final class EqualsFloat extends EqualsValue<Float> {

    static final AnnotationValueVisitor<Boolean, Float> INSTANCE = new EqualsFloat();

    private EqualsFloat() {
      super();
    }

    @Override
    public Boolean visitFloat(float f, Float p) {
      return f == p.floatValue();
    }

  }

  static final class EqualsInt extends EqualsValue<Integer> {

    static final AnnotationValueVisitor<Boolean, Integer> INSTANCE = new EqualsInt();

    private EqualsInt() {
      super();
    }

    @Override
    public Boolean visitInt(int i, Integer p) {
      return i == p.intValue();
    }

  }

  static final class EqualsLong extends EqualsValue<Long> {

    static final AnnotationValueVisitor<Boolean, Long> INSTANCE = new EqualsLong();

    private EqualsLong() {
      super();
    }

    @Override
    public Boolean visitLong(long l, Long p) {
      return l == p.longValue();
    }

  }

  static final class EqualsShort extends EqualsValue<Short> {

    static final AnnotationValueVisitor<Boolean, Short> INSTANCE = new EqualsShort();

    private EqualsShort() {
      super();
    }

    @Override
    public Boolean visitShort(short s, Short p) {
      return s == p.shortValue();
    }

  }

  static final class EqualsString extends EqualsValue<String> {

    static final AnnotationValueVisitor<Boolean,String > INSTANCE = new EqualsString();

    private EqualsString() {
      super();
    }

    @Override
    public Boolean visitString(String s, String p) {
      return s.equals(p);
    }


  }

  static final class EqualsType extends EqualsValue<TypeMirror> {

    private final Types types;

    EqualsType(Types types) {
      this.types = types;
    }

    @Override
    public Boolean visitType(TypeMirror t, TypeMirror p) {
      return this.types.isSameType(t, p);
    }

  }

  static final class EqualsAnnotationMirror extends EqualsValue<AnnotationMirror> {

    private final Types types;
    private final Elements elements;

    EqualsAnnotationMirror(Types types, Elements elements) {
      this.types = types;
      this.elements = elements;
    }

    @Override
    public Boolean visitAnnotation(AnnotationMirror a, AnnotationMirror b) {
      if (this.types.isSameType(a.getAnnotationType(), b.getAnnotationType())) {
      }
      Map<String, AnnotationValue> aValues = getElementValuesWithDefaults(this.elements, a);
      Map<String, AnnotationValue> pValues = getElementValuesWithDefaults(this.elements, b);

      if (aValues.size() != pValues.size()) {
        return false;
      }

      IsAnnotationValueEqual isEqual = new IsAnnotationValueEqual(this.types, this.elements);
      for (Entry<String, AnnotationValue> entry : aValues.entrySet()) {
        AnnotationValue aValue = entry.getValue();
        AnnotationValue bValue = pValues.get(entry.getKey());
        boolean equal = isEqual.visit(aValue, bValue);
        if (!equal) {
          return false;
        }
      }
      return true;
    }

  }

  static final class EqualsEnumConstant extends EqualsValue<VariableElement> {

    private final Types types;

    EqualsEnumConstant(Types types) {
      this.types = types;
    }

    @Override
    public Boolean visitEnumConstant(VariableElement c, VariableElement p) {
      if (!this.types.isSameType(c.asType(), p.asType())) {
        return false;
      }
      return c.getSimpleName().equals(p.getSimpleName());
    }

  }

  static final class EqualsArray extends EqualsValue<List<? extends AnnotationValue>> {

    private final Types types;
    private final Elements elements;

    EqualsArray(Types types, Elements elements) {
      this.types = types;
      this.elements = elements;
    }

    @Override
    public Boolean visitArray(List<? extends AnnotationValue> vals, List<? extends AnnotationValue> p) {
      if (vals.size() != p.size()) {
        return false;
      }
      IsAnnotationValueEqual isEqual = new IsAnnotationValueEqual(this.types, this.elements);
      for (int i = 0; i < vals.size(); i++) {
        AnnotationValue a = vals.get(i);
        AnnotationValue b = p.get(i);

        boolean equal = isEqual.visit(a, b);
        if (!equal) {
          return false;
        }

      }
      return true;
    }

  }

  static final class IsArray extends SimpleAnnotationValueVisitor8<Boolean, Void> {

    static final AnnotationValueVisitor<Boolean, Void> INSTANCE = new IsArray();

    private IsArray() {
      super();
    }

    @Override
    protected Boolean defaultAction(Object o, Void p) {
      return false;
    }

    @Override
    public Boolean visitArray(List<? extends AnnotationValue> vals, Void p) {
      return true;
    }

  }

  static final class IsAnnotation extends SimpleAnnotationValueVisitor8<Boolean, Void> {

    static final AnnotationValueVisitor<Boolean, Void> INSTANCE = new IsAnnotation();

    private IsAnnotation() {
      super();
    }

    @Override
    protected Boolean defaultAction(Object o, Void p) {
      return false;
    }

    @Override
    public Boolean visitAnnotation(AnnotationMirror a, Void p) {
      return true;
    }

  }

}
