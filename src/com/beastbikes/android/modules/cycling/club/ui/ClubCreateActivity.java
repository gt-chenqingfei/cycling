package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.android.modules.social.im.ui.conversation.ConversationStaticActivity;
import com.beastbikes.android.modules.social.im.ui.conversation.LocationSelectActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.ui.binding.CountryPageActivity;
import com.beastbikes.android.utils.ImageSelectHelper;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.utils.Utils;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwitchView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Alias("创建俱乐部")
@LayoutResource(R.layout.club_create_profile_edit)
public class ClubCreateActivity extends SessionFragmentActivity implements
        OnClickListener {
    private static final Logger logger = LoggerFactory.getLogger(ClubCreateActivity.class);

    @IdResource(R.id.club_create_head_lay)
    private FrameLayout clubCreateHeadLay;

    @IdResource(R.id.club_create_profile_edit_lay)
    private ViewGroup clubProfileGroup;

    @IdResource(R.id.club_create_head_civ)
    private CircleImageView clubCreateHeadImgview;

    @IdResource(R.id.club_create_name_et)
    private EditText clubCreateNameEt;

    @IdResource(R.id.club_create_name_tv)
    private TextView clubCreateNameTv;

    @IdResource(R.id.club_create_city_lay)
    private FrameLayout clubCreateCityLay;

    @IdResource(R.id.club_create_city_tv)
    private TextView clubCreateCityTv;

    @IdResource(R.id.club_create_city_content_tv)
    private TextView clubCreateCityContentTv;

    @IdResource(R.id.club_create_intro_et)
    private EditText clubCreateIntroEt;

    @IdResource(R.id.club_create_intro_tv)
    private TextView clubCreateIntroTv;

    @IdResource(R.id.club_create_realname_et)
    private EditText clubCreateRealNameEt;

    @IdResource(R.id.club_create_realname_tv)
    private TextView clubCreateRealNameTv;

    @IdResource(R.id.club_create_phone_et)
    private EditText clubCreatePhoneEt;

    @IdResource(R.id.club_create_phone_tv)
    private TextView clubCreatePhoneTv;

    @IdResource(R.id.club_create_verificationcode_tv)
    private TextView verificationcodeTV;

    @IdResource(R.id.club_create_vcode_et)
    private EditText clubvCodeET;

    @IdResource(R.id.club_create_verificationcode_btn)
    private Button clubCreateVerificationCodeBTN;//

    @IdResource(R.id.club_create_qq_et)
    private EditText clubCreateQQEt;

    @IdResource(R.id.club_create_create_btn)
    private Button clubCreateCreateBtn;

    @IdResource(R.id.openprivateclubrl)
    private RelativeLayout openprivateclubrl;

    @IdResource(R.id.privateclubhelptv)
    private TextView privateclubhelptv;

    @IdResource(R.id.create_club_private_sv)
    private SwitchView privateClubSv;

    @IdResource(R.id.club_create_zone_tv)
    private TextView tvZone;

    private static final int COLOR_RED = 0xffff0000;
    private static final int COLOR_BLACK = 0xcc222222;

    private String clubHeadUrl;
    private String clubCity, clubProvince;
    private static int DIALOG_CREATE_CANCEL = 100;
    private static int DIALOG_CREATE_BACK_WARMING = 101;
    LoadingDialog loadingDialog;
    private ClubManager clubManager;
    private UserManager usrManager;

    private ImageSelectHelper selectImageView = null;

    private int status = 0;
    private String clubId;

    private PopupWindow popupWindow;
    private int isPrivateClub = 0;

    private String city;
    private String province;

    private CountDownTimer timer;
    private final static String COUNTTIMEKEY = "COUNTTIME";
    private int countTimeValue = 0;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
        if (countTimeValue == 0) {
            if (savedInstanceState != null) {
                countTimeValue = savedInstanceState.getInt(COUNTTIMEKEY, 0);
                startCount(countTimeValue);
            }
        }
        SpeedxAnalytics.onEvent(this, "进入创建俱乐部页面",null);

        clubManager = new ClubManager(this);
        usrManager = new UserManager(this);
        clubCreateNameEt.requestFocus();
        clubCreateHeadLay.setOnClickListener(this);
        clubCreateCityLay.setOnClickListener(this);
        clubCreateCreateBtn.setOnClickListener(this);
        openprivateclubrl.setOnClickListener(this);
        clubCreateVerificationCodeBTN.setOnClickListener(this);
        privateClubSv.setChecked(false);
        tvZone.setOnClickListener(this);
        this.tvZone.setText("+" + LocaleManager.getCountryCode(this));
        privateClubSv.setOnClickSwitchListener(new SwitchView.OnClickSwitchListener() {
            @Override
            public void onSwitchChanged(boolean open) {
                if (open) {
                    isPrivateClub = 1;
                } else {
                    isPrivateClub = 0;
                }
            }
        });
        refreshView();
        getMyClub();
        getRemoteUserLocaltion();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (status == ClubInfoCompact.CLUB_STATUS_NONE) {
                if (TextUtils.isEmpty(clubHeadUrl) && TextUtils.isEmpty(clubCreateNameEt.getText().toString())
                        && TextUtils.isEmpty(clubCreateIntroEt.getText().toString()) && TextUtils.isEmpty(clubCreateRealNameEt.getText().toString())
                        && TextUtils.isEmpty(clubCreatePhoneEt.getText().toString()) && TextUtils.isEmpty(clubvCodeET.getText().toString())
                        && TextUtils.isEmpty(clubCreateQQEt.getText().toString())) {
                    finish();
                    return true;
                }
                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setMessage(R.string.club_create_back_warming);
                dialog.setPositiveButton(R.string.activity_club_manager_dialog_ok, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CountryPageActivity.REQ_COUNTRY) {
            if (data != null) {
                String country = data.getStringExtra(CountryPageActivity.EXTTA_COUNTRY_CODE);
                if (!TextUtils.isEmpty(country)) {
                    tvZone.setText("+" + country);
                }
            }
        } else if (requestCode == ConversationStaticActivity.REQUEST_CODE_LOCALTION_SELECT) {
            if (resultCode == RESULT_OK) {
                if (null != data) {
                    String addr = data.getStringExtra(LocationSelectActivity.EXTRA_ADDR);
                    String province = data.getStringExtra(LocationSelectActivity.EXTRA_PROVINCE);
                    String area = data.getStringExtra(LocationSelectActivity.EXTRA_AREA);
                    city = data.getStringExtra(LocationSelectActivity.EXTRA_CITYNAME);
                    if (TextUtils.isEmpty(city)) {
                        city = area;
                    }
                    if (!TextUtils.isEmpty(province) && province.equals(city)) {
                        city = area;
                    }
                    clubCreateCityContentTv.setText(province + " " + city);

                    if (!TextUtils.isEmpty(province)) {
                        this.province = province;
                    }
                    latitude = data.getDoubleExtra(LocationSelectActivity.EXTRA_LAT, 0.0);
                    longitude = data.getDoubleExtra(LocationSelectActivity.EXTRA_LNG, 0.0);
                }
            }
        }
        if (null != selectImageView)
            selectImageView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.club_create_create_btn:
                postRegisterClub();
//                if (status == ClubInfoCompact.CLUB_STATUS_NONE) {
//                    postRegisterClub();
//                } else {
//                    logger.error("Club create error ! status =" + status);
//                }
                break;
            case R.id.club_create_head_lay: {
                selectImageView = new ImageSelectHelper(this,
                        new ImageSelectHelper.SelectImageListener() {

                            @Override
                            public void onfinishSelectImage(String path) {
                                clubHeadUrl = path;
                                if (!TextUtils.isEmpty(path)) {
                                    Picasso.with(ClubCreateActivity.this).load("file://" + path).fit()
                                            .centerCrop().error(R.drawable.ic_avatar_club).
                                            placeholder(R.drawable.ic_avatar_club).into(clubCreateHeadImgview);
                                } else {
                                    clubCreateHeadImgview.setImageResource(R.drawable.ic_avatar_club);
                                }
                            }
                        }, true);
                selectImageView.show();
            }
            break;
            case R.id.club_create_city_lay:
                Intent intent = new Intent(this, LocationSelectActivity.class);
                startActivityForResult(intent, ConversationStaticActivity.REQUEST_CODE_LOCALTION_SELECT);
                break;
            case R.id.openprivateclubrl:
                if (privateclubhelptv.getVisibility() == View.VISIBLE) {
                    privateclubhelptv.setVisibility(View.INVISIBLE);
                } else {
                    privateclubhelptv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.club_create_verificationcode_btn:
                if (TextUtils.isEmpty(clubCreatePhoneEt.getText())) {
                    clubCreatePhoneTv.setTextColor(COLOR_RED);
                    return;
                }
                clubCreateVerificationCodeBTN.setClickable(false);
                clubCreateVerificationCodeBTN.setBackgroundResource(R.drawable.gray);
                sendSmsCode();
                break;
            case R.id.club_create_zone_tv:
                startActivityForResult(new Intent(this, CountryPageActivity.class), CountryPageActivity.REQ_COUNTRY);
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(COUNTTIMEKEY, countTimeValue);
        super.onSaveInstanceState(outState);
    }

    private void postRegisterClub() {
        if (!formValidate())
            return;

        SpeedxAnalytics.onEvent(this, "提交资料创建俱乐部",null);
        loadingDialog = new LoadingDialog(this,
                getString(R.string.club_create_create_waiting), false);
        loadingDialog.show();
        final String etName = clubCreateNameEt.getText().toString();
        final String etIntro = clubCreateIntroEt.getText().toString();
        final String etPhone = tvZone.getText().toString() + clubCreatePhoneEt.getText().toString();
        final String etRealName = clubCreateRealNameEt.getText().toString();
        final String etQQ = clubCreateQQEt.getText().toString();
        final String etCode = clubvCodeET.getText().toString();

        if (TextUtils.isEmpty(clubHeadUrl)) {
            postRegisterClubTask(null, etName, province, city, etIntro, etRealName, etPhone, etQQ, etCode, isPrivateClub, latitude, longitude);
            return;
        }

        String aid = UUID.randomUUID().toString();
        final String md5Aid = AlgorithmUtils.md5(aid);
        QiNiuManager qiNiuManager = new QiNiuManager(ClubCreateActivity.this);
        String qiNiuTokenKey = qiNiuManager.getClubLogo() + md5Aid;
        qiNiuManager.setQiNiuUploadCallBack(new QiNiuUploadCallBack() {
            @Override
            public void onComplete(String key) {
                postRegisterClubTask(md5Aid, etName, province, city, etIntro, etRealName, etPhone, etQQ, etCode, isPrivateClub, latitude, longitude);
            }

            @Override
            public void onError() {
                if (null != loadingDialog)
                    loadingDialog.dismiss();
            }
        });
        qiNiuManager.uploadFile(qiNiuTokenKey, clubHeadUrl, qiNiuTokenKey);
    }

    private void postRegisterClubTask(final String logoId,
                                      final String name, final String province, final String city,
                                      final String desc, final String realName, final String mobilephone,
                                      final String qq, final String vcode,
                                      final int isPrivate, final double latitude, final double longitude) {
        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubInfoCompact>() {
                    @Override
                    protected ClubInfoCompact doInBackground(String... params) {
                        try {
                            ClubInfoCompact infoCompact = clubManager.postRegisterClub(logoId,
                                    name,
                                    province,
                                    city,
                                    desc,
                                    realName,
                                    mobilephone,
                                    qq,
                                    vcode,
                                    isPrivate,
                                    latitude,
                                    longitude);
                            return infoCompact;
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null != loadingDialog)
                            loadingDialog.dismiss();
                        if (result == null)
                            return;

                        finish();
                    }
                });
    }

    private void sendSmsCode() {

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                return clubManager.sendSmscode(params[0], ClubManager.MSG_TYPE_REG_CLUB);
            }

            @Override
            protected void onPostExecute(Integer integer) {

                if (integer != -1) {
                    startCount(integer);
                } else {
                    clubCreateVerificationCodeBTN.setClickable(true);
                    clubCreateVerificationCodeBTN.setBackgroundResource(R.drawable.bg_verificationcodebtn);
                    clubCreateVerificationCodeBTN.setText(getResources().getString(R.string.get_verification_code));
                }

            }
        }, tvZone.getText().toString() + clubCreatePhoneEt.getText().toString());


    }

    private void getMyClub() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(String... params) {
                        AVUser user = AVUser.getCurrentUser();
                        if (null == user)
                            return null;

                        try {
                            return clubManager.getMyClub(user.getObjectId());
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null == result)
                            return;

                        status = result.getStatus();
                        clubId = result.getObjectId();

                        refreshView();
                    }

                });
    }

    private void getRemoteUserLocaltion() {
        final String userId = this.getUserId();
        getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                try {
                    return usrManager.getProfileByUserId(params[0]);
                } catch (Exception e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(ProfileDTO rui) {
                if (null == rui)
                    return;
                clubCity = rui.getCity();
                clubProvince = rui.getProvince();
                if (!TextUtils.isEmpty(clubCity) && !clubCity.equals("null"))
                    clubCreateCityContentTv.setText(clubCity);
            }

        }, userId);
    }

    private void refreshView() {
        try {
            if (!TextUtils.isEmpty(clubCity) && !clubCity.equals("null"))
                clubCreateCityContentTv.setText(clubCity);
            logger.trace("m", "clubCity=" + clubCity);
            switch (status) {
                case ClubInfoCompact.CLUB_STATUS_NONE:
                    clubProfileGroup.setVisibility(View.VISIBLE);
                    clubCreateCreateBtn.setBackgroundColor(0xffe34848);
                    clubCreateCreateBtn.setText(R.string.club_btn_create);
                    break;
            }
        } catch (Exception e) {
            Log.d("m", e.toString());
        }
    }

    private boolean formValidate() {
        if (TextUtils.isEmpty(clubCreateNameEt.getText())) {
            clubCreateNameTv.setTextColor(COLOR_RED);
            return false;
        } else {
            clubCreateNameTv.setTextColor(COLOR_BLACK);
        }


        if (TextUtils.isEmpty(city)) {
            clubCreateCityTv.setTextColor(COLOR_RED);
            return false;
        } else {
            clubCreateCityTv.setTextColor(COLOR_BLACK);
        }

        if (TextUtils.isEmpty(clubCreateIntroEt.getText())) {
            clubCreateIntroTv.setTextColor(COLOR_RED);
            return false;
        } else {
            clubCreateIntroTv.setTextColor(COLOR_BLACK);
        }

        if (TextUtils.isEmpty(clubCreateRealNameEt.getText())) {
            clubCreateRealNameTv.setTextColor(COLOR_RED);
            return false;
        } else {
            clubCreateRealNameTv.setTextColor(COLOR_BLACK);
        }

        if (TextUtils.isEmpty(clubCreatePhoneEt.getText())) {
            clubCreatePhoneTv.setTextColor(COLOR_RED);
            return false;
        } else {
            clubCreatePhoneTv.setTextColor(COLOR_BLACK);
        }

        if (TextUtils.isEmpty(clubvCodeET.getText())) {
            verificationcodeTV.setTextColor(COLOR_RED);
            return false;
        } else {
            verificationcodeTV.setTextColor(COLOR_BLACK);
        }

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (Utils.isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    private void startCount(int integer) {
        timer = new CountDownTimer(integer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTimeValue = (int) (millisUntilFinished / 1000);
                clubCreateVerificationCodeBTN.setText(millisUntilFinished / 1000 + "s");

            }

            @Override
            public void onFinish() {
                clubCreateVerificationCodeBTN.setClickable(true);
                clubCreateVerificationCodeBTN.setBackgroundResource(R.drawable.bg_verificationcodebtn);
                clubCreateVerificationCodeBTN.setText(getResources().getString(R.string.get_verification_code));
            }
        };
        timer.start();
    }

}
