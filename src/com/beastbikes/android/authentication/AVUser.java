package com.beastbikes.android.authentication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.utils.RSAUtils;
import com.beastbikes.framework.android.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;


public class AVUser implements Serializable {

    private static final Logger logger = LoggerFactory
            .getLogger(AVUser.class);

    public static final int SIGN_TYPE_EMAIL = 1;
    public static final int SIGN_TYPE_PHONE = 2;
    public static final int SIGN_TYPE_WEIBO = 4;
    public static final int SIGN_TYPE_QQ = 8;
    public static final int SIGN_TYPE_WECHAT = 16;
    public static final int SIGN_TYPE_TWITTER = 32;
    public static final int SIGN_TYPE_FACEBOOK = 64;
    public static final int SIGN_TYPE_GOOGLE_PLUS = 128;
    public static final int SIGN_TYPE_STRAVA = 256;

    public static final String SP_KEY = AVUser.class.getSimpleName();
    public static final String SP_KEY_PWD = AVUser.class.getSimpleName() + "PWD";

    private String sessionToken;
    private String username;
    private int signType;
    private String password;
    private String mobilePhoneNumber;
    private String email;
    private String thirdToken;
    private String objectId;
    private String avatar;
    private String displayName;
    private String city;
    private String clubName;
    private String clubId;
    private String geoCode;
    private double weight;

    private int fansNum;
    private int followerNum;
    private int followStatus;

    private int speedxId;

    private static AVUser instance = null;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getThirdToken() {
        return thirdToken;
    }

    public void setThirdToken(String thirdToken) {
        this.thirdToken = thirdToken;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(int followStatus) {
        this.followStatus = followStatus;
    }

    public int getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(int followerNum) {
        this.followerNum = followerNum;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static AVUser getCurrentUser() {
        // return getCurrentUser(AVUser.class);
        if (instance == null) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(BeastBikes.getInstance());
            String data = sp.getString(SP_KEY, null);
            if (null != data) {
                instance = (AVUser) json2Object(data);
            }
        }
        return instance;
    }

    /**
     * 保存当前用户
     *
     * @param user
     */
    public static void saveCurrentUser(AVUser user) {
        instance = user;

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BeastBikes.getInstance());
        String jsonString = null;

        if (null == user) {
            sp.edit().remove(SP_KEY).commit();
            sp.edit().remove(SP_KEY_PWD).commit();
            return;
        }
        savePwd(user.getPassword());
        jsonString = object2JsonString(user);
        sp.edit().putString(SP_KEY, jsonString).commit();
    }

