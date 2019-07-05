package com.beastbikes.android.modules.qiniu;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by caoxiao on 16/5/10.
 */
public class QiNiuManager extends AbstractBusinessObject implements Constants {

    private Context context;
    private QiNiuStub qiNiuStub;

    private final String QI_NIU_UPLOAD_URL = "http://bazaar.speedx.com/";

    private final int INVALID_TOKEN_STATUS_CODE = -5;
    private final int EXPIRED_TOKEN_STATUS_CODE = 401;
    private final int NO_TOKEN_STATUS_CODE = -4;

    private final int FAIL_TO_CONNECT = -1004;

    private int requestCount = 0;

    private final int UPLOADBYPATH = 1;
    private final int UPLOADBYFILE = 2;
    private int fileType = UPLOADBYPATH;

    private Logger logger = LoggerFactory.getLogger(QiNiuManager.class);

    private TokenInvailThread tokenInvailThread;

    //    private String keyTitle;
    private String key;
    private String qiNiuToken = "";
    private String path;
    private File file;

    private final String tokenKeyTitle = "bazaar:";
    private String tokenKey = "";

    private QiNiuUploadCallBack qiNiuUploadCallBack;
    private QiNiuProgressCallback qiNiuProgressCallback;

    private final String debugAvaterKey = "testerAvatar/";
    private final String avaterKey = "avatar/";

    private final String debugClubLogo = "testerClubLogo/";
    private final String clubLogo = "clubLogo/";

    private final String debugRouteMap = "testerRouteMap/";
    private final String routeMap = "routeMap/";

    private final String debugClubFeed = "testerClubFeedAlbum/";
    private final String clubFeed = "clubFeedAlbum/";

    private final String debugClubActivity = "testerClubActivity/";
    private final String clubActivity = "clubActivity/";

    private final String debugFeedback = "testerFeedbackImage/";
    private final String feedback = "feedbackImage/";

    public QiNiuManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        this.context = context;
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.qiNiuStub = factory.create(QiNiuStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(context));
    }

    public String qiniuTokenRequest() {
        try {
            final JSONObject jsonObject = this.qiNiuStub.getQiniuToken(tokenKeyTitle + tokenKey);
            if (null == jsonObject) {
                return "";
            }
            int code = jsonObject.optInt("code");
            if (code == 0) {
                String token = jsonObject.optString("result");
                if (!TextUtils.isEmpty(token)) {
                    qiNiuToken = token;
                }
                return token;
            }
        } catch (Exception e) {
            logger.error("e", e.getMessage());
        }
        return "";
    }

    public void uploadFile(String fileName, String path, String tokenKey) {
        this.tokenKey = tokenKey;
        this.fileType = UPLOADBYPATH;
        this.path = path;
        requestCount = 0;
        this.key = fileName;
        getQiNiuToken();
    }

    public void uploadFile(String fileName, File file) {
        this.tokenKey = "";
        this.fileType = UPLOADBYFILE;
        this.requestCount = 0;
        this.file = file;
        this.key = fileName;
        getQiNiuToken();
    }

    public void uploadFile(String fileName, File file, String tokenKey) {
        this.tokenKey = tokenKey;
        this.fileType = UPLOADBYFILE;
        this.requestCount = 0;
        this.file = file;
        this.key = fileName;
        getQiNiuToken();
    }

    public void uploadFile(String fileName, File file, String tokenKey,QiNiuUploadCallBack qiNiuUploadCallBack) {
        this.qiNiuUploadCallBack = qiNiuUploadCallBack;
        this.tokenKey = tokenKey;
        this.fileType = UPLOADBYFILE;
        this.requestCount = 0;
        this.file = file;
        this.key = fileName;
        getQiNiuToken();
    }

    public void setQiNiuUploadCallBack(QiNiuUploadCallBack qiNiuUploadCallBack) {
        this.qiNiuUploadCallBack = qiNiuUploadCallBack;
    }

    public void setQiNiuProgressCallback(QiNiuProgressCallback qiNiuProgressCallback) {
        this.qiNiuProgressCallback = qiNiuProgressCallback;
    }

    private void uploadFileByPath() {
        UploadManager uploadManager = getUploadManager(qiNiuUploadCallBack);
        if (uploadManager == null) {
            qiNiuUploadCallBack.onError();
            return;
        }
        uploadManager.put(path, key, qiNiuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                handleResult(key, info, response);
            }
        }, uploadOptions);
    }

    private void uploadFileByFile() {
        UploadManager uploadManager = getUploadManager(qiNiuUploadCallBack);
        if (uploadManager == null) {
            qiNiuUploadCallBack.onError();
            return;
        }
        uploadManager.put(file, key, qiNiuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                handleResult(key, info, response);
            }

        }, uploadOptions);
    }

    private UploadManager getUploadManager(final QiNiuUploadCallBack qiNiuUploadCallBack) {
        requestCount++;
        if (requestCount > 3) {
            qiNiuUploadCallBack.onError();
            return null;
        }
        UploadManager uploadManager = new UploadManager();
        return uploadManager;
    }

    private void handleResult(String key, ResponseInfo info, JSONObject response) {
        if (!TextUtils.isEmpty(info.error)) {
            logger.error("uploadManager error ", info.error);
            Toasts.show(context, info.error);
        }
        if (INVALID_TOKEN_STATUS_CODE == info.statusCode || EXPIRED_TOKEN_STATUS_CODE == info.statusCode || NO_TOKEN_STATUS_CODE == info.statusCode) {
            logger.error("statusCode", info.statusCode + "");
            getQiNiuToken();
            return;
        }
        if (FAIL_TO_CONNECT == info.statusCode) {
            qiNiuUploadCallBack.onError();
            return;
        }
        if (info.isOK()) {
            if (qiNiuUploadCallBack != null)
                qiNiuUploadCallBack.onComplete(QI_NIU_UPLOAD_URL + key);
        }
    }

    private void getQiNiuToken() {
        tokenInvailThread = new TokenInvailThread();
        tokenInvailThread.start();
    }

    public String getAvaterTokenKey() {
        return BeastBikes.isDebug ? debugAvaterKey : avaterKey;
    }

    public String getClubLogo() {
        return BeastBikes.isDebug ? debugClubLogo : clubLogo;
    }

    public String getRouteMapTokenKey() {
        return BeastBikes.isDebug ? debugRouteMap : routeMap;
    }

    public String getClubFeedTokenKey() {
        return BeastBikes.isDebug ? debugClubFeed : clubFeed;
    }

    public String getClubActivityTokenKey() {
        return BeastBikes.isDebug ? debugClubActivity : clubActivity;
    }

    public String getFeedbackTokenKey() {
        return BeastBikes.isDebug ? debugFeedback : feedback;
    }

    private class TokenInvailThread extends Thread {

        @Override
        public void run() {
            qiniuTokenRequest();
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (fileType) {
                        case UPLOADBYPATH:
                            uploadFileByPath();
                            break;
                        case UPLOADBYFILE:
                            uploadFileByFile();
                            break;
                    }
                }
            });
        }
    }

    UploadOptions uploadOptions = new UploadOptions(null, null, false,
            new UpProgressHandler() {
                public void progress(String key, double percent) {
//                    logger.info("qiniu" + key + ": " + percent);
                    if (qiNiuProgressCallback != null)
                        qiNiuProgressCallback.progressCallBack(key, percent);
                }
            }, null);
}
