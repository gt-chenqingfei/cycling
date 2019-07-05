package com.beastbikes.android.modules.strava.biz;

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

public class StravaManager extends AbstractBusinessObject {

    static final String TAG = "StravaManager";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StravaManager.class);
    private static final String BASE_URL = "https://www.strava.com";
    public static final int CLIENT_ID = 13697;
    private static final String CLIENT_SECRET = "ac9ad6125c73f55e68800a4982a380eea4c153a2";

    private StravaServiceStub authStub;

    public StravaManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        final BeastBikes app = (BeastBikes) context.getApplicationContext();

        final Map<String, String> params = new TreeMap<>();
        params.put("User-Agent", RestfulAPI.buildUserAgent(context));
        params.put("X-Client-Lang", Locale.getDefault().getLanguage());
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        authStub = factory.create(StravaServiceStub.class, BASE_URL, params);
    }


    public JSONObject tokenExchange(final String code) {

        return authStub.token(CLIENT_ID, CLIENT_SECRET, code);
    }

    public JSONObject deAuthorize(String accessToken) {
        return authStub.deauthorize(accessToken);
    }


}