package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.preferences.ui.BaseEditTextActivity;
import com.beastbikes.android.modules.preferences.ui.CutAvatarActivity;
import com.beastbikes.android.modules.preferences.ui.EditTextActivity;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@MenuResource(R.menu.club_info_setting_menu)
@LayoutResource(R.layout.activity_club_info_setting)
public class ClubInfoSettingActivity extends SessionFragmentActivity implements
        OnClickListener, Constants {

    private static final int RC_EDIT_CLUB_LOGO_GALLERY = 2;

    private static final int RC_EDIT_CLUB_LOGO_CAMERA = 3;

    private static final int RC_EDIT_CLUB_LOGO_CROP = 4;

    public static final int RC_EDIT_CLUB_NAME = 5;

    public static final int RC_EDIT_CLUB_DESC = 6;

    @IdResource(R.id.activity_club_info_setting_logo)
    private RelativeLayout logo;

    @IdResource(R.id.activity_club_info_setting_logo_img)
    private CircleImageView logoIv;

    @IdResource(R.id.activity_club_info_setting_name)
    private RelativeLayout nameVg;

    @IdResource(R.id.activity_club_info_setting_name_value)
    private TextView nameTv;

    @IdResource(R.id.activity_club_info_setting_desc)
    private RelativeLayout descVg;

    @IdResource(R.id.activity_club_info_setting_desc_value)
    private TextView descTv;

    @IdResource(R.id.create_club_private_sv)
    private Switch privateClubSv;

    private ClubInfoCompact clubInfo;

    private ClubManager manager;

    private int isPrivateClub;

    private File tempFile = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            getPhotoFileName());

    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }


        this.manager = new ClubManager(this);

        this.logo.setOnClickListener(this);

        this.nameVg.setOnClickListener(this);
        this.descVg.setOnClickListener(this);

        this.fetchMyClub();

