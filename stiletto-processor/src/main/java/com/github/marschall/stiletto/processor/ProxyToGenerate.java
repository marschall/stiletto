package com.github.marschall.stiletto.processor;

import java.util.Objects;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

final class ProxyToGenerate {

  private final String targetClassName;
  private final String aspectClassName;
  private final TypeElement targetClassElement;
  private final TypeMirror aspect;

  ProxyToGenerate(String targetClassName, String apectClassName, TypeElement targetClassElement, TypeMirror aspect) {
    this.targetClassName = targetClassName;
    this.aspectClassName = apectClassName;
    this.targetClassElement = targetClassElement;
    this.aspect = aspect;
  }

  TypeElement getTargetClassElement() {
    return this.targetClassElement;
  }

  TypeMirror getAspect() {
    return this.aspect;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ProxyToGenerate)) {
      return false;
    }
    ProxyToGenerate other = (ProxyToGenerate) obj;
    return this.targetClassName.equals(other.targetClassName)
            && this.aspectClassName.equals(other.aspectClassName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.targetClassName, this.aspectClassName);
  }

  @Override
  public String toString() {
    return "aspect: " + this.aspectClassName + " -> target class:" + this.targetClassName;
  }

}