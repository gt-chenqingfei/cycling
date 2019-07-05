package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubActivityManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityInfo;
import com.beastbikes.android.modules.cycling.club.ui.widget.PickerDialogManange;
import com.beastbikes.android.modules.cycling.club.ui.widget.richeditor.PushImageManage;
import com.beastbikes.android.modules.cycling.club.ui.widget.richeditor.Utils;
import com.beastbikes.android.modules.social.im.ui.conversation.LocationSelectActivity;
import com.beastbikes.android.modules.user.ui.binding.CountryPageActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.multiimageselector.MultiImageSelectorActivity;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Alias("发布活动")
@MenuResource(R.menu.activity_club_release_menu)
@LayoutResource(R.layout.activity_club_release_activities)
public class ClubActivityReleaseActivity extends SessionFragmentActivity implements View.OnClickListener,
        View.OnTouchListener, Constants {
    private static final int REQ_SELECT_IMAGE = 0x123;
    private static final int REQ_HTML = 0x124;
    private static final int REQ_SELECT_PLACE = 0x125;
    private static final int REQ_SELECT_ROUTE = 0x126;

    public static final String CLUB_ACTIVITY_MANAGE_TAG = "club_activity_manage_tag";
    public static final int REPOST_CLUB_ACTIVITY = 1;
    public static final int EDIT_CLUB_ACTIVITY = 2;
    public static final String EXTRA_ACT_INFO = "ACT_INFO";
    public static final int REQ_CUT_IMAGE = 0x127;

    private static final String PREF_RELEASE_INFO = "release_info";
    private static final String PREF_INFO = "release_info";

    @IdResource(R.id.club_act_map_img)
    private View actImageViewGroup;

    @IdResource(R.id.club_act_map_img_src)
    private ImageView actImage;

    @IdResource(R.id.club_act_map_img_src_layer)
    private View layer;

    @IdResource(R.id.club_act_scrollview)
    private ScrollView scrollView;

    @IdResource(R.id.club_act_info)
    private View actinfo;

    @IdResource(R.id.club_act_starttime)
    private View starttime;

    @IdResource(R.id.club_act_endtime)
    private View endtime;

    @IdResource(R.id.club_act_info_place)
    private View place;

    @IdResource(R.id.club_act_info_tv)
    private TextView infoTv;

    @IdResource(R.id.club_act_map_icon)
    private View addImageIcon;

    @IdResource(R.id.club_act_add_route)
    private View addRoute;

    @IdResource(R.id.club_act_deadline)
    private View deadlineView;

    @IdResource(R.id.club_act_add_isclub)
    private View isclub;

    @IdResource(R.id.club_act_info_place_tv)
    private TextView placeTv;

    @IdResource(R.id.club_act_info_addroute_tv)
    private TextView routeTv;

    @IdResource(R.id.club_release_act_more_setting_group_layout)
    private View settingGroupLayout;

    @IdResource(R.id.club_release_act_more_setting_group)
    private View settingGrop;

    @IdResource(R.id.club_release_act_more_setting_icon)
    private View moreSettingIcon;

    @IdResource(R.id.club_act_starttime_tv)
    private TextView startTimeTv;

    @IdResource(R.id.club_act_endtime_tv)
    private TextView endTimeTv;

    @IdResource(R.id.club_act_deadline_tv)
    private TextView deadLineTv;

    @IdResource(R.id.club_act_commit)
    private TextView commit;

    @IdResource(R.id.club_act_theme)
    private EditText theme;

    @IdResource(R.id.club_act_iphone)
    private EditText phone;

    @IdResource(R.id.club_act_user_number)
    private EditText userNumber;

    @IdResource(R.id.laboratory_activity_grid_switch)
    private Switch switchView;

    @IdResource(R.id.club_act_info_isclub_tv)
    private TextView isClubTv;

    @IdResource(R.id.club_create_zone_tv)
    private TextView tvZone;

    private SharedPreferences preferences;

    private String imagePath;
    private String html;
    private Animation downAnim;
    private Animation upAnim;

    private Animation commitDownAnim;
    private Animation commitUpAnim;

    private PickerDialogManange dialogManange;

    private String defauleImage;
    private String[] defauleImagePath = {"http://bazaar.speedx.com/clubActivity/clubActivityCover1@3x.png",
            "http://bazaar.speedx.com/clubActivity/clubActivityCover2@2x.png",
            "http://bazaar.speedx.com/clubActivity/clubActivityCover3@3x.png"};
    private int[] defauleId = {R.drawable.activity_club_release_defual01, R.drawable.activity_club_release_defual02
            , R.drawable.activity_club_release_defual03};
    private ClubActivityInfo clubActivityInfo;

    private double latitude;
    private double longitude;
    private String routeId = "";
    private String routeName = "";

    private ClubActivityManager clubActManager;
    private String applyEndDate;
    private LoadingDialog loadingDialog;
    private int maxMembers;
    private boolean isRepost;
    private QiNiuManager qiNiuManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        preferences = getSharedPreferences(PREF_RELEASE_INFO, Context.MODE_PRIVATE);
        clubActManager = new ClubActivityManager(this);
        qiNiuManager = new QiNiuManager(this);
        dialogManange = new PickerDialogManange(this, startTimeTv, endTimeTv, deadLineTv);
        initMoreSettingView();
        actImageViewGroup.setOnClickListener(this);
        actinfo.setOnClickListener(this);
        starttime.setOnClickListener(this);
        endtime.setOnClickListener(this);
        place.setOnClickListener(this);
        deadlineView.setOnClickListener(this);
        addRoute.setOnClickListener(this);
        settingGrop.setOnClickListener(this);
        commit.setOnClickListener(this);
        tvZone.setOnClickListener(this);
        this.tvZone.setText("+" + LocaleManager.getCountryCode(this));
        fullScrollUp();
        Intent intent = getIntent();
        if (intent.hasExtra(CLUB_ACTIVITY_MANAGE_TAG)) {
            this.clubActivityInfo =
                    (ClubActivityInfo) intent.getSerializableExtra(EXTRA_ACT_INFO);
            init(clubActivityInfo);
            if (intent.getIntExtra(CLUB_ACTIVITY_MANAGE_TAG, -1) == EDIT_CLUB_ACTIVITY) {
                switchView.setEnabled(false);
            } else if (intent.getIntExtra(CLUB_ACTIVITY_MANAGE_TAG, -1) == REPOST_CLUB_ACTIVITY) {
                initRepostActivity();
            }
        } else {
            String json = preferences.getString(PREF_INFO, "");
            try {
                JSONObject job = new JSONObject(json);
                init(new ClubActivityInfo(job));
            } catch (JSONException e) {
                init(new ClubActivityInfo());
            }
        }
        scrollView.setOnTouchListener(this);
    }

    private void init(ClubActivityInfo info) {
        dialogManange.init(info);
        routeName = info.getRouteName();
        routeId = info.getRouteId();
        setRouteTv();
        if (info.getMaxMembers() != 0) {
            userNumber.setText(info.getMaxMembers() + "");
        }
        String phoneNum = info.getMobilephone();
        if (!TextUtils.isEmpty(phoneNum)) {
            try {
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phoneNum, "CN");
                int countryCode = phoneNumber.getCountryCode();
                String phone = String.valueOf(phoneNumber.getNationalNumber());
                this.phone.setText(phone);
                this.tvZone.setText("+" + countryCode);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        } else {
            phone.setText("");
        }

        if (!TextUtils.isEmpty(info.getTitle())) {
            theme.setText(info.getTitle());
        } else {
            theme.setText("");
        }

        setHtml(info.getDecstiption());

        setPlaceTv(info.getMobPlace());
        this.latitude = info.getMobPoint()[0];
        this.longitude = info.getMobPoint()[1];
        if (getIntent().getIntExtra(CLUB_ACTIVITY_MANAGE_TAG, -1) == EDIT_CLUB_ACTIVITY) {
            switchView.setChecked(!info.isClubPrivate());
        }
        if (TextUtils.isEmpty(info.getCover()) || info.getCover().equals("null")) {
            actImage.setImageResource(defauleId[getDefualImage()]);
            imagePath = "";
        } else {
            setActIamgeView(info.getCover());
            if (new File(info.getCover()).exists()) {
                imagePath = info.getCover();
            } else {
                defauleImage = info.getCover();
            }
        }
    }

    private void initMoreSettingView() {
        this.downAnim = AnimationUtils.loadAnimation(this, R.anim.club_release_down);
        this.downAnim.setFillAfter(true);
        this.commitDownAnim = AnimationUtils.loadAnimation(this, R.anim.club_release_commit_down);
        this.commitDownAnim.setFillAfter(true);

        this.upAnim = AnimationUtils.loadAnimation(this, R.anim.club_release_up);
        this.commitUpAnim = AnimationUtils.loadAnimation(this, R.anim.club_release_commit_up);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activity_club_release_menu) {
            init(new ClubActivityInfo());
        } else if (item.getItemId() == android.R.id.home) {
            saveInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActIamgeView(String path) {
        if (new File(path).exists()) {
            path = "file://" + path;
        }
        Picasso.with(this).load(path).fit().placeholder(defauleId[0])
                .error(defauleId[0]).centerCrop().into(actImage);
        addImageIcon.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_act_map_img:
                Intent it = new Intent(this, MultiImageSelectorActivity.class);
                it.putExtra(MultiImageSelectorActivity.EXTRA_GALLERY_FULL, true);
                it.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                it.putExtra(MultiImageSelectorActivity.EXTRA_IS_SHOWMAX, false);
                it.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                        new ArrayList<String>());
                startActivityForResult(it, REQ_SELECT_IMAGE);
                break;

            case R.id.club_act_info:
                Intent htmlIntent = new Intent(this, ClubActInfoEditorActivity.class);
                if (!TextUtils.isEmpty(html)) {
                    htmlIntent.putExtra(ClubActInfoEditorActivity.EXTRA_CONTENT, this.html);
                }
                startActivityForResult(htmlIntent, REQ_HTML);
                break;

            case R.id.club_act_starttime:
                dialogManange.showStartDatePicker();
                break;

            case R.id.club_act_endtime:
                dialogManange.showEndDatePicker();
                break;

            case R.id.club_act_info_place:
                Intent intent = new Intent(ClubActivityReleaseActivity.this, LocationSelectActivity.class);

                startActivityForResult(intent, REQ_SELECT_PLACE);
                break;

            case R.id.club_act_add_route:
                Intent routeIntent = new Intent(ClubActivityReleaseActivity.this,
                        ClubActRouteSelfActivity.class);
                startActivityForResult(routeIntent, REQ_SELECT_ROUTE);
                break;

            case R.id.club_act_deadline:
                dialogManange.showDeadLinePicker();
                break;

            case R.id.club_release_act_more_setting_group:
                if (settingGroupLayout.getVisibility() == View.GONE) {
                    moreSettingIcon.startAnimation(downAnim);
                    settingGroupLayout.setVisibility(View.VISIBLE);
                    fullScrollDown();
                } else {
                    moreSettingIcon.startAnimation(upAnim);
                    settingGroupLayout.setVisibility(View.GONE);
                }

                break;

            case R.id.club_act_commit:// 发布
                commit();
                break;

            case R.id.club_create_zone_tv:
                startActivityForResult(new Intent(this, CountryPageActivity.class), CountryPageActivity.REQ_COUNTRY);
                break;
        }
    }

    private void initRepostActivity() {
        isRepost = true;
        startTimeTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
        endTimeTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
    }

    private boolean checkRepostActivityTime() {
        if (isRepost) {
            if (clubActivityInfo == null)
                return false;
            long time1 = DateFormatUtil.timeFormat2Date(clubActivityInfo.getStartDate());
            long time2 = DateFormatUtil.timeFormat2Date(clubActivityInfo.getEndDate());
            String startTime1 = DateFormatUtil.formatYMDHm(time1);
            String endTime1 = DateFormatUtil.formatYMDHm(time2);
            String startTime2 = startTimeTv.getText().toString();
            String endTime2 = endTimeTv.getText().toString();
            if (!TextUtils.isEmpty(startTime1) && !TextUtils.isEmpty(endTime1) && !TextUtils.isEmpty(startTime2) && !TextUtils.isEmpty(endTime2)
                    && startTime1.equals(startTime2) && endTime1.equals(endTime2)) {
                return true;
            }
        }
        return false;
    }

    private void commit() {
        if (checkRepostActivityTime()) {
            Toasts.showOnUiThread(ClubActivityReleaseActivity.this, getResources().getString(R.string.change_time_repost_activity));
            startTimeTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
            endTimeTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
            return;
        }
        if (TextUtils.isEmpty(theme.getText())) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_theme_error_str));
            return;
        }
        if (TextUtils.isEmpty(html)) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_html_error_str));
            return;
        }
        if (startTimeTv.getCurrentTextColor() == getResources().getColor(R.color.designcolor_c7)) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_starttime_error_str));
            return;
        }
        if (endTimeTv.getCurrentTextColor() == getResources().getColor(R.color.designcolor_c7)) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_endtime_error_str));
            return;
        }

        if (placeTv.getText().toString().equals(getString(R.string.club_act_info_place_str)) ||
                placeTv.getCurrentTextColor() == getResources().getColor(R.color.designcolor_c7)) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_route_error_str));
            return;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_phone_error_str));
            return;
        }
        if (userNumber.getText().toString().equals("0")) {
            Toasts.showOnUiThread(this, getString(R.string.club_act_userNumber_error_str));
            return;
        }

        final String title = theme.getText().toString();
        final String mobPlace = placeTv.getText().toString();
        final String mobPoint = latitude + "," + longitude;
        final String startDate = getFormatTime(startTimeTv.getText().toString());
        final String endDate = getFormatTime(endTimeTv.getText().toString());
        final String mobilephone = tvZone.getText().toString() + phone.getText().toString();
        this.applyEndDate = "";
        if (!deadLineTv.getText().toString().equals(getString(R.string.club_act_time_str))) {
            applyEndDate = getFormatTime(deadLineTv.getText().toString());
        }
        if (TextUtils.isEmpty(userNumber.getText())) {
            this.maxMembers = 50;
        } else {
            this.maxMembers = Integer.valueOf(userNumber.getText().toString());
        }
        final int isClubPrivate = switchView.isChecked() ? 0 : 1;
        if (routeName == null) routeName = "";
        if (routeId == null) routeId = "";

        commit.setClickable(false);
        this.loadingDialog = new LoadingDialog(this, "", true);
        loadingDialog.show();
        if (TextUtils.isEmpty(imagePath)) {
            pushHtmlIamgeAndCommit(title, mobPlace, mobPoint, startDate, endDate, routeId, routeName, mobilephone, applyEndDate,
                    maxMembers, isClubPrivate, defauleImage);
        } else {
            uploadImageAndCommit(title, mobPlace, mobPoint, startDate, endDate, routeId, routeName, mobilephone, applyEndDate,
                    maxMembers, isClubPrivate);
        }

    }

    private void pushHtmlIamgeAndCommit(final String title, final String mobPlace, final String mobPoint,
                                        final String startDate, final String endDate, final String routeId, final String routeName,
                                        final String mobilephone, final String applyEndDate,
                                        final int maxMembers, final int isClubPrivate, final String actIcon) {
        PushImageManage pushIamgeManage = new PushImageManage(html, getAsyncTaskQueue());
        pushIamgeManage.pushImage(new PushImageManage.OnReplaceImageURl() {
            @Override
            public void onSuccess(String html) {
                SpeedxAnalytics.onEvent(ClubActivityReleaseActivity.this, "", "click_club_release_activity");
                commitByNet(title, "", mobPlace, mobPoint, startDate, endDate, routeId, routeName, mobilephone, applyEndDate,
                        maxMembers, isClubPrivate, html, actIcon);
            }

            @Override
            public void onfail() {
                if (loadingDialog != null)
                    loadingDialog.cancel();
                commit.setClickable(true);
                Toasts.showOnUiThread(ClubActivityReleaseActivity.this,
                        getString(R.string.network_not_awesome));
            }
        });
    }

    private int getDefualImage() {
        int y = (int) (Math.random() * 3);
        defauleImage = defauleImagePath[y];
        return y;
    }

    private void uploadImageAndCommit(final String title, final String mobPlace, final String mobPoint,
                                      final String startDate, final String endDate, final String routeId, final String routeName,
                                      final String mobilephone, final String applyEndDate,
                                      final int maxMembers, final int isClubPrivate) {
        String aid = UUID.randomUUID().toString();
        final String md5Aid = AlgorithmUtils.md5(aid);
        String qiNiuTokenKey = qiNiuManager.getClubActivityTokenKey() + md5Aid;
        qiNiuManager.setQiNiuUploadCallBack(new QiNiuUploadCallBack() {
            @Override
            public void onComplete(String key) {
                pushHtmlIamgeAndCommit(title, mobPlace, mobPoint, startDate, endDate, routeId, routeName, mobilephone, applyEndDate,
                        maxMembers, isClubPrivate, key);
            }

            @Override
            public void onError() {
                commit.setClickable(true);
                if (loadingDialog != null)
                    loadingDialog.cancel();
            }
        });
        qiNiuManager.uploadFile(qiNiuTokenKey, imagePath, qiNiuTokenKey);


    }

    private void commitByNet(final String title, final String desc, final String mobPlace, final String mobPoint,
                             final String startDate, final String endDate, final String routeId, final String routeName,
                             final String mobilephone, final String applyEndDate,
                             final int maxMembers, final int isClubPrivate, final String decstiption, final String cover) {
        getAsyncTaskQueue().add(new AsyncTask<Object, Object, ClubActivityInfo>() {
            @Override
            protected ClubActivityInfo doInBackground(Object... params) {
                if (getIntent().hasExtra(CLUB_ACTIVITY_MANAGE_TAG) &&
                        getIntent().getIntExtra(CLUB_ACTIVITY_MANAGE_TAG, -1) == EDIT_CLUB_ACTIVITY) {
                    return clubActManager.updateClubActivity(clubActivityInfo.getActId(), title, desc, mobPlace, mobPoint,
                            startDate, endDate, routeId, routeName, mobilephone, applyEndDate, maxMembers, isClubPrivate, decstiption, cover);
                } else {
                    return clubActManager.createClubActivity(title, desc, mobPlace, mobPoint,
                            startDate, endDate, routeId, routeName, mobilephone, applyEndDate, maxMembers, isClubPrivate, decstiption, cover);
                }
            }

            @Override
            protected void onPostExecute(ClubActivityInfo clubActivityInfo) {
                super.onPostExecute(clubActivityInfo);
                commit.setClickable(true);
                if (loadingDialog != null)
                    loadingDialog.cancel();
                if (clubActivityInfo != null) {
                    if (!getIntent().hasExtra(CLUB_ACTIVITY_MANAGE_TAG)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                    }

                    if (clubActivityInfo == null)
                        return;
                    Uri uri = Uri.parse(ClubActivityInfoBrowserActivity.
                            getActivityUrl(clubActivityInfo.getActId(), ClubActivityReleaseActivity.this));


                    final Intent browserIntent = new Intent(ClubActivityReleaseActivity.this,
                            ClubActivityInfoBrowserActivity.class);
                    browserIntent.setData(uri);
                    browserIntent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_ACTIVITY_TYPE, 1);
                    browserIntent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_CLUB_ACTIVITY_ID,
                            clubActivityInfo.getActId());
                    browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                    browserIntent.setPackage(getPackageName());
                    startActivity(browserIntent);
                    setResult(RESULT_OK, getIntent());
                    finish();
                } else {
                    Toasts.showOnUiThread(ClubActivityReleaseActivity.this,
                            getString(R.string.network_not_awesome));
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQ_SELECT_IMAGE:
                        ArrayList<String> list = data.getStringArrayListExtra(
                                MultiImageSelectorActivity.EXTRA_RESULT);
                        if (!list.isEmpty()) {
                            String path = list.get(0);
                            Intent i = new Intent(this, ClubCutBitmapActivity.class);
                            i.putExtra(ClubCutBitmapActivity.EXTRA_AVATAR_PATH, path);
                            startActivityForResult(i, REQ_CUT_IMAGE);
                        }
                        break;
                    case REQ_HTML:
                        String html = data.getStringExtra(ClubActInfoEditorActivity.EXTRA_HTML);
                        setHtml(html);
                        break;
                    case REQ_SELECT_PLACE:// 选点
                        this.latitude = data.getDoubleExtra(LocationSelectActivity.EXTRA_LAT, 0);
                        this.longitude = data.getDoubleExtra(LocationSelectActivity.EXTRA_LNG, 0);
                        String address = data.getStringExtra(LocationSelectActivity.EXTRA_ADDR);
                        setPlaceTv(address);
                        break;
                    case REQ_SELECT_ROUTE:
                        this.routeId = data.getStringExtra(ClubActRouteSelfActivity.EXTRA_ROUTE_ID);
                        this.routeName = data.getStringExtra(ClubActRouteSelfActivity.EXTRA_ROUTE_NAME);
                        setRouteTv();
                        break;
                    case CountryPageActivity.REQ_COUNTRY:
                        if (data != null) {
                            String country = data.getStringExtra(CountryPageActivity.EXTTA_COUNTRY_CODE);
                            if (!TextUtils.isEmpty(country)) {
                                tvZone.setText("+" + country);
                            }
                        }
                        break;
                }
                break;
            case 4:
                if (requestCode == REQ_CUT_IMAGE) {
                    final String path = data
                            .getStringExtra(ClubCutBitmapActivity.EXTRA_AVATAR_PATH);
                    if (!TextUtils.isEmpty(path)) {
                        imagePath = path;
                        setActIamgeView(imagePath);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setHtml(String html) {
        if (!TextUtils.isEmpty(html) && !html.equals("null")) {
            infoTv.setTextColor(Color.BLACK);
            infoTv.setText(Utils.htmlRemoveTag(html));
        } else {
            infoTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
            infoTv.setText(getString(R.string.club_act_info));
        }
        this.html = html;
    }

    private void setPlaceTv(String address) {
        if (!TextUtils.isEmpty(address) || placeTv.getText().toString().equals("null")) {
            placeTv.setTextColor(Color.BLACK);
            placeTv.setText(address);
        } else {
            placeTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
            placeTv.setText(getString(R.string.club_act_info_place_str));
        }
    }


    private void setRouteTv() {
        if (!TextUtils.isEmpty(routeName) || routeTv.getText().toString().equals("null")) {
            routeTv.setTextColor(Color.BLACK);
            routeTv.setText(routeName);
        } else {
            routeTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
            routeTv.setText("");
        }
    }

    private void fullScrollDown() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void fullScrollUp() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    // 将字符串转为时间戳
    public long getTime(String user_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d;
        try {
            d = sdf.parse(user_time);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getFormatTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        long time = 0;
        try {
            time = sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormatUtil.dateFormat2String(time);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == event.ACTION_MOVE &&
                commit.getVisibility() == View.VISIBLE) {
            //当手指离开的时候
            commit.startAnimation(commitDownAnim);
            commit.setVisibility(View.INVISIBLE);
        } else if (event.ACTION_MOVE != event.getAction() &&
                commit.getVisibility() == View.INVISIBLE) {
            commit.startAnimation(commitUpAnim);
            commit.setVisibility(View.VISIBLE);

        }
        return super.onTouchEvent(event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveInfo();
        }
        return false;
    }


    private void saveInfo() {
        if (!getIntent().hasExtra(CLUB_ACTIVITY_MANAGE_TAG)) {
            String title = theme.getText().toString();
            String mobPlace = placeTv.getText().toString();
            String startDate = getFormatTime(startTimeTv.getText().toString());
            String endDate = getFormatTime(endTimeTv.getText().toString());
            String mobilephone = phone.getText().toString();
            this.applyEndDate = "";
            if (!deadLineTv.getText().toString().equals(getString(R.string.club_act_time_str))) {
                applyEndDate = getFormatTime(deadLineTv.getText().toString());
            }
            if (!TextUtils.isEmpty(userNumber.getText())) {
                this.maxMembers = Integer.valueOf(userNumber.getText().toString());
            }
            JSONObject object = new JSONObject();
            try {
                object.put("title", title);
                object.put("decstiption", html);
                object.put("cover", imagePath);
                object.put("endDate", endDate);
                object.put("applyEndDate", applyEndDate);
                object.put("startDate", startDate);
                object.put("maxMembers", maxMembers);
                object.put("isClubPrivate", !switchView.isChecked());
                object.put("mobPlace", mobPlace);
                JSONArray array = new JSONArray();
                array.put(0, Double.valueOf(latitude));
                array.put(1, Double.valueOf(longitude));
                object.put("mobPoint", array);
                object.put("routeId", routeId);
                object.put("routeName", routeName);
                object.put("mobilephone", mobilephone);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREF_INFO, object.toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        finish();
    }


}

