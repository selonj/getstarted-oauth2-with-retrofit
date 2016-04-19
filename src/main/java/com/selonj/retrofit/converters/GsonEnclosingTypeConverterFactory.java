package com.selonj.retrofit.converters;

import com.google.gson.Gson;
import com.selonj.gson.enclosed.annotation.Enclosed;
import com.selonj.gson.enclosed.dsl.Enclosing;
import com.selonj.retrofit.utils.Annotations;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016-04-19.
 */
public class GsonEnclosingTypeConverterFactory extends Converter.Factory {
  private Converter.Factory delegate;

  public static GsonEnclosingTypeConverterFactory create() {
    return new GsonEnclosingTypeConverterFactory(GsonConverterFactory.create());
  }

  @Override public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    return delegate.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
  }

  @Override public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations, Retrofit retrofit) {
    Enclosed annotation = Annotations.getAnnotation(Enclosed.class, annotations);
    if (annotation != null) {
      final Gson gson = Enclosing.with(usingCustomType(annotation) ? (Class) type : annotation.type()).on(annotation.value()).slight();
      return new Converter<ResponseBody, Object>() {
        @Override public Object convert(ResponseBody value) throws IOException {
          return gson.fromJson(value.string(), type);
        }
      };
    }
    return delegate.responseBodyConverter(type, annotations, retrofit);
  }

  private boolean usingCustomType(Enclosed annotation) {
    return annotation.type() == void.class;
  }

  @Override public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
    return delegate.stringConverter(type, annotations, retrofit);
  }

  public GsonEnclosingTypeConverterFactory(Converter.Factory delegate) {
    this.delegate = delegate;
  }
}
