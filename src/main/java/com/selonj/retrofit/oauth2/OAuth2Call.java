package com.selonj.retrofit.oauth2;

import com.selonj.retrofit.DelegatingCall;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016-04-19.
 */
public class OAuth2Call<R> extends DelegatingCall<R> {
  private final Retrofit retrofit;
  private Converter<ResponseBody, R> responseConverter;
  private AccessToken accessToken;

  public OAuth2Call(Call<R> target, Retrofit retrofit, Converter<ResponseBody, R> responseConverter, AccessToken accessToken) {
    super(target);
    this.retrofit = retrofit;
    this.responseConverter = responseConverter;
    this.accessToken = accessToken;
  }

  @Override public Response<R> execute() throws IOException {
    Call<R> custom = target.clone();

    Request request = custom.request().newBuilder().get().header("Authorization", "bearer " + accessToken.fetch()).build();
    okhttp3.Response rawResponse = retrofit.callFactory().newCall(request).execute();
    try {
      return Response.success(responseConverter.convert(rawResponse.body()), rawResponse);
    } finally {
      rawResponse.body().close();
    }
  }
}
