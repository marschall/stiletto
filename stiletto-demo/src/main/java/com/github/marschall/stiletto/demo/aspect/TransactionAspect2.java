package com.github.marschall.stiletto.demo.aspect;

import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;

public class TransactionAspect2 {

  public void inTransaction(@DeclaredAnnotation Transactional transactional) {

  }

}