//        privateClubSv.setOnClickSwitchListener(new SwitchView.OnClickSwitchListener() {
//            @Override
//            public void onSwitchChanged(boolean open) {
//                if (open) {
//                    isPrivateClub = 1;
//                } else {
//                    isPrivateClub = 0;
//                }
//            }
//        });
        privateClubSv.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean open) {
                if (open) {
                    isPrivateClub = 1;
                } else {
                    isPrivateClub = 0;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_EDIT_CLUB_LOGO_GALLERY:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = null;
                    String picturePath = null;
                    try {
                        c = this.getContentResolver().query(selectedImage,
                                filePathColumns, null, null, null);
                        if (null != c && c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(filePathColumns[0]);
                            picturePath = c.getString(columnIndex);
                        } else {
                            AVAnalytics.onError(this, "get photo path failed:"
                                    + selectedImage.toString());
                        }
                    } catch (Exception e) {
                        AVAnalytics.onError(this, Log.getStackTraceString(e));
                    } finally {
                        if (null != c && !c.isClosed()) {
                            c.close();
                        }
                        if (TextUtils.isEmpty(picturePath))
                            picturePath = selectedImage.getPath();
                    }

                    Intent i = new Intent(this, CutAvatarActivity.class);
                    i.putExtra(CutAvatarActivity.EXTRA_AVATAR_PATH, picturePath);
                    startActivityForResult(i, RC_EDIT_CLUB_LOGO_CROP);
                }
                break;
            case RC_EDIT_CLUB_LOGO_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Intent i = new Intent(this, CutAvatarActivity.class);
                    i.putExtra(CutAvatarActivity.EXTRA_AVATAR_PATH,
                            tempFile.getAbsolutePath());
                    startActivityForResult(i, RC_EDIT_CLUB_LOGO_CROP);
                }
                break;
            case RC_EDIT_CLUB_LOGO_CROP:
                if (resultCode == RC_EDIT_CLUB_LOGO_CROP) {
                    final String path = data.getStringExtra(CutAvatarActivity.EXTRA_AVATAR_PATH);
                    this.path = path;

                    if (!TextUtils.isEmpty(path)) {
                        Picasso.with(this).load("file://" + path).fit().centerCrop().
                                error(R.drawable.ic_club_setting_default_logo).
                                placeholder(R.drawable.ic_club_setting_default_logo).into(this.logoIv);
                    } else {
                        this.logoIv.setImageResource(R.drawable.ic_club_setting_default_logo);
                    }
                }
                break;
            case RC_EDIT_CLUB_NAME: {
                if (RESULT_OK == resultCode) {
                    Bundle bundle = data.getExtras();
                    String name = bundle
                            .getString(BaseEditTextActivity.EXTRA_VALUE);
                    if (TextUtils.isEmpty(name)
                            || this.nameTv.getText().toString().equals(name))
                        return;

                    this.nameTv.setText(name);
                }
            }
            break;
            case RC_EDIT_CLUB_DESC: {
                if (RESULT_OK == resultCode) {
                    Bundle bundle = data.getExtras();
                    String name = bundle.getString(BaseEditTextActivity.EXTRA_VALUE);
                    if (TextUtils.isEmpty(name)
                            || this.descTv.getText().toString().equals(name))
                        return;

                    this.descTv.setText(name);
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.club_info_menu_setting_savr_item:
                this.updateClubInfo(item);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    String clubId = "";
    private void updateClubInfo(final MenuItem item) {
        final String name = this.nameTv.getText().toString();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        final String desc = this.descTv.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            return;
        }
        item.setEnabled(false);
        final Context ctx = this;

        final File file = new File(path);

        try {
            clubId = AlgorithmUtils.md5(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(clubId) && file.exists()) {

            QiNiuManager qiNiuManager = new QiNiuManager(this);
            qiNiuManager.setQiNiuUploadCallBack(new QiNiuUploadCallBack() {
                @Override
                public void onComplete(String key) {
                    Picasso.with(ClubInfoSettingActivity.this).invalidate(key);
                    if (AVUser.getCurrentUser() != null) {
                        SharedPreferences userSp = getSharedPreferences(AVUser.getCurrentUser()
                                .getObjectId(), 0);
                        SharedPreferences.Editor editor = userSp.edit();
                        editor.putString(CLUB_LOGO_LOCALE, path);
                        editor.putString(CLUB_LOGO, key);
                        editor.putLong(CLUB_LOGO_CHANGE, System.currentTimeMillis());
                        editor.apply();
                    }
                    uploadInfo(clubId, name, desc, key);
                    item.setEnabled(true);
                }

                @Override
                public void onError() {
                    uploadInfo("", name, desc, "");
                    item.setEnabled(true);
                }
            });
            String qiNiuTokenKey = qiNiuManager.getClubLogo() + clubId;
            qiNiuManager.uploadFile(qiNiuTokenKey, file, qiNiuTokenKey);
            Toasts.show(ctx, R.string.club_info_setting_save_msg);
            return;
        }
        uploadInfo("", name, desc, "");
        item.setEnabled(true);

    }

    private void uploadInfo(final String logo, final String name, final String desc, final String localLogoImage) {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return manager.postUpdateClubInfo(logo, name, null,
                            null, desc, null, isPrivateClub, localLogoImage);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result.booleanValue()) {
                    Toasts.show(getApplicationContext(), R.string.club_info_setting_save_success);
                    finish();
                } else {
                    Toasts.show(getApplicationContext(), R.string.club_info_setting_save_error);
                }
                super.onPostExecute(result);
            }

        });
    }

    private void fetchMyClub() {
        final String userId = this.getUserId();
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, ClubInfoCompact>() {

            @Override
            protected ClubInfoCompact doInBackground(Void... params) {
                if (null != manager) {
                    try {
                        return manager.getMyClub(userId);
                    } catch (BusinessException e) {
                        return null;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(ClubInfoCompact result) {
                if (null != result) {
                    clubInfo = result;
                    final String name = result.getName();
                    if (!TextUtils.isEmpty(name)) {
                        nameTv.setText(name);
                    }
                    final String desc = result.getDesc();
                    if (!TextUtils.isEmpty(desc)) {
                        descTv.setText(desc);
                    }
                    final String logo = result.getLogo();
                    if (!TextUtils.isEmpty(logo)) {
                        Picasso.with(ClubInfoSettingActivity.this).load(logo).fit().
                                error(R.drawable.ic_club_setting_default_logo).
                                placeholder(R.drawable.ic_club_setting_default_logo).
                                centerCrop().into(logoIv);
                    } else {
                        logoIv.setImageResource(R.drawable.ic_club_setting_default_logo);
                    }

                    if (!clubInfo.getIsPrivate()) {
                        privateClubSv.setChecked(false);
                        isPrivateClub = 0;
                    } else {
                        privateClubSv.setChecked(true);
                        isPrivateClub = 1;
                    }
                }
            }

        });
    }

    private void showPickDialog() {
        new AlertDialog.Builder(this)
                .setTitle(
                        getString(R.string.club_info_setting_logo_title))
                .setNegativeButton(
                        getString(R.string.user_setting_activity_setting_avatar_gallery),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        null);
                                intent.setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                                startActivityForResult(intent,
                                        RC_EDIT_CLUB_LOGO_GALLERY);
                            }
                        })
                .setPositiveButton(
                        getString(R.string.user_setting_activity_setting_avatar_camera),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.dismiss();
                                Intent intent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(tempFile));
                                startActivityForResult(intent,
                                        RC_EDIT_CLUB_LOGO_CAMERA);
                            }
                        }).show();
    }

    private String getPhotoFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'BEAST'_yyyyMMdd_HHmmss", Locale.getDefault());
        return dateFormat.format(new Date()) + ".jpg";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_club_info_setting_logo:
                this.showPickDialog();
                break;
            case R.id.activity_club_info_setting_name: {
                final String name = this.nameTv.getText().toString();
                final Intent editIntent = new Intent(this,
                        BaseEditTextActivity.class);
                editIntent.putExtra(BaseEditTextActivity.EXTRA_VALUE, name);
                editIntent.putExtra(BaseEditTextActivity.EXTRA_MAX_LENGTH, 12);
                startActivityForResult(editIntent, RC_EDIT_CLUB_NAME);
            }
            break;
            case R.id.activity_club_info_setting_desc: {
                final String desc = this.descTv.getText().toString();
                final Intent editIntent = new Intent(this,
                        BaseEditTextActivity.class);
                editIntent.putExtra(EditTextActivity.EXTRA_VALUE, desc);
                editIntent.putExtra(BaseEditTextActivity.EXTRA_MAX_LENGTH, 20);
                startActivityForResult(editIntent, RC_EDIT_CLUB_DESC);
            }
            break;
            default:
                break;
        }
    }

}
