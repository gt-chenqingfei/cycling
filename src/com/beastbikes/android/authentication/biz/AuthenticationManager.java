package com.beastbikes.android.authentication.biz;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationException;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedService;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.persistence.BeastPersistenceManager;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.persistence.DataAccessObject;
import com.beastbikes.framework.persistence.PersistenceException;

import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AuthenticationManager extends AbstractBusinessObject {

    static final String TAG = "AuthenticationManager";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationManager.class);

    public static interface AuthenticationCallback {

        void onResult(AuthenticationException authenticationException);
    }

    private final UserManager userManager;
    private final DataAccessObject<LocalUser> userDao;
    private AuthenticationServiceStub authStub;

    public AuthenticationManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        final BeastBikes app = (BeastBikes) context.getApplicationContext();

        final BeastPersistenceManager bps = app.getPersistenceManager();
        this.userDao = bps.getDataAccessObject(LocalUser.class);
        this.userManager = new UserManager(context);

        final Map<String, String> params = new TreeMap<>();
        params.put("User-Agent", RestfulAPI.buildUserAgent(context));
        params.put("X-Client-Lang", Locale.getDefault().getLanguage());
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        authStub = factory.create(AuthenticationServiceStub.class, RestfulAPI.BASE_URL, params);
    }

    public void signIn() {
        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            LocalUser upo = new LocalUser();
            upo.setPassword(AVUser.getPwd());
            upo.setUsername(user.getUsername());
            signIn(upo, null, user.getSignType());
        }
    }

    public void signIn(final LocalUser upo, final AuthenticationCallback ac, final int type) {

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... strings) {
                try {
                    return authStub.signIn(upo.getUsername(), upo.getPassword(), type, upo.getNickname());
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                if (null == jsonObject) {
                    if (ac != null) {
                        ac.onResult(new AuthenticationException(0, "server connect error!"));
                    }
                } else {
                    if (0 != jsonObject.optInt("code")) {
                        if (ac != null) {
                            ac.onResult(new AuthenticationException(
                                    jsonObject.optInt("code"), jsonObject.optString("message")));
                        }
                    } else {
                        JSONObject result = jsonObject.optJSONObject("result");
                        if (result == null && ac != null) {
                            ac.onResult(new AuthenticationException(0, "server connect error!"));
                            return;
                        }
                        doParseUserInfo(jsonObject, upo, type);
                        if (ac != null) {
                            ac.onResult(null);
                        }
                        ClubFeedService.init();
                        RestfulAPI.cookieSync();
                    }
                }
            }
        }.execute();

    }

    public void signUp(final LocalUser upo, final String vcode, final AuthenticationCallback ac, final int signType) {

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... strings) {
                try {
                    return authStub.signUp(upo.getNickname(), upo.getUsername(), upo.getPassword(), vcode);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                if (null == jsonObject) {
                    ac.onResult(new AuthenticationException(0, "server connect error!"));
                } else {
                    if (0 != jsonObject.optInt("code")) {
                        ac.onResult(new AuthenticationException(
                                jsonObject.optInt("code"), jsonObject.optString("message")));
                    } else {

                        JSONObject result = jsonObject.optJSONObject("result");
                        if (result == null) {
                            ac.onResult(new AuthenticationException(0, "server connect error!"));
                            return;
                        }
                        RestfulAPI.cookieSync();
                        doParseUserInfo(jsonObject, upo, signType);
                        ac.onResult(null);
                    }
                }
            }
        }.execute();

    }

    private void doParseUserInfo(JSONObject jsonObject, final LocalUser upo, final int type) {
        if (null != jsonObject) {
            JSONObject result = jsonObject.optJSONObject("result");
            if (result == null)
                return;

            JSONObject jsonUserInfo = result.optJSONObject("user_info");
            if (null == jsonUserInfo) {
                return;
            }

            ProfileDTO userProfile = new ProfileDTO(jsonUserInfo);

            AVUser user = new AVUser();
            user.setPassword(upo.getPassword());
            user.setSessionToken(result.optString("sessionId"));
            user.setUsername(upo.getUsername());
            String userId = userProfile.getUserId();
            if (TextUtils.isEmpty(userId)) {
                userId = jsonUserInfo.optString("objectId");
            }
            user.setObjectId(userId);
            user.setSignType(type);
            user.setAvatar(userProfile.getAvatar());
            user.setDisplayName(userProfile.getNickname());
            user.setClubName(userProfile.getClubName());
            user.setCity(userProfile.getCity());
            user.setGeoCode(jsonUserInfo.optString("geoCode"));
            user.setWeight(userProfile.getWeight());
            user.setClubId(jsonUserInfo.optString("clubId"));
            // v2.2.0-rc 新增内容
            user.setEmail(jsonUserInfo.optString("email"));
            user.setFansNum(jsonUserInfo.optInt("fansNum"));
            user.setFollowStatus(jsonUserInfo.optInt("followStatu"));
            user.setFollowerNum(jsonUserInfo.optInt("followNum"));

            //v2.4.1
            user.setSpeedxId(jsonUserInfo.optInt("speedxId"));
            AVUser.saveCurrentUser(user);

            syncUserInfo(upo, userProfile);
        }
    }

    public JSONObject findPasswordByEmail(String email) {
        try {
            return authStub.sendResetEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject sendSmscode(String mobilephone, String msgType) {
        try {
            return authStub.sendSmscode(mobilephone, msgType);
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject resetPasswordByMobile(String mobilephone,
                                            final String vcode, final String password) {
        try {
            return authStub.resetPasswordByMobile(mobilephone, vcode, password);
        } catch (Exception e) {
            return null;
        }
    }

    public void syncUserInfo(final LocalUser upo, final ProfileDTO ru) {
        final String userId = ru.getUserId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {

                final LocalUser lupo = new LocalUser();
                lupo.setId(userId);
                lupo.setUsername(ru.getUsername());
                if (TextUtils.isEmpty(ru.getNickname())) {
                    lupo.setNickname(upo.getNickname());
                } else {
                    lupo.setNickname(ru.getNickname());
                }
                lupo.setEmail(ru.getEmail());
                try {
                    lupo.setGender(ru.getSex());
                } catch (NumberFormatException nfe) {
                    lupo.setGender(0);
                }
                lupo.setHeight(ru.getHeight());
                lupo.setWeight(ru.getWeight());
                lupo.setProvince(ru.getProvince());
                lupo.setCity(ru.getCity());
                lupo.setDistrict(ru.getDistrict());
                lupo.setTotalDistance(ru.getTotalDistance());

                // 新增v2.2.0-rc
                lupo.setFansNum(ru.getFansNum());
                lupo.setFollowStatus(ru.getFollowStatu());
                lupo.setFollowerNum(ru.getFollowNum());
                // 新增v2.4.0
                lupo.setMedalNum(ru.getMedalNum());

                try {
                    userDao.createOrUpdate(lupo);
                } catch (PersistenceException pe) {
                    Log.e(TAG, "Persist user error");
                }

            }
        }).start();
    }


    public void logOut() {
        AVUser.saveCurrentUser(null);
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... strings) {
                try {
                    return authStub.signOut();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                if (jsonObject != null) {
                    logger.info(jsonObject.toString());
                }
            }
        }.execute();
    }
}