package com.github.marschall.stiletto.processor;

interface NamingStrategy {

  String deriveClassName(String originalUnqualifiedName);

}
