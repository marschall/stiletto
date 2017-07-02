package com.github.marschall.stiletto.processor.el;

public class TargetClass {

  private final String fullyQualifiedName;
  private final String simpleName;

  public TargetClass(String fullyQualifiedName) {
    this.fullyQualifiedName = fullyQualifiedName;
    this.simpleName = getSimpleName(fullyQualifiedName);
  }

  private static String getSimpleName(String fullyQualifiedName) {
    int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
    if (lastDotIndex != -1) {
      return fullyQualifiedName.substring(lastDotIndex + 1, fullyQualifiedName.length());
    } else {
      return fullyQualifiedName;
    }
  }

  public String getFullyQualifiedName() {
    return this.fullyQualifiedName;
  }
  public String getSimpleName() {
    return this.simpleName;
  }

}
