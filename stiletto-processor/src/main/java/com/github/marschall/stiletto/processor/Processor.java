package com.github.marschall.stiletto.processor;

import static com.github.marschall.stiletto.processor.Processor.GENERATE_ASPECTS;
import static com.github.marschall.stiletto.processor.Processor.GENERATE_ASPECT;
import static javax.lang.model.SourceVersion.RELEASE_8;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

@SupportedAnnotationTypes({GENERATE_ASPECT, GENERATE_ASPECTS})
@SupportedSourceVersion(RELEASE_8)
public class Processor extends AbstractProcessor {

  static final String GENERATE_ASPECTS = "com.github.marschall.stiletto.api.generation.GenerateAspects";

  static final String GENERATE_ASPECT = "com.github.marschall.stiletto.api.generation.GenerateAspect";

  private ProcessingEnvironment processingEnv;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    TypeElement generateAspects = this.processingEnv.getElementUtils().getTypeElement(GENERATE_ASPECTS);
    TypeElement generateAspect = this.processingEnv.getElementUtils().getTypeElement(GENERATE_ASPECT);
    ExecutableElement generateAspectsValue = getValueMethod(generateAspects);
    ExecutableElement generateAspectValue = getValueMethod(generateAspect);

    for (Element processedClass : roundEnv.getElementsAnnotatedWith(generateAspects)) {
      for (AnnotationMirror annotation : processedClass.getAnnotationMirrors()) {
        AnnotationValue annotationValue = annotation.getElementValues().get(generateAspectsValue);
        AnnotationMirror generateAspectAnnotation = annotationValue.accept(AnnotationMirrorExtractor.INSTANCE, null);
//        annotationValue.
      }
    }
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(generateAspect)) {
      for (AnnotationMirror annotation : processedClass.getAnnotationMirrors()) {
        AnnotationValue annotationValue = annotation.getElementValues().get(generateAspectValue);
        TypeMirror aspectClass = annotationValue.accept(TypeMirrorExtractor.INSTANCE, null);
        DeclaredType declaredType = (DeclaredType) aspectClass;
//        annotationValue.
      }
    }
    return true;
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

    @Override
    protected AnnotationMirror defaultAction(Object o, Void p) {
      throw new IllegalArgumentException("unknown element type");
    }

  }

  static final class AspectToGenerate {

    private final String className;
    private final String apectClassName;

    AspectToGenerate(String className, String apectClassName) {
      this.className = className;
      this.apectClassName = apectClassName;
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

  }

}
