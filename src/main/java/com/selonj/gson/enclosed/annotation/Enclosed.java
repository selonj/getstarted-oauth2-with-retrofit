package com.selonj.gson.enclosed.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2016-04-19.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Enclosed {
  String value() default "data";

  Class type() default void.class;
}
