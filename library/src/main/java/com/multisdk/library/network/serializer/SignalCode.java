package com.multisdk.library.network.serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) public @interface SignalCode {
  public boolean encrypt() default false;

  public int messageCode() default 0;
}
