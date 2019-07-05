package com.beastbikes.android.modules.cycling.club.ui.widget.richeditor;

import android.os.Handler;
import android.os.Message;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhangyao on 2016/3/15.
 */
public class PushImageManage {
    private ArrayList<String> images = new ArrayList<>();
    private Map<String, String> map = new HashMap<String, String>();
    private String html;
    private AsyncTaskQueue asyncTaskQueue;
    private OnReplaceImageURl onReplaceImageURl;

    private int current;

    public PushImageManage(String html, AsyncTaskQueue asyncTaskQueue) {
        this.images.addAll(Utils.getImageSrc(html));
        this.html = html;
        this.asyncTaskQueue = asyncTaskQueue;
    }

    public void pushImage(OnReplaceImageURl onReplaceImageURl) {
        this.onReplaceImageURl = onReplaceImageURl;
        if (images.isEmpty()) {
            onReplaceImageURl.onSuccess(html);
            return;
        }
        current = 0;
        uploadImage(images.get(current));
    }

    private void uploadImage(String path) {
        String aid = UUID.randomUUID().toString();
        final String md5Aid = AlgorithmUtils.md5(aid);
        QiNiuManager qiNiuManager = new QiNiuManager(BeastBikes.getInstance());
        String qiNiuTokenKey = qiNiuManager.getClubActivityTokenKey() + md5Aid;
        qiNiuManager.setQiNiuUploadCallBack(new QiNiuUploadCallBack() {
            @Override
            public void onComplete(String key) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = key;
                handler.sendMessage(message);
            }

            @Override
            public void onError() {
                if (onReplaceImageURl != null)
                    onReplaceImageURl.onfail();
            }
        });
        qiNiuManager.uploadFile(qiNiuTokenKey, path, qiNiuTokenKey);
    }

    //1成功,-1失败
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                map.put(images.get(current), (String) msg.obj);
            }
            if (current >= images.size() - 1) {
                replaceImageURl();
                return;
            }
            current++;
            uploadImage(images.get(current));
        }

    };

    private void replaceImageURl() {
        for (String key : map.keySet()) {
            html = html.replace(key, map.get(key));
        }
        if (onReplaceImageURl != null) {
            onReplaceImageURl.onSuccess(html);
        }
    }

    public interface OnReplaceImageURl {
        void onSuccess(String html);

        void onfail();
    }


}
