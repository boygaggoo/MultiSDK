package com.xdd.pay.util;

public class ReflectHiddenFuncException extends Exception {
  private static final long serialVersionUID = 1L;

  public ReflectHiddenFuncException() {
  }

  public ReflectHiddenFuncException(final String message) {
    super(message);
  }

  public ReflectHiddenFuncException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ReflectHiddenFuncException(final Throwable cause) {
    super(cause);
  }
}
