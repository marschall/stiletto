package com.github.marschall.stiletto.processor;

import static com.github.marschall.stiletto.processor.Processor.ANNOTATION_NAME;
import static javax.lang.model.SourceVersion.RELEASE_8;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(ANNOTATION_NAME)
@SupportedSourceVersion(RELEASE_8)
public class Processor extends AbstractProcessor {

  static final String ANNOTATION_NAME = "com.github.marschall.stiletto.api.generation.GenerateAspects";

  private ProcessingEnvironment processingEnv;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.processingEnv = processingEnv;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    TypeElement generateAspects = this.processingEnv.getElementUtils().getTypeElement(ANNOTATION_NAME);
    ExecutableElement generateAspectsValues = null;
    for (Element member : generateAspects.getEnclosedElements()) {
      if (member.getKind() == ElementKind.METHOD && member.getSimpleName().contentEquals("value")) {
        generateAspectsValues = (ExecutableElement) member;
      }
    }
    for (Element processedClass : roundEnv.getElementsAnnotatedWith(generateAspects)) {
      for (AnnotationMirror annotation : processedClass.getAnnotationMirrors()) {
        AnnotationValue annotationValue = annotation.getElementValues().get(generateAspectsValues);
//        annotationValue.
      }
    }
    return true;
  }

}
