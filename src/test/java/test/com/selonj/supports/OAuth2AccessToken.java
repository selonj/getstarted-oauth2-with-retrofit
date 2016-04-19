package test.com.selonj.supports;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016-04-19.
 */
public class OAuth2AccessToken {
  public static final int FIVE_SECONDS = 5;
  @SerializedName("access_token")
  public String accessToken;
  @SerializedName("refresh_token")
  public String refreshToken;
  @SerializedName("expires_in")
  public long expires;
  private long createAt = now();
  private int thresholds = FIVE_SECONDS;

  public boolean hasExpired() {
    return elapsedSeconds() >= expires - thresholds;
  }

  private long elapsedSeconds() {
    return (now() - createAt) / 1000;
  }

  private long now() {
    return System.currentTimeMillis();
  }

  @Override public String toString() {
    return refreshToken;
  }
}