    public static void updateCurrentUser(AVUser user) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BeastBikes.getInstance());

        String jsonString = null;
        jsonString = object2JsonString(user);
        sp.edit().putString(SP_KEY, jsonString).commit();
    }

    /**
     * AVUser 转成json串
     *
     * @param user
     * @return
     */
    private static String object2JsonString(AVUser user) {
        String jsonString = null;
        JSONObject object = new JSONObject();
        try {
            object.put("sessionToken", user.getSessionToken());
            object.put("username", user.getUsername());
            object.put("mobilePhoneNumber", user.getMobilePhoneNumber());
            object.put("email", user.getEmail());
            object.put("objectId", user.getObjectId());
            object.put("signType", user.getSignType());
            object.put("password", user.getPassword());
            object.put("avatar", user.getAvatar());
            object.put("displayName", user.getDisplayName());
            object.put("city", user.getCity());
            object.put("clubName", user.getClubName());
            object.put("thirdToken", user.getThirdToken());
            object.put("geoCode",user.getGeoCode());
            object.put("weight",user.getWeight());
            object.put("clubId",user.getClubId());
            // 新增的字段 v2.2.0
            object.put("fansNum", user.getFansNum());
            object.put("followerNum", user.getFollowerNum());
            object.put("followStatus", user.getFollowStatus());
            // 新增的字段 v2.4.1
            object.put("speedxId",user.getSpeedxId());
            jsonString = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * json串转化成AVUser 对象
     *
     * @param jsonString
     * @return
     */
    private static AVUser json2Object(String jsonString) {

        AVUser tmp = null;
        if (TextUtils.isEmpty(jsonString))
            return tmp;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            if (null != jsonObject) {
                int signType = jsonObject.optInt("signType");
                if (signType != 0) {
                    tmp = new AVUser();
                    tmp.setPassword(jsonObject.optString("password"));
                    tmp.setObjectId(jsonObject.optString("objectId"));
                    tmp.setUsername(jsonObject.optString("username"));
                    tmp.setSessionToken(jsonObject.optString("sessionToken"));
                    tmp.setMobilePhoneNumber(jsonObject.optString("mobilePhoneNumber"));
                    tmp.setEmail(jsonObject.optString("email"));
                    tmp.setAvatar(jsonObject.optString("avatar"));
                    tmp.setDisplayName(jsonObject.optString("displayName"));
                    tmp.setCity(jsonObject.optString("city"));
                    tmp.setClubName(jsonObject.optString("clubName"));
                    tmp.setThirdToken(jsonObject.optString("thirdToken"));
                    tmp.setSignType(signType);
                    tmp.setGeoCode(jsonObject.optString("geoCode"));
                    tmp.setWeight(jsonObject.optDouble("weight"));
                    tmp.setClubId(jsonObject.optString("clubId"));
                    tmp.setFansNum(jsonObject.optInt("fansNum"));
                    tmp.setFollowerNum(jsonObject.optInt("followerNum"));
                    tmp.setFollowStatus(jsonObject.optInt("followStatus"));
                    tmp.setSpeedxId(jsonObject.optInt("speedxId"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tmp;
    }

    private static void savePwd(String pwd) {
        try {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(BeastBikes.getInstance());

            // 从文件中得到公钥
            InputStream inPublic = BeastBikes.getInstance().getApplicationContext().getResources().getAssets().open("rsa_public_key.pem");
            PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
            // 加密
            byte[] encryptByte = RSAUtils.encryptData(pwd.getBytes(), publicKey);
            // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
            String afterencrypt = Base64.encodeToString(encryptByte, 0);

            sp.edit().putString(SP_KEY_PWD, afterencrypt).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getPwd() {
        String password = null;
        try {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(BeastBikes.getInstance());
            String encryptContent = sp.getString(SP_KEY_PWD, null);
            // 从文件中得到私钥
            if (!TextUtils.isEmpty(encryptContent)) {
                PrivateKey privateKey = RSAUtils.loadPrivateKey(BeastBikes.getUserPrivateKey());
                // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
                byte[] decryptByte = RSAUtils.decryptData(Base64.decode(encryptContent, 0), privateKey);
                password = new String(decryptByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * 获取leancloud 本地User数据
     * 这个目的做服务器迁移，老用户如果不获取用户信息
     * 有些用户可能会不记得自己的账号
     */
    public static AVUser initAVCloudUser() {
        AVUser user = getCurrentUser();
        if (user != null)
            return null;
        try {
            byte[] data = FileUtils.readContentBytesFromFile(currentUserArchivePath());

            String jsonString = data != null && data.length != 0 ? new String(data) : "";
            JSONObject jsonObject = null;
            if (!TextUtils.isEmpty(jsonString)) {
                try {
                    jsonObject = new JSONObject(jsonString);
                    if (jsonObject != null) {
                        JSONObject serverData = jsonObject.optJSONObject("serverData");
                        if (serverData != null) {
                            user = new AVUser();
                            JSONObject authData = serverData.optJSONObject("authData");
                            if (authData != null) {
                                JSONObject qq = authData.optJSONObject("qq");
                                JSONObject weibo = authData.optJSONObject("weibo");
                                if (qq != null) {
                                    user.setSignType(SIGN_TYPE_QQ);
                                    user.setUsername(qq.optString("openid"));
                                } else if (weibo != null) {
                                    user.setSignType(SIGN_TYPE_WEIBO);
                                    user.setUsername(weibo.optString("uid"));
                                }
                                user.setPassword("50b6w2e2hi");
                            } else if (jsonObject.has("email")) {

                                user.setSignType(SIGN_TYPE_EMAIL);
                                user.setEmail(jsonObject.optString("email"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    logger.error("Init AVUser error :", e);
                }
                saveCurrentUser(user);
            }
        } catch (Exception e) {
            logger.error("Init AVUser error :", e);
        }
        return user;
    }

    private static File currentUserArchivePath() {
        File file = new File(FileUtils.getPaasDocumentDir() + "/currentUser");
        return file;
    }


    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }
    public int getSpeedxId() {
        return speedxId;
    }

    public void setSpeedxId(int speedxId) {
        this.speedxId = speedxId;
    }
}