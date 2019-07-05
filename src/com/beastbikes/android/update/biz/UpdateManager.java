package com.beastbikes.android.update.biz;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.update.dto.VersionInfo;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

/**
 * Created by chenqingfei on 16/7/18.
 */
public class UpdateManager extends AbstractBusinessObject {
    private UpdateStub updateStub = null;

    public interface CheckUpdateCallback {
        public void onUpdateAvailable(VersionInfo info);
    }

    private Context context;

    /**
     * Create an instance with the specified {@link BusinessContext}
     *
     * @param context The business context
     */
    public UpdateManager(Context context) {

        super((BusinessContext) context.getApplicationContext());
        this.context = context.getApplicationContext();
        final RestfulAPIFactory factory = new RestfulAPIFactory(this.context);
        this.updateStub = factory.create(UpdateStub.class, RestfulAPI.BASE_URL, RestfulAPI.getParams(this.context));
    }

    private VersionInfo getVersion() throws BusinessException {
        try {
            final JSONObject jsonObject = this.updateStub.checkLatestApkUpdate();
            if (null == jsonObject) {
                return null;
            }
            int code = jsonObject.optInt("code");
            if (code == 0) {

                JSONObject result = jsonObject.optJSONObject("result");
                if (result != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    VersionInfo d = mapper.readValue(result.toString(), VersionInfo.class);
                    return d;
                }

            }
        } catch (Exception e) {
            Log.e("UpdateManager", e.toString());
        }
        return null;
    }

    public static int getCurrentVersion(Context context) {
        final String pkg = context.getPackageName();
        final PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(pkg, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public VersionInfo checkUpdate() {
        VersionInfo versionInfo = null;
        try {
            versionInfo = getVersion();
            if (versionInfo != null) {
                int currentVersion = getCurrentVersion(this.context);
                if (versionInfo.getVersionCode() <= currentVersion) {
                    return null;
                }
            }
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return versionInfo;
    }
}
