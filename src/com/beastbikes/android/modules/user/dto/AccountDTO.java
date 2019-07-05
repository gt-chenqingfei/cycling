package com.beastbikes.android.modules.user.dto;

import com.beastbikes.android.modules.user.dao.entity.LocalAccounts;

import org.json.JSONObject;

/**
 * Created by zhangyao on 2016/1/26.
 */
public class AccountDTO {

    public static final int STATUS_BOND= 1;

    private int authType;
    private int status;
    private String accessToken;
    private String thirdNick;
    private String authKey;
    private int resID;
    private String accountName;

    public AccountDTO(String accountName, int resID, int auth_type) {
        this.resID = resID;
        this.accountName = accountName;
        this.authType = auth_type;
    }

    public AccountDTO(JSONObject result) {
        this.status = result.optInt("status");
        this.accessToken = result.optString("auth_token");
        this.authKey = result.optString("auth_key");
        this.authType = result.optInt("auth_type");
        this.thirdNick = result.optString("third_nick");
    }

    public AccountDTO(LocalAccounts localAccounts) {
        this.status = (int) localAccounts.getStatus();
        this.accessToken = localAccounts.getAuthToken();
        this.authKey = localAccounts.getAuthkey();
        this.authType = (int) localAccounts.getAuthType();
        this.thirdNick = localAccounts.getThirdNick();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getThirdNick() {
        return thirdNick;
    }

    public void setThirdNick(String thirdNick) {
        this.thirdNick = thirdNick;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "authType='" + authType + '\'' +
                ", status=" + status +
                ", accessToken='" + accessToken + '\'' +
                ", authKey='" + authKey + '\'' +
                '}';
    }
}
