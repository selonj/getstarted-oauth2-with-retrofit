package com.selonj.retrofit.utils;

import java.lang.annotation.Annotation;

/**
 * Created by Administrator on 2016-04-19.
 */
public class Annotations {
  public static boolean hasAnnotation(Class<? extends Annotation> annotationType, Annotation[] annotations) {
    return getAnnotation(annotationType, annotations) != null;
  }

  public static <T extends Annotation> T getAnnotation(Class<T> annotationType, Annotation[] annotations) {
    for (Annotation candidate : annotations) {
      if (annotationType.isInstance(candidate)) {
        return (T) candidate;
      }
    }
    return null;
  }
}
