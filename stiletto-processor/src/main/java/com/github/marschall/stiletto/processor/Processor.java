package com.github.marschall.stiletto.processor;

import static javax.lang.model.SourceVersion.RELEASE_8;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("com.github.marschall.stiletto.api.generation.GenerateAspects")
@SupportedSourceVersion(RELEASE_8)
public class Processor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // TODO Auto-generated method stub
        return false;
    }

}
