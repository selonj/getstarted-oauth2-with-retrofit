package com.selonj.retrofit.oauth2;

import java.io.IOException;

/**
 * Created by Administrator on 2016-04-19.
 */
public interface AccessToken {
  String fetch() throws IOException;
}
