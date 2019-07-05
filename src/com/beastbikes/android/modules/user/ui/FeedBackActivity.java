package com.beastbikes.android.modules.user.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.dialog.Wheelview;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ImageUploader;
import com.beastbikes.android.modules.cycling.club.dto.ImageInfo;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.utils.LogUtil;
import com.beastbikes.android.widget.WheelViewPopupWindow;
import com.beastbikes.android.widget.multiimageselector.MultiImageSelectorActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.android.utils.TelephonyUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenqingfei on 16/6/14.
 */
@MenuResource(R.menu.activity_complain_menu)
@LayoutResource(R.layout.activity_feedback)
public class FeedBackActivity extends SessionFragmentActivity implements View.OnClickListener, QiNiuUploadCallBack {
    public static final int REQ_SELECT_IMAGE = 0xff11;

    @IdResource(R.id.tab_app_error)
    TextView tvTabError;

    @IdResource(R.id.tab_product_suggest)
    TextView tvTabProSuggest;

    @IdResource(R.id.feedback_issue_type)
    View issueType;

    @IdResource(R.id.tv_feedback_issue_type)
    TextView issueTypeText;

    @IdResource(R.id.feedback_suggest_et)
    EditText etSuggest;

    @IdResource(R.id.feedback_iv_picture)
    ImageView ivPicture;

    @IdResource(R.id.feedback_upload_log_cb)
    CheckBox cbUploadLog;

    @IdResource(R.id.feedback_upload_log_ll)
    View llUploadLog;

    @IdResource(R.id.feedback_contact_ll)
    View llContact;

    @IdResource(R.id.feedback_contact)
    EditText etContact;

    @IdResource(R.id.container)
    View container;

    ArrayList<String> issueTypeList = new ArrayList<>();

    private WheelViewPopupWindow popupWindow;

