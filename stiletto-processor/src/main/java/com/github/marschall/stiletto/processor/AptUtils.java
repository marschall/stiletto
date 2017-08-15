package com.github.marschall.stiletto.processor;

import static javax.lang.model.element.Modifier.PRIVATE;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

/**
 * Utility methods for dealing with {@link javax.lang.model.element}
 * and {@link javax.lang.model.type} types.
 */
final class AptUtils {

  private final Types types;

  AptUtils(Types types) {
    this.types = types;
  }

  boolean containsAnyNonVoid(List<? extends ExecutableElement> methods) {
    for (ExecutableElement method : methods) {
      if (method.getReturnType().getKind() != TypeKind.VOID) {
        return true;
      }
    }
    return false;
  }

  String getSignature(ExecutableElement executableElement) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(executableElement.getSimpleName());
    buffer.append('(');
    boolean first = true;
    for (VariableElement parameter : executableElement.getParameters()) {
      if (!first) {
        buffer.append(", ");
      }
      buffer.append(this.types.erasure(parameter.asType()).toString());
      first = false;
    }
    buffer.append(')');
    return buffer.toString();
  }

  String getQualifiedName(Element element) {
    return asTypeElement(element).getQualifiedName().toString();
  }

  String getSimpleName(TypeElement element) {
    return element.getSimpleName().toString();
  }

  DeclaredType asDeclaredType(TypeMirror type) {
    return type.accept(DeclaredTypeExtractor.INSTANCE, null);
  }

  TypeElement asTypeElement(Element element) {
    return element.accept(TypeElementExtractor.INSTANCE, null);
  }

  ExecutableElement asExecutableElement(Element element) {
    return element.accept(ExecutableElementExtractor.INSTANCE, null);
  }

  TypeMirror asTypeMirror(AnnotationValue annotationValue) {
    return annotationValue.accept(TypeMirrorExtractor.INSTANCE, null);
  }

  String asString(AnnotationValue annotationValue) {
    return annotationValue.accept(StringExtractor.INSTANCE, null);
  }

  AnnotationMirror asAnnotationMirror(AnnotationValue annotationValue) {
    return annotationValue.accept(AnnotationMirrorExtractor.INSTANCE, null);
  }

  List<? extends AnnotationValue> asAnnotationValues(AnnotationValue annotationValue) {
    return annotationValue.accept(AnnotationsMirrorExtractor.INSTANCE, null);
  }

  static abstract class ExpectedElementExtractor<R> extends SimpleElementVisitor8<R, Void> {

    @Override
    protected R defaultAction(Element e, Void p) {
      throw new IllegalArgumentException("unknown element type");
    }

  }

  List<ExecutableElement> getNonPrivateConstrctors(TypeElement element) {
    List<ExecutableElement> constrctors = new ArrayList<>(2);
    for (Element member : element.getEnclosedElements()) {
      member.accept(NonPrivateConstructorExtractor.INSTANCE, constrctors);
    }
    return constrctors;
  }

  static final class TypeElementExtractor extends ExpectedElementExtractor<TypeElement> {

    static final ElementVisitor<TypeElement, Void> INSTANCE = new TypeElementExtractor();

    @Override
    public TypeElement visitType(TypeElement e, Void p) {
      return e;
    }

  }

  static final class ExecutableElementExtractor extends ExpectedElementExtractor<ExecutableElement> {

    static final ElementVisitor<ExecutableElement, Void> INSTANCE = new ExecutableElementExtractor();

    @Override
    public ExecutableElement visitExecutable(ExecutableElement e, Void p) {
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

  static final class StringExtractor extends ExpectedValueExtractor<String> {

    static final AnnotationValueVisitor<String, Void> INSTANCE = new StringExtractor();

    @Override
    public String visitString(String s, Void p) {
      return s;
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

  static final class NonPrivateConstructorExtractor extends SimpleElementVisitor8<Void, List<ExecutableElement>> {

    static final ElementVisitor<Void, List<ExecutableElement>> INSTANCE = new NonPrivateConstructorExtractor();

    @Override
    public Void visitExecutable(ExecutableElement e, List<ExecutableElement> p) {
      if (e.getKind() == ElementKind.CONSTRUCTOR
              && !e.getModifiers().contains(PRIVATE)) {
        p.add(e);
      }
      return null;
    }

  }

}
