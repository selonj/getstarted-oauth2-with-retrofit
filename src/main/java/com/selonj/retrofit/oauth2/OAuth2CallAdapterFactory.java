package com.selonj.retrofit.oauth2;

import com.selonj.retrofit.oauth2.annotation.OAuth2;
import com.selonj.retrofit.utils.Annotations;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016-04-19.
 */
public class OAuth2CallAdapterFactory extends CallAdapter.Factory {

  private  AccessToken accessToken;

  public OAuth2CallAdapterFactory(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  @Override public CallAdapter<?> get(final Type returnType, final Annotation[] annotations, final Retrofit retrofit) {
    CallAdapter<?> kernel = retrofit.nextCallAdapter(this, returnType, annotations);
    if (!Annotations.hasAnnotation(OAuth2.class, annotations)) return kernel;
    return new OAuth2CallAdapter(kernel, retrofit, annotations);
  }

  private class OAuth2CallAdapter implements CallAdapter<Object> {
    private final CallAdapter<?> kernel;
    private final Retrofit retrofit;
    private Annotation[] annotations;

    public OAuth2CallAdapter(CallAdapter<?> kernel, Retrofit retrofit, Annotation[] annotations) {
      this.kernel = kernel;
      this.retrofit = retrofit;
      this.annotations = annotations;
    }

    @Override public Type responseType() {
      return kernel.responseType();
    }

    @Override public <R> Object adapt(final retrofit2.Call<R> call) {
      return new OAuth2Call<>(call, retrofit, (Converter<ResponseBody, R>) retrofit.responseBodyConverter(responseType(), annotations), accessToken);
    }
  }
}
