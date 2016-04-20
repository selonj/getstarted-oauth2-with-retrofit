package test.com.selonj;

import com.selonj.retrofit.oauth2.AccessToken;
import com.selonj.retrofit.oauth2.OAuth2CallAdapterFactory;
import com.selonj.retrofit.converters.GsonEnclosingTypeConverterFactory;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import test.com.selonj.supports.AccessTokenService;
import test.com.selonj.supports.OAuth2AccessToken;
import test.com.selonj.supports.User;
import test.com.selonj.supports.UserClient;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Administrator on 2016-04-19.
 */
public class UserClientTest {

  private Retrofit retrofit;
  private UserClient client;
  private AccessTokenService tokenService;
  private String clientId = "zbnc-app";
  private String clientSecret = "a6ec5e9556a9362d8db1bdec4e66a813";
  private String username = "android";
  private String password = "a51d0c0ac2eca7068efb6523ac726e2c";

  @Before public void configure() throws Exception {
    retrofit = new Retrofit.Builder().
        baseUrl("http://192.168.1.166:8080/bentian/").
        addConverterFactory(GsonEnclosingTypeConverterFactory.create()).
        addCallAdapterFactory(new OAuth2CallAdapterFactory(new AccessToken() {
          private OAuth2AccessToken retrieved;

          @Override public String fetch() throws IOException {
            return fetchAccessToken().accessToken;
          }

          private OAuth2AccessToken fetchAccessToken() throws IOException {
            if (retrieved == null) {
              return retrieved = fetchBy("获取令牌：", tokenService.fetch(clientId, clientSecret, username, password));
            }
            if (retrieved.hasExpired()) {
              return retrieved = fetchBy("刷新令牌：", tokenService.refresh(clientId, clientSecret, retrieved));
            }
            System.out.println("使用已有的令牌：" + retrieved.accessToken);
            return retrieved;
          }

          private OAuth2AccessToken fetchBy(String tag, Call<OAuth2AccessToken> endpoint) throws IOException {
            System.out.println(tag + endpoint.request().url().toString());
            return endpoint.execute().body();
          }
        })).
        validateEagerly(true).
        build();
    client = retrofit.create(UserClient.class);
    tokenService = retrofit.create(AccessTokenService.class);
  }

  @Test public void retrievesUser() throws Exception {
    User user = client.fetch().execute().body();

    assertThat(user.username, equalTo("android"));
    assertThat(user.avatar, equalTo("/images/avatar2.jpg"));
    assertThat(user.mobile, equalTo("13122191011"));
  }
}
