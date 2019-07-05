package com.beastbikes.android.modules.cycling.club.biz;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.modules.cycling.club.dto.ImageInfo;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;

import java.io.File;

/**
 * Created by chenqingfei on 16/5/26.
 */
public class ImageUploader {
    public static final int TYPE_USER_AVATAR = 0;
    public static final int TYPE_CLUB_FEED = 3;
    public static final int TYPE_CLUB_ACTIVITY = 4;
    public static final int TYPE_FEEDBACK = 5;

    private boolean uploading = true;
    private boolean toUpload = false;
    private ImageInfo info;
    private QiNiuManager qiNiuManager;
    private File file;
    private String tokenKey = "";
    private String filePath = "";
    private String fileName;

    public ImageUploader withFile(String filePath, String fileName, int type, int arg) {
        this.filePath = filePath;
        return withFile(null, fileName, type);
    }

    public ImageUploader withFile(File file, String fileName, int type) {
        this.file = file;
        this.fileName = fileName;
        this.uploading = true;
        this.toUpload = false;
        this.qiNiuManager = new QiNiuManager(BeastBikes.getInstance());
        this.info = new ImageInfo();
        this.info.setId(fileName);
        switch (type) {
            case TYPE_CLUB_FEED:
                tokenKey = qiNiuManager.getClubFeedTokenKey() + fileName;
                break;
            case TYPE_USER_AVATAR:
                tokenKey = qiNiuManager.getAvaterTokenKey() + fileName;
                break;
            case TYPE_CLUB_ACTIVITY:
                tokenKey = qiNiuManager.getClubActivityTokenKey() + fileName;
                break;
            case TYPE_FEEDBACK:
                tokenKey = qiNiuManager.getFeedbackTokenKey() + fileName;
                break;
        }
        return this;
    }

    public ImageInfo saveSync() {
        while (uploading) {

            if (!toUpload) {
                toUpload = true;
                if (file != null) {
                    qiNiuManager.uploadFile(tokenKey, file, tokenKey, new QiNiuUploadCallBack() {
                        @Override
                        public void onComplete(String key) {
                            uploading = false;
                            info.setUrl(key);
                        }

                        @Override
                        public void onError() {
                            uploading = false;
                        }
                    });
                } else if (filePath != null) {
                    qiNiuManager.uploadFile(fileName, filePath, tokenKey);
                    qiNiuManager.setQiNiuUploadCallBack(new QiNiuUploadCallBack() {
                        @Override
                        public void onComplete(String key) {
                            uploading = false;
                            info.setUrl(key);
                        }

                        @Override
                        public void onError() {
                            uploading = false;
                        }
                    });
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    public void saveInBackground(QiNiuUploadCallBack callBack) {
        qiNiuManager.uploadFile(fileName, filePath, tokenKey);
        qiNiuManager.setQiNiuUploadCallBack(callBack);
    }

}
