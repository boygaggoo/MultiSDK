package com.multisdk.library.network.serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public @interface ByteField {
  public int index();

  public int bytes() default -1;

  public String description() default "";
}
