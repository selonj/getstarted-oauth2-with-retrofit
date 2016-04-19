package test.com.selonj.supports;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016-04-19.
 */
public interface AccessTokenService {
  @GET("oauth/token.json?grant_type=password")
  Call<OAuth2AccessToken> fetch(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("username") String username, @Query("password") String password);

  @GET("oauth/token.json?grant_type=refresh_token")
  Call<OAuth2AccessToken> refresh(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("refresh_token") OAuth2AccessToken token);
}
