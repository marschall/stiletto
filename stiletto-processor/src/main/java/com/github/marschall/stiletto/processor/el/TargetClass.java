package com.github.marschall.stiletto.processor.el;

public class TargetClass {

  private final String name;
  private final String simpleName;

  public TargetClass(String name) {
    this.name = name;
    this.simpleName = getSimpleName(name);
  }

  private static String getSimpleName(String name) {
    int lastDotIndex = name.lastIndexOf('.');
    if (lastDotIndex != -1) {
      return name.substring(lastDotIndex + 1, name.length());
    } else {
      return name;
    }
  }

  public String getName() {
    return this.name;
  }
  public String getSimpleName() {
    return this.simpleName;
  }

}
