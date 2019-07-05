package com.beastbikes.android.authentication;

/**
 * Created by chenqingfei on 16/8/10.
 */
public class AuthenticationBean {

    private String accessToken;
    private String openId;
    private String nickname;
    private String type;
    private String tokenSecret;

    public AuthenticationBean() {
    }

    public AuthenticationBean(String accessToken, String openId, String nickname, String tokenSecret) {
        this.accessToken = accessToken;
        this.openId = openId;
        this.nickname = nickname;
        this.tokenSecret = tokenSecret;
    }

    public AuthenticationBean(String accessToken, String openId, String nickname, String tokenSecret, String type) {
        this.accessToken = accessToken;
        this.openId = openId;
        this.nickname = nickname;
        this.tokenSecret = tokenSecret;
        this.type = type;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
}
