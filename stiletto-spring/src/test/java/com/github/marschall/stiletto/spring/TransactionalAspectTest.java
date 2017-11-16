package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.IllegalCharsetNameException;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalAspectTest {

  @Test
  public void rollbackOnDefault() {
    Transactional defaultTransactional = new SimpleTransactional(new Class[0], new String[0], new Class[0], new String[0]);
    assertTrue(TransactionalAspect.rollbackOn(new IllegalArgumentException(), defaultTransactional));
    assertTrue(TransactionalAspect.rollbackOn(new StackOverflowError(), defaultTransactional));
    assertFalse(TransactionalAspect.rollbackOn(new IOException(), defaultTransactional));
  }

  @Test
  public void rollbackOnClass() {
    Transactional transactional = new SimpleTransactional(new Class[] {IllegalArgumentException.class}, new String[0], new Class[] {RuntimeException.class}, new String[0]);
    assertTrue(TransactionalAspect.rollbackOn(new IllegalCharsetNameException("illegal"), transactional));

    transactional = new SimpleTransactional(new Class[] {RuntimeException.class}, new String[0], new Class[] {IllegalArgumentException.class}, new String[0]);
    assertFalse(TransactionalAspect.rollbackOn(new IllegalCharsetNameException("illegal"), transactional));
  }

  @Test
  public void rollbackOnClassName() {
    Transactional transactional = new SimpleTransactional(new Class[0], new String[] {"java.io"}, new Class[0], new String[] {"java.lang"});
    assertTrue(TransactionalAspect.rollbackOn(new IOException(), transactional));

    transactional = new SimpleTransactional(new Class[0], new String[] {"java.lang"}, new Class[0], new String[] {"java.io"});
    assertFalse(TransactionalAspect.rollbackOn(new IOException(), transactional));
  }

  static final class SimpleTransactional implements Transactional {

    private final Class<? extends Throwable>[] rollbackFor;
    private final String[] rollbackForClassName;
    private final Class<? extends Throwable>[] noRollbackFor;
    private final String[] noRollbackForClassName;

    SimpleTransactional(Class<? extends Throwable>[] rollbackFor, String[] rollbackForClassName, Class<? extends Throwable>[] noRollbackFor, String[] noRollbackForClassName) {
      this.rollbackFor = rollbackFor;
      this.rollbackForClassName = rollbackForClassName;
      this.noRollbackFor = noRollbackFor;
      this.noRollbackForClassName = noRollbackForClassName;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return Transactional.class;
    }

    @Override
    public String value() {
      return "";
    }

    @Override
    public String transactionManager() {
      return "";
    }

    @Override
    public Propagation propagation() {
      return Propagation.REQUIRED;
    }

    @Override
    public Isolation isolation() {
      return Isolation.DEFAULT;
    }

    @Override
    public int timeout() {
      return TransactionDefinition.TIMEOUT_DEFAULT;
    }

    @Override
    public boolean readOnly() {
      return false;
    }

    @Override
    public Class<? extends Throwable>[] rollbackFor() {
      return this.rollbackFor;
    }

    @Override
    public String[] rollbackForClassName() {
      return this.rollbackForClassName;
    }

    @Override
    public Class<? extends Throwable>[] noRollbackFor() {
      return this.noRollbackFor;
    }

    @Override
    public String[] noRollbackForClassName() {
      return this.noRollbackForClassName;
    }

  }

}
