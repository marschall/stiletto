package com.github.marschall.stiletto.processor;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;

/**
 * Utility methods for dealing with {@link javax.lang.model.element}
 * and {@link javax.lang.model.type} types.
 */
final class AptUtils {

  private AptUtils() {
    throw new IllegalArgumentException("not instantiable");
  }

  static String getQualifiedName(Element element) {
    return asTypeElement(element).getQualifiedName().toString();
  }

  static String getSimpleName(TypeElement element) {
    return element.getSimpleName().toString();
  }

  static DeclaredType asDeclaredType(TypeMirror type) {
    return type.accept(DeclaredTypeExtractor.INSTANCE, null);
  }


  static TypeElement asTypeElement(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null);
  }


  static TypeMirror asTypeMirror(AnnotationValue annotationValue) {
    return annotationValue.accept(TypeMirrorExtractor.INSTANCE, null);
  }

  static AnnotationMirror asAnnotationMirror(AnnotationValue annotationValue) {
    return annotationValue.accept(AnnotationMirrorExtractor.INSTANCE, null);
  }

  static List<? extends AnnotationValue> asAnnotationValues(AnnotationValue annotationValue) {
    return annotationValue.accept(AnnotationsMirrorExtractor.INSTANCE, null);
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

}
