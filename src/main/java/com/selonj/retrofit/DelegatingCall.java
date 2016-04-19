package com.selonj.retrofit;

import java.io.IOException;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016-04-19.
 */
public class DelegatingCall<R> implements Call<R> {
  protected final Call<R> target;

  public DelegatingCall(Call<R> target) {
    this.target = target;
  }

  @Override public Response<R> execute() throws IOException {
    return target.execute();
  }

  @Override public void enqueue(Callback<R> callback) {
    target.enqueue(callback);
  }

  @Override public boolean isExecuted() {
    return target.isExecuted();
  }

  @Override public void cancel() {
    target.cancel();
  }

  @Override public boolean isCanceled() {
    return target.isCanceled();
  }

  @Override public Call<R> clone() {
    return target.clone();
  }

  @Override public Request request() {
    return target.request();
  }
}