    private String picPath;
    private int type = 0;
    private String detailText = null;
    private String content = null;
    private StringBuilder logIds = null;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
        String[] issueArray = getResources().getStringArray(R.array.feedback_issue_type);
        for (String v : issueArray) {
            issueTypeList.add(v);
        }
        initView();
    }

    private void initView() {
        tvTabError.setOnClickListener(this);
        tvTabError.setTag(false);
        tvTabError.setTextColor(0xffd62424);
        tvTabError.setBackgroundResource(R.drawable.bg_feedback_tab);

        tvTabProSuggest.setOnClickListener(this);
        issueType.setOnClickListener(this);
        ivPicture.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_complain_menu_commit:
                doSubmit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_app_error:
            case R.id.tab_product_suggest:
                tabSwitch();
                break;
            case R.id.feedback_issue_type:
                this.popupWindow = new WheelViewPopupWindow(this, issueTypeList, 2, new Wheelview.WheelSelectIndexListener() {
                    @Override
                    public void endSelect(int index, String value) {
                        if (!TextUtils.isEmpty(value)) {
                            content = value;
                            issueTypeText.setText(content);
                        }
                    }
                });
                this.popupWindow.showAtLocation(container, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.feedback_iv_picture:
                getImages();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQ_SELECT_IMAGE:
                        ArrayList<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        if (list != null && list.size() > 0) {
                            picPath = list.get(0);
                            Picasso.with(this).load("file://" + picPath).fit()
                                    .centerCrop().error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar).into(this.ivPicture);
                        }
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onComplete(String key) {
//            Toasts.show(this, "Upload log success");
    }

    @Override
    public void onError() {

    }

    private void tabSwitch() {
        Boolean b = (Boolean) tvTabError.getTag();
        if (b) {
            tvTabError.setTag(false);
            tvTabError.setTextColor(0xffd62424);
            tvTabError.setBackgroundResource(R.drawable.bg_feedback_tab);

            tvTabProSuggest.setTextColor(0xff000000);
            tvTabProSuggest.setBackgroundResource(R.drawable.transparent);

            issueType.setVisibility(View.VISIBLE);
            llUploadLog.setVisibility(View.VISIBLE);
            llContact.setVisibility(View.VISIBLE);
            type = 0;
        } else {
            tvTabError.setTag(true);
            tvTabError.setTextColor(0xff000000);
            tvTabError.setBackgroundResource(R.drawable.transparent);

            tvTabProSuggest.setTextColor(0xffd62424);
            tvTabProSuggest.setBackgroundResource(R.drawable.bg_feedback_tab);
            issueType.setVisibility(View.GONE);
            llUploadLog.setVisibility(View.GONE);
            llContact.setVisibility(View.GONE);
            type = 1;
        }
    }

    private void getImages() {
        Intent it = new Intent(this, MultiImageSelectorActivity.class);
        it.putExtra(MultiImageSelectorActivity.EXTRA_GALLERY_FULL, true);
        it.putExtra(MultiImageSelectorActivity.EXTRA_IS_SHOWMAX, false);
        it.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        it.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                new ArrayList<String>());
        startActivityForResult(it, REQ_SELECT_IMAGE);
    }

    private void exportLog() {

        if (cbUploadLog.isChecked() && type == 0) {

            final PackageManager pm = getPackageManager();

            try {
                final ApplicationInfo ai = pm.getApplicationInfo(
                        this.getPackageName(), PackageManager.GET_META_DATA);

                if (LogUtil.exportLog(ai.dataDir + File.separator + "files"
                        + File.separator + "log", Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/beast")) {
//                        Toasts.showOnUiThread(this, "export log success");
                } else {
//                        Toasts.showOnUiThread(this, "export log failed");
                }
            } catch (PackageManager.NameNotFoundException e) {
                return;
            }
            this.uploadLog(new Date());
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void uploadLog(final Date date) {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String suffix = sdf.format(date) + ".log";
        final File log = new File(getFilesDir(), "log");
        final File[] logs = log.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(suffix);
            }
        });

        if (null == logs || logs.length < 1)
            return;

        logIds = new StringBuilder();

        final AVUser user = AVUser.getCurrentUser();
        final String sn = null != user ? user.getObjectId() : TelephonyUtils.getDeviceId(this);
        String prefix = LogUtil.logPath + String.valueOf(sn) + "_" + suffix;
        String logId = "android_" + String.valueOf(sn) + "_" + suffix;
        final SimpleDateFormat sdfSecond = new SimpleDateFormat("_HH-mm-ss");
        for (int i = 0; i < logs.length; i++) {
            String second = sdfSecond.format(new Date());
            prefix = LogUtil.stringInsert(prefix, second, prefix.length() - 5);
            logId = LogUtil.stringInsert(logId, second, logId.length() - 5);
            QiNiuManager qiNiuManager = new QiNiuManager(this);
            qiNiuManager.setQiNiuUploadCallBack(this);
            qiNiuManager.uploadFile(prefix, logs[i], prefix);
            logIds.append(logId);
            if (i > 0 && i < logs.length - 1) {
                logIds.append(",");
            }
        }
    }

    private void doSubmit() {
        String contact = null;


        if (!TextUtils.isEmpty(etSuggest.getText())) {
            detailText = etSuggest.getText().toString();
        } else {
            Toasts.show(this, R.string.feedback_issue_desc_not_null);
            return;
        }

        if (type == 0) {
            if (TextUtils.isEmpty(content)) {
                Toasts.show(this, R.string.feedback_issue_type_not_null);
                return;
            } else if (TextUtils.isEmpty(etContact.getText())) {
                Toasts.show(this, R.string.feedback_contact_not_null);
                return;
            }

        }

        if (!TextUtils.isEmpty(etContact.getText())) {
            contact = etContact.getText().toString();
        }

        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, Boolean>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog = new LoadingDialog(FeedBackActivity.this, getString(R.string.feedback_submitting), true);
                        loadingDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {
                        try {
                            String logId = null;
                            exportLog();

                            if (!TextUtils.isEmpty(logIds)) {
                                logId = logIds.toString();
                            }

                            JSONObject detail = new JSONObject();
                            File file = null;
                            if (!TextUtils.isEmpty(picPath)) {
                                file = new File(picPath);
                            }
                            if (file != null && file.exists()) {
                                ImageUploader uploader = new ImageUploader();
                                String fileName = null;
                                try {
                                    fileName = AlgorithmUtils.md5(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ImageInfo info = uploader.withFile(file, fileName, ImageUploader.TYPE_FEEDBACK).saveSync();
                                detail.put("image", info.getUrl());
                            }

                            detail.put("text", detailText);
                            return new UserManager(FeedBackActivity.this).feedback(content, params[0], type, detail.toString(), logId);
                        } catch (BusinessException e) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        if (result) {
                            Toasts.show(FeedBackActivity.this, R.string.feedback_submit_success);
                            FeedBackActivity.this.finish();
                        }

                    }

                }, contact);
    }

}
