package test.com.selonj.supports;

import com.selonj.gson.enclosed.annotation.Enclosed;
import com.selonj.retrofit.oauth2.annotation.OAuth2;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2016-04-19.
 */
public interface UserClient {
  @OAuth2 @GET("user.json")
  @Enclosed(value="user") Call<User> fetch();
}
