package com.github.marschall.stiletto.demo.aspect;

import java.lang.annotation.Annotation;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;

public class TransactionAspect2 {

  public void inTransaction(@DeclaredAnnotation Transactional transactional) {

  }

  static class X implements Transactional {

    @Override
    public Class<? extends Annotation> annotationType() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String value() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String transactionManager() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Propagation propagation() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Isolation isolation() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public int timeout() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public boolean readOnly() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public Class<? extends Throwable>[] rollbackFor() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String[] rollbackForClassName() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Class<? extends Throwable>[] noRollbackFor() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String[] noRollbackForClassName() {
      // TODO Auto-generated method stub
      return null;
    }

  }

}
