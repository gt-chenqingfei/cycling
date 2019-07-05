package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.InputDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnBean;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnCallBack;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityService;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.simplify.SimplifyUtil;
import com.beastbikes.android.modules.map.MapBase;
import com.beastbikes.android.modules.preferences.ui.BaseEditTextActivity;
import com.beastbikes.android.modules.preferences.ui.EditTextActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.beastbikes.android.modules.user.ui.ActivityComplainActivity;
import com.beastbikes.android.modules.user.ui.WatermarkCameraActivity;
import com.beastbikes.android.modules.user.ui.WatermarkFinishedActivity;
import com.beastbikes.android.modules.user.util.ActivityDataUtil;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.sharepopupwindow.CommonSharePopupWindow;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareImageDTO;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CyclingCompletedActivity extends CyclingCompletedBase implements
        InputDialog.OnInputDialogClickListener {
    //Intent extra
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_CLOUD_ACTIVITY = "activity_dto";
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_EDIT_ACTIVITY_COVER = "edit_activity";// code
    public static final String EXTRA_AVATAR_URL = "avatar_url";
    public static final String EXTRA_NICK_NAME = "nick_name";
    public static final String EXTRA_SPORT_IDENTIFY = "sport_identify";

    private AVUser mUser = AVUser.getCurrentUser();
    private ActivityManager mActivityManager;
    private ActivityDTO mActivity;
    private ProfileDTO mProfileDTO;
    private String mActivityIdentifier;
    private List<SampleDTO> mSamples = new ArrayList<>();
    private List<Double> mAltitudes = new ArrayList<>();
    private List<Double> mAltitudeDistances = new ArrayList<>();
    private List<Double> mHeartRates = new ArrayList<>();
    private List<Double> mCadences = new ArrayList<>();
    // 分享图片路径
    private String mSceneryPath;
    private CommonSharePopupWindow mSharePopupWindow;
    private CommonShareImageDTO mShareImageDTO;
    // 保存本地骑行图片Path
    private List<String> mLocalSceneryUrls = new ArrayList<String>();
    private String mCityName;

    private InputDialog mReportDialog;

    private int mSampleSize = 50;

    SampleDTO maxSpeedSample = null;
    double totalMaxSpeed = 0;
    double totalMaxSpeedDis = 0;
    double totalMaxAltitude = 0;
    double totalMaxAltitudeDis = 0;
    double totalMaxHeartRate = 0;
    double totalMaxHeartRateDis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (null == intent || mUser == null) {
            finish();
            return;
        }
        this.mActivityManager = new ActivityManager(this);
        this.mActivityIdentifier = getIntent().getStringExtra(EXTRA_SPORT_IDENTIFY);

        if (intent.getSerializableExtra(EXTRA_CLOUD_ACTIVITY) != null) {
            this.mActivity = (ActivityDTO) intent.getSerializableExtra(EXTRA_CLOUD_ACTIVITY);
            dataFormat(this.mActivity);
            notifyAllDataSetChanged(mActivity, mSamples, mAltitudes,
                    TextUtils.equals(AVUser.getCurrentUser().getObjectId(), getUserId()));
        }
        this.getUserInfo(getUserId());


        setUpMap();
    }

    @Override
    public List<SampleDTO> getSamples() {
//        if (mSamples.size() <= 0) {
////            getSamples(mActivityIdentifier);
//        }
        return mSamples;
    }


    @Override
    public void onSideBarItemClick(int id) {
        super.onSideBarItemClick(id);
        switch (id) {
            case R.id.record_side_btn_visible:
                SpeedxAnalytics.onEvent(this, "设置地图私密", "setting_cycling_record_private");

                if (!isPrivate) {

                    boolean isFirst = mDefaultSp.getBoolean(Constants.PREF_SET_MAP_PRIVATE_FIRST, true);
                    if (isFirst) {
                        showMapPrivateDialog();
                    } else {
                        isPrivate = true;
                        mActivity.setIsPrivate(isPrivate ? 1 : 0);
                        this.updateCyclingRecord(STATUS_PRIVATE);
                        speedxMap.switchMapHiddenState(isPrivate);
                    }
                } else {
                    isPrivate = false;
                    mActivity.setIsPrivate(isPrivate ? 1 : 0);
                    this.updateCyclingRecord(STATUS_PUBLIC);
                    speedxMap.switchMapHiddenState(isPrivate);
                }
                break;
        }
    }

    @Override
    public void onActionBarItemClick(int id) {
        super.onActionBarItemClick(id);
        switch (id) {
            case R.id.action_bar_tools_share:
                postShare();
                break;
            case R.id.action_bar_tools_camera:
                startWatermarkCamera(false);
                SpeedxAnalytics.onEvent(this, "分享水印相机",
                        "click_ridding_history_share_digital_watermarking");
                break;
            case R.id.action_bar_tools_upload:
                SpeedxAnalytics.onEvent(this, "", "save_ridding_goal");
                this.uploadLocalActivity();
                break;
            case R.id.action_bar_tools_report:
                if (null != this.mActivity && !this.mActivity.isHasReport()) {
                    this.mReportDialog = new InputDialog(
                            this,
                            null,
                            getString(R.string.activity_complete_activity_report_hint),
                            this, 70, false, false);
                    this.mReportDialog.show();
                }
                break;
            case R.id.action_bar_cheat_report:
            case R.id.action_bar_tip_cheat:
                showComplainDialog();
                break;

        }
    }

    @Override
    public void onSummaryItemClick(int id) {
        super.onSummaryItemClick(id);
        if (id == R.id.summary_cycling_edit) {

            final Intent editIntent = new Intent(this,
                    BaseEditTextActivity.class);
            editIntent.putExtra(EditTextActivity.EXTRA_VALUE, mActivity.getTitle());
            startActivityForResult(editIntent, RC_EDIT_ACTIVITY_TITLE);

            SpeedxAnalytics.onEvent(this, "更改骑行记录名称", "edit_cycling_record_title");
        }
    }

    @Override
    public void onInputDialogClickOk(String text) {
        this.postReport(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case RC_EDIT_ACTIVITY_COVER:// 修改或者删除风景图
                        int resultType = data
                                .getIntExtra(EXTRA_EDIT_ACTIVITY_COVER, -1);
                        if (resultType == 0)
                            this.startWatermarkCamera(true);

                        if (resultType == 1)
                            this.deleteActivityImage();
                        break;

                    case RC_EDIT_ACTIVITY_TITLE:// 修改title
                        Bundle bundle = data.getExtras();
                        String title = bundle.getString(BaseEditTextActivity.EXTRA_VALUE);
                        if (TextUtils.isEmpty(title) || this.mActivity.getTitle().equals(title))
                            return;

                        if (null != this.mActivity) {
                            this.mActivity.setTitle(title);
                            notifySummaryDataChanged(mActivity);
                            // update Activity
                            this.updateCyclingRecordTitle(mActivity.getActivityIdentifier(), title);
                        }
                        break;

                    case RC_ADD_ACTIVITY_IMAGE:
                        String filePath = data
                                .getStringExtra(WatermarkFinishedActivity.EXTRA_PICTURE_PATH);
                        if (TextUtils.isEmpty(filePath))
                            return;

                        final File file = new File(filePath);
                        if (!file.exists())
                            return;

                        this.mSceneryPath = filePath;

                        final BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inSampleSize = 4;

                        // 如果只保存一张图片需要clear
                        this.mLocalSceneryUrls.clear();
                        this.mLocalSceneryUrls.add(this.mSceneryPath);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < this.mLocalSceneryUrls.size(); i++) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("filePath" + 0, this.mLocalSceneryUrls.get(i));
                                array.put(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (null == array || array.length() <= 0
                                || TextUtils.isEmpty(this.mActivityIdentifier))
                            return;

                        try {
                            LocalActivity localActivity = this.mActivityManager
                                    .getLocalActivity(this.mActivityIdentifier);
                            if (null == localActivity)
                                return;

                            this.mActivityManager.updateLocalActivity(localActivity);
                            logger.trace("update local activity local scenery url "
                                    + array.toString());
                        } catch (BusinessException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;

        }
    }

    @Override
    public void onScreenshot(String path) {
        super.onScreenshot(path);

        if (!TextUtils.isEmpty(path)) {
            CommonShareImageDTO mShareImageDTO = new CommonShareImageDTO();
            mShareImageDTO.setImagePath(path);
            mSharePopupWindow = new CommonSharePopupWindow(
                    CyclingCompletedActivity.this, mShareImageDTO, "数据模版");
            mSharePopupWindow.showAtLocation(mSliderLayout,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    // 弹出作弊提示框
    private void showComplainDialog() {
        AVUser user = AVUser.getCurrentUser();
        if (null != user && !user.getObjectId().equals(getUserId())) {
            return;
        }

        final MaterialDialog materialDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_finished_cheat_dialog, null);
        materialDialog.setContentView(view);
        materialDialog.setTitle(R.string.activity_finished_activity_cheat_title);
        materialDialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CyclingCompletedActivity.this,
                        ActivityComplainActivity.class);
                intent.putExtra(ActivityComplainActivity.EXTRA_USER_ID,
                        getUserId());
                if (null != mActivity)
                    intent.putExtra(ActivityComplainActivity.EXTRA_ACTIVITY_ID,
                            mActivity.getActivityId());
                startActivity(intent);
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }

    // 弹出作弊提示框
    private void showMapPrivateDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setTitle(R.string.activity_finished_activity_set_map_private);
        materialDialog.setMessage("");
        materialDialog.setPositiveButton(R.string.label_i_know, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultSp.edit().putBoolean(Constants.PREF_SET_MAP_PRIVATE_FIRST, false).apply();
                isPrivate = true;
                mActivity.setIsPrivate(isPrivate ? 1 : 0);
                updateCyclingRecord(STATUS_PRIVATE);
                speedxMap.switchMapHiddenState(isPrivate);
                materialDialog.dismiss();
            }
        });

        materialDialog.show();
    }

    private void updateCyclingRecord(final int isPrivate) {
        setResult(RESULT_UPDATE);
//        if (this.isPrivate /*&& mapType != MapType.MapBox*/) {
        speedxMap.onDestroy();
        setUpMap();
//        }
        notifySideBarDataChange(mActivity);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    mActivityManager.updateCyclingRecord(mActivity.getActivityIdentifier(), isPrivate);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
    }

    private void dataFormat(ActivityDTO dto) {
        if (dto == null)
            return;

        if (TextUtils.isEmpty(mActivityIdentifier)) {
            mActivityIdentifier = dto.getActivityIdentifier();
        }

        String avatarUrl = getIntent().getStringExtra(EXTRA_AVATAR_URL);
        String nickName = getIntent().getStringExtra(EXTRA_NICK_NAME);

        super.isPrivate = dto.getIsPrivate() == 1;
        dto.setTotalDistance(dto.getTotalDistance() / 1000);
        dto.setNickname(nickName);
        dto.setAvatarUrl(avatarUrl);

        if (TextUtils.isEmpty(dto.getTitle()) || dto.getTitle().equals("null")
                || TextUtils.isEmpty(dto.getTitle().trim())) {
            dto.setTitle(ActivityDataUtil.formatDateTime(this, dto.getStartTime()));
        }

        if ((dto.getVelocity() <= 0 || dto.getVelocity() == Double.NaN) && dto.getElapsedTime() != 0) {
            dto.setVelocity(dto.getTotalDistance() / dto.getElapsedTime() * 3600);
        }

        if (dto.getVelocity() > ActivityService.MAX_VELOCITY) {
            dto.setVelocity(ActivityService.MAX_VELOCITY);
        }

        if (dto.getMaxVelocity() > ActivityService.MAX_VELOCITY) {
            dto.setMaxVelocity(ActivityService.MAX_VELOCITY);
        }

        //国际化单位转换
        if (!LocaleManager.isDisplayKM(this)) {
            dto.setTotalDistance(LocaleManager.kilometreToMile(dto.getTotalDistance()));
            dto.setVelocity(LocaleManager.kphToMph(dto.getVelocity()));
            dto.setMaxVelocity(LocaleManager.kphToMph(dto.getMaxVelocity()));
            dto.setRiseTotal(LocaleManager.metreToFeet(dto.getRiseTotal()));
            dto.setMaxAltitude(LocaleManager.metreToFeet(dto.getMaxAltitude()));
            dto.setUphillDistance(LocaleManager.metreToFeet(dto.getUphillDistance()));
        }
    }

    private void getUserInfo(final String userId) {

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                ProfileDTO userInfo = null;
                try {
                    UserManager userManager = new UserManager(CyclingCompletedActivity.this);
                    userInfo = userManager.getProfileByUserId(userId);

                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return userInfo;
            }

            @Override
            protected void onPostExecute(ProfileDTO userInfo) {
                super.onPostExecute(userInfo);
                if (userInfo != null) {
                    if (mActivity != null) {
                        mActivity.setAvatarUrl(userInfo.getAvatar());
                        mActivity.setNickname(userInfo.getNickname());
                        notifySummaryDataChanged(mActivity);
                    }
                    mProfileDTO = userInfo;
                    getActivityInfoByActivityId(mActivityIdentifier);

                }
            }
        });
    }

    /**
     * Get Activity Info by activityId
     *
     * @param activityId
     */
    private void getActivityInfoByActivityId(final String activityId) {

        if (TextUtils.isEmpty(activityId)) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ActivityDTO>() {
            @Override
            protected ActivityDTO doInBackground(String... params) {
                try {
                    return mActivityManager.getActivityInfoByActivityId(getUserId(), params[0]);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ActivityDTO activityDTO) {
                if (null == activityDTO) {
                    return;
                }

                mActivity = activityDTO;


                dataFormat(mActivity);
                notifyAllDataSetChanged(activityDTO, mSamples, mAltitudes,
                        TextUtils.equals(AVUser.getCurrentUser().getObjectId(), getUserId()));

                getSamples(mActivityIdentifier);
            }
        }, activityId);
    }

    // 获取骑行轨迹打点
    private void getSamples(String activityIdentifier) {

        if (TextUtils.isEmpty(activityIdentifier))
            return;

        String activityId = "";
        if (null != mActivity) {
            activityId = mActivity.getActivityId();
        }

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<SampleDTO>>() {

                    @Override
                    protected void onPreExecute() {
                        loadingShow(true, getString(R.string.activity_complete_activity_loading));
                    }

                    @Override
                    protected List<SampleDTO> doInBackground(String... params) {
                        try {
                            List<SampleDTO> list = mActivityManager.getActivitySamplesByActivityId(
                                    params[0], getUserId(), params[1]);

                            if (null == list || list.isEmpty()) {
                                return null;
                            }

                            Collections.sort(list, mComparator);
                            mAltitudes.clear();
                            List<SampleDTO> resultList = new ArrayList<>();
                            totalMaxAltitude = mActivity.getMaxAltitude();
                            totalMaxHeartRate = mActivity.getMaxCardiacRate();

                            for (SampleDTO sd : list) {
                                double latitude = sd.getLatitude1();
                                double longitude = sd.getLongitude1();

                                if (latitude == 0 || longitude == 0
                                        || latitude == 4.9E-324 || longitude == 4.9E-324)
                                    continue;

                                totalMaxAltitude = Math.max(totalMaxAltitude, sd.getAltitude());

                                if (sd.getVelocity() > totalMaxSpeed) {
                                    totalMaxSpeed = sd.getVelocity();
                                    totalMaxSpeedDis = sd.getDistance();
                                    maxSpeedSample = sd;
                                }

                                if (sd.getAltitude() > totalMaxAltitude) {
                                    totalMaxAltitude = sd.getAltitude();
                                    totalMaxAltitudeDis = sd.getDistance();
                                }

                                if (sd.getCardiacRate() >= totalMaxHeartRate) {
                                    sd.setCardiacRate(totalMaxHeartRate);
                                    totalMaxHeartRateDis = sd.getDistance();
                                }
                                resultList.add(sd);
                            }

                            if (totalMaxSpeed > ActivityService.MAX_VELOCITY) {
                                totalMaxSpeed = ActivityService.MAX_VELOCITY;
                                mActivity.setMaxVelocity(totalMaxSpeed);
                            }
                            mActivity.setMaxAltitude(totalMaxAltitude);

                            // 压缩打点数据
                            if (mActivity.getTotalDistance() > 50) {
                                float tolerance = 0.0001f;
                                resultList = SimplifyUtil.asserSamplesEqual(tolerance, true,
                                        resultList);
                            }

                            return resultList;
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<SampleDTO> result) {
                        loadingDismiss();

                        if (null == result || result.isEmpty() || isFinishing())
                            return;

                        mSamples = result;
                        speedxMap.drawMapPoint(result);

                        fetchCadencesAndHeartRates(result);
                        fetchSpeeds(result);
                        fetch200Elevation(result);
                        notifyStatisticsSlopeChanged(mActivity.getTotalDistance(), result);
                        getGeoCode(result);
                    }

                }, activityId, activityIdentifier);
    }

    /**
     * 速度点数如果小于等于50,则取所有
     * 如果大于50,则为50
     *
     * @param speedSampleDTOs
     */
    private void fetchSpeeds(List<SampleDTO> speedSampleDTOs) {
        List<SampleDTO> sampleDTOs = new ArrayList<>();
        sampleDTOs.addAll(speedSampleDTOs);
        List<Double> speeds = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        int size = sampleDTOs.size();
        //如果打点数不超过50个,则不压缩取点
        if (size <= 50) {
            SampleDTO sampleDTO0 = sampleDTOs.get(0);
            double speed = sampleDTO0.getDistance() / sampleDTO0.getElapsedTime() * 3.6;
            if (speed > mActivity.getMaxVelocity()) {
                speed = mActivity.getMaxVelocity();
            }
            speeds.add(speed);
            distances.add(sampleDTO0.getDistance() / 1000);
            for (int i = 1; i < size; i++) {
                SampleDTO sampleDTO = sampleDTOs.get(i);
                SampleDTO sampleDTOPre = sampleDTOs.get(i - 1);

                speed = (sampleDTO.getDistance() - sampleDTOPre.getDistance()) /
                        (sampleDTO.getElapsedTime() - sampleDTOPre.getElapsedTime()) * 3.6;
                if (speed > mActivity.getMaxVelocity()) {
                    speed = mActivity.getMaxVelocity();
                }
                speeds.add(speed);
                distances.add(sampleDTO.getDistance() / 1000);
            }

            if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                notifyStatisticsSamplesChanged(mActivity, sampleDTOs, listMax(speeds),
                        mActivity.getTotalDistance(), distances, speeds);
            } else {
                notifyStatisticsSamplesChanged(mActivity, sampleDTOs,
                        LocaleManager.kphToMph(listMax(speeds)),
                        LocaleManager.kilometreToMile(mActivity.getTotalDistance()),
                        LocaleManager.kilometreToMileList((ArrayList) distances),
                        LocaleManager.kphToMphList((ArrayList) speeds));
            }
            return;
        }

        //第一个点赋值为0
        speeds.add(0.0);
        distances.add(0.0);

        //否则取出38个点
        //取前5个真实点
        for (int i = 0; i < 5; i++) {
            SampleDTO sampleDTO = sampleDTOs.remove(0);
            speeds.add(sampleDTO.getVelocity());
            distances.add(sampleDTO.getDistance() / 1000);
        }

        //取后5个真实点
        size = sampleDTOs.size();
        for (int i = 5; i > 0; i--) {
            SampleDTO sampleDTO = sampleDTOs.remove(size-- - i);
            speeds.add(sampleDTO.getVelocity());
            distances.add(sampleDTO.getDistance() / 1000);
        }

        int value = sampleDTOs.size() / 37;
        int k = 6;
        double speed = 0;
        for (int i = 1; i <= 37; i++) {
            int endIndex = i * value - 1;
            if (endIndex >= sampleDTOs.size()) {
                speeds.add(k, 0.0);
                Double distance = sampleDTOs.get(sampleDTOs.size() - 1)
                        .getDistance() / 1000;
                distances.add(k, distance);
                k++;
                continue;
            }

            SampleDTO sampleDTO = sampleDTOs.get(endIndex);
            //取中间n个点的平均速度,使用距离比上时间
            long time = sampleDTO.getElapsedTime() - sampleDTOs.get((i - 1) * value).getElapsedTime();

            if (time <= 0) {
                double sum = 0;
                for (int j = (i - 1) * value; j <= endIndex; j++) {
                    sum += sampleDTOs.get(j).getVelocity();
                }
                speed = sum / value;
            } else {
                speed = (sampleDTO.getDistance() - sampleDTOs.get((i - 1) * value).getDistance()) / time * 3.6;
            }

            if (speed > mActivity.getMaxVelocity()) {
                speed = mActivity.getMaxVelocity();
            }

            speeds.add(k, speed);
            distances.add(k, sampleDTO.getDistance() / 1000);
            k++;
        }

        speeds.add(0.0);
        distances.add(mActivity.getTotalDistance());

        //替换最大速度值
        int tempSize = speeds.size();
        for (int i = 1; i < tempSize; i++) {
            if (totalMaxSpeedDis / 1000 >= distances.get(i - 1) && totalMaxSpeedDis / 1000 < distances.get(i)) {
                speeds.add(i, mActivity.getMaxVelocity());
                distances.add(i, totalMaxSpeedDis / 1000);
                break;
            }
        }

        mSampleSize = speeds.size();
        if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
            notifyStatisticsSamplesChanged(mActivity, sampleDTOs, listMax(speeds),
                    mActivity.getTotalDistance(), distances, speeds);
        } else {
            notifyStatisticsSamplesChanged(mActivity, sampleDTOs,
                    LocaleManager.kphToMph(listMax(speeds)),
                    LocaleManager.kilometreToMile(mActivity.getTotalDistance()),
                    LocaleManager.kilometreToMileList((ArrayList<Double>) distances),
                    LocaleManager.kphToMphList((ArrayList<Double>) speeds));
        }
    }

    /**
     * 获取海拔
     *
     * @param sampleDTOs
     */
    private void fetch200Elevation(List<SampleDTO> sampleDTOs) {
        int targetSize = 200;
        int size = sampleDTOs.size();
        StringBuilder elevationStr = new StringBuilder();
        if (size <= targetSize) {
            for (SampleDTO sampleDTO : sampleDTOs) {
                double latitude = sampleDTO.getLatitude1();
                double longitude = sampleDTO.getLongitude1();
                if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                    continue;
                }
                mAltitudes.add(sampleDTO.getAltitude());
                elevationStr.append(latitude).append(",").append(longitude)
                        .append('|');
            }
        } else {
            int value = size / targetSize;
            for (int i = 0; i < targetSize; i++) {
                int endIndex = i * value;
                if (endIndex >= size) {
                    SampleDTO sampleDTO = sampleDTOs.get(sampleDTOs.size() - 1);
                    double latitude = sampleDTO.getLatitude1();
                    double longitude = sampleDTO.getLongitude1();
                    if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                        continue;
                    }

                    mAltitudes.add(sampleDTO.getAltitude());
                    elevationStr.append(latitude).append(",").append(longitude).append('|');
                    continue;
                }

                SampleDTO sampleDTO = sampleDTOs.get(endIndex);
                double latitude = sampleDTO.getLatitude1();
                double longitude = sampleDTO.getLongitude1();
                if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                    continue;
                }
                //取n个值的第一个值
                mAltitudes.add(sampleDTO.getAltitude());
                elevationStr.append(latitude).append(",").append(longitude).append('|');
            }
        }

        if (size > 50) {
            this.fetchElevation(50, elevationStr.substring(0, elevationStr.length() - 1));
        } else {
            this.fetchElevation(size, elevationStr.substring(0, elevationStr.length() - 1));
        }
    }

    /**
     * 获取踏频,心率
     *
     * @param sampleDTOs
     */
    private void fetchCadencesAndHeartRates(List<SampleDTO> sampleDTOs) {
        int size = sampleDTOs.size();

        if (size <= 50) {
            for (int i = 0; i < size; i++) {
                SampleDTO sampleDTO = sampleDTOs.get(i);
                mHeartRates.add(sampleDTO.getCardiacRate());
                mCadences.add(sampleDTO.getCadence());
                mAltitudeDistances.add(sampleDTO.getDistance() / 1000);
            }
        } else {
            int value = size / 50;
            for (int i = 1; i <= 50; i++) {
                int endIndex = i * value;
                if (endIndex >= size) {
                    Double distance = sampleDTOs.get(sampleDTOs.size() - 1)
                            .getDistance() / 1000;
                    mAltitudeDistances.add(distance);
                    double heartRateSum = 0.0;
                    double cadenceSum = 0.0;
                    for (int j = (i - 1) * value; j <= size - 1; j++) {
                        heartRateSum += sampleDTOs.get(j).getCardiacRate();
                        cadenceSum += sampleDTOs.get(j).getCadence();
                    }
                    //取中间n个值的平均值
                    if (heartRateSum / value > totalMaxHeartRate) {
                        mHeartRates.add(totalMaxHeartRate);
                    } else {
                        mHeartRates.add(heartRateSum / value);
                    }
                    mCadences.add(cadenceSum / value);
                    continue;
                }

                SampleDTO sampleDTO = sampleDTOs.get(endIndex);
                double heartRateSum = 0.0;
                double cadenceSum = 0.0;
                for (int j = (i - 1) * value; j <= endIndex; j++) {
                    heartRateSum += sampleDTOs.get(j).getCardiacRate();
                    cadenceSum += sampleDTOs.get(j).getCadence();
                }
                //取中间n个值的平均值
                if (heartRateSum / value > totalMaxHeartRate) {
                    mHeartRates.add(totalMaxHeartRate);
                } else {
                    mHeartRates.add(heartRateSum / value);
                }
                mCadences.add(cadenceSum / value);
                if (i == 1) {
                    mAltitudeDistances.add(0.0);
                } else if (i == 50) {
                    mAltitudeDistances.add(mActivity.getTotalDistance());
                } else {
                    mAltitudeDistances.add(sampleDTO.getDistance() / 1000);
                }

            }
        }

        //替换最大心率值
        int tempSize = mHeartRates.size();
        for (int i = 1; i < tempSize; i++) {
            if (totalMaxHeartRateDis / 1000 >= mAltitudeDistances.get(i - 1) && totalMaxHeartRateDis / 1000 < mAltitudeDistances.get(i)) {
                mHeartRates.remove(i - 1);
                mHeartRates.add(i - 1, totalMaxHeartRate);
                mAltitudeDistances.remove(i - 1);
                mAltitudeDistances.add(i - 1, totalMaxHeartRateDis / 1000);
                break;
            }
        }

        notifyCadencesChanged(mCadences);
        int limitHeartRate = 0;
        if (mProfileDTO != null) {
            limitHeartRate = mProfileDTO.getMaxHeartRate();
        }
        if (LocaleManager.isDisplayKM(this)) {
            notifyStatisticsHeartRateChanged(mActivity, limitHeartRate,
                    mAltitudeDistances, mHeartRates);
        } else {
            notifyStatisticsHeartRateChanged(mActivity, limitHeartRate,
                    LocaleManager.kilometreToMileList(
                            (ArrayList<Double>) mAltitudeDistances), mHeartRates);
        }

    }

    // 绘制海拔曲线
    private void fetchElevation(int size, String elevationStr) {
        if (TextUtils.isEmpty(elevationStr))
            return;

        final List<Double> listElevation = new ArrayList<>();
        final StringBuilder sb = new StringBuilder(
                "http://maps.google.cn/maps/api/elevation/json?path=");
        sb.append(elevationStr).append("&samples=" + mSampleSize);
        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONArray arrayLatLng;
                        if ("OK".equals(response.optString("status"))) {
                            arrayLatLng = response.optJSONArray("results");
                            for (int i = 0; i < arrayLatLng.length(); i++) {
                                try {
                                    JSONObject obj = (JSONObject) arrayLatLng.get(i);
                                    listElevation.add(obj.optDouble("elevation"));
                                } catch (JSONException e) {
                                    logger.error("get elevation error", e);
                                }
                            }
                            double maxAltitude = listMax(listElevation);
                            final double min = listMin(listElevation);
                            mActivity.setMaxAltitude(maxAltitude);

                            if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                                notifyStatisticsElevationChanged(mActivity.getTotalDistance(), mAltitudeDistances, listElevation,
                                        mActivity.getMaxAltitude(), min);
                            } else {
                                notifyStatisticsElevationChanged(mActivity.getTotalDistance(), LocaleManager.
                                                kilometreToMileList((ArrayList<Double>) mAltitudeDistances),
                                        LocaleManager.metreToFeet((ArrayList<Double>) listElevation),
                                        LocaleManager.metreToFeet(mActivity.getMaxAltitude()),
                                        LocaleManager.metreToFeet(min));
                            }

                        } else if ("INVALID_REQUEST".equals(response.optString("status"))) {
                            logger.error("get elevation error",
                                    getString(R.string.route_elevation_activity_error));
                            compressElevations(mAltitudes);
                        } else {
                            logger.error("get elevation error",
                                    getString(R.string.route_elevation_activity_error));
                            compressElevations(mAltitudes);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logger.error("get elevation error", error.getMessage());

                compressElevations(mAltitudes);

            }
        });

        super.mRequestQueue.add(request);
    }

    //压缩为小于等于50个点
    private void compressElevations(List<Double> elevations) {
        int size = elevations.size();
        if (size <= 50) {
            if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                notifyStatisticsElevationChanged(mActivity.getTotalDistance(), mAltitudeDistances, elevations,
                        listMax(elevations), listMin(elevations));
            } else {
                notifyStatisticsElevationChanged(mActivity.getTotalDistance(), LocaleManager.
                                kilometreToMileList((ArrayList<Double>) mAltitudeDistances),
                        LocaleManager.metreToFeet((ArrayList<Double>) elevations),
                        LocaleManager.metreToFeet(listMax(elevations)), LocaleManager.metreToFeet(listMin(elevations)));
            }
        } else {
            ArrayList<Double> arrayList = new ArrayList<>();
            int value = size / 50;
            for (int i = 0; i < 50; i++) {
                int endIndex = i * value;
                if (endIndex >= size) {
                    arrayList.add(elevations.get(size - 1));
                    continue;
                }

                arrayList.add(elevations.get(endIndex));
            }

            if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                notifyStatisticsElevationChanged(mActivity.getTotalDistance(), mAltitudeDistances, arrayList,
                        listMax(arrayList), listMin(arrayList));
            } else {
                notifyStatisticsElevationChanged(mActivity.getTotalDistance(),
                        LocaleManager.kilometreToMileList((ArrayList<Double>) mAltitudeDistances),
                        LocaleManager.metreToFeet((ArrayList<Double>) arrayList),
                        LocaleManager.metreToFeet(listMax(arrayList)), LocaleManager.metreToFeet(listMin(arrayList)));
            }
        }
    }

    private void getGeoCode(List<SampleDTO> result) {
        if (null == result || result.isEmpty() || result.size() <= 0) {
            return;
        }

        if (getUserId().equals(mUser.getObjectId())) {
            SampleDTO startPoint = result.get(0);
            GoogleMapCnAPI googleMapCnAPI = new GoogleMapCnAPI();
            googleMapCnAPI.geoCode(super.mRequestQueue,
                    startPoint.getLatitude1(), startPoint.getLongitude1(),
                    new GoogleMapCnCallBack() {
                        @Override
                        public void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean) {
                            if (googleMapCnBean != null) {
                                mCityName = googleMapCnBean.getCityName();
                            }
                        }

                        @Override
                        public void onGetGeoInfoError(VolleyError volleyError) {

                        }
                    });
        }
    }

    /**
     * Update Cycling Record Title
     *
     * @param activityId Cycling ID
     * @param title      Cycling Title
     */
    private void updateCyclingRecordTitle(final String activityId, final String title) {
        if (TextUtils.isEmpty(activityId) || TextUtils.isEmpty(title)) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return mActivityManager.updateCyclingRecordTitle(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Toasts.show(CyclingCompletedActivity.this, R.string.toast_update_success);
                } else {
                    Toasts.show(CyclingCompletedActivity.this, R.string.toast_update_error);
                }
            }
        }, activityId, title);
    }

    private void uploadLocalActivity() {
        if (null == mActivity) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    LocalActivity localActivity = mActivityManager.
                            getLocalActivity(mActivity.getActivityIdentifier());
                    if (null != localActivity) {
                        return mActivityManager.saveSamples(localActivity);
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer == 0) {
                    Toasts.show(CyclingCompletedActivity.this,
                            R.string.setting_fragment_item_upload_error_log_success);
                } else {
                    Toasts.show(CyclingCompletedActivity.this,
                            R.string.setting_fragment_item_upload_error);
                }
            }
        });
    }


    /*
     * 跳转到水印相机页面
     */
    private void startWatermarkCamera(boolean forResult) {
        Intent intent = new Intent(this, WatermarkCameraActivity.class);
        UserManager userManager = new UserManager(this);
        String nickName = "";
        String cityName = "";
        try {
            LocalUser localUser = userManager.getLocalUser(getUserId());
            if (null != localUser) {
                nickName = localUser.getNickname();
                cityName = localUser.getCity();
            }
        } catch (BusinessException e) {
            e.printStackTrace();
        }


        if (null != mActivity) {
            mActivity.setNickname(nickName);
            mActivity.setCityName(cityName);
            if (!TextUtils.isEmpty(this.mCityName))
                mActivity.setCityName(this.mCityName);
            intent.putExtra(WatermarkCameraActivity.EXTRA_ACTIVITY_DTO,
                    mActivity);
        }

        if (forResult)
            startActivityForResult(intent, RC_ADD_ACTIVITY_IMAGE);
        else
            startActivity(intent);
    }

    /**
     * 删除骑行记录风景图
     */
    public void deleteActivityImage() {
        if (TextUtils.isEmpty(this.mActivityIdentifier))
            return;

        try {
            final LocalActivity localActivity = this.mActivityManager
                    .getLocalActivity(mActivityIdentifier);
            this.mActivityManager.updateLocalActivity(localActivity);

            this.mSceneryPath = "";
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 举报
     *
     * @param message
     */
    private void postReport(final String message) {
        if (TextUtils.isEmpty(mActivity.getActivityId())) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                loadingShow(true, null);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return mActivityManager.postReportSportRoute(mActivity.getActivityId(),
                            params[0]);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                loadingDismiss();

                if (null != mReportDialog) {
                    mReportDialog.dismiss();
                }

                mActivity.setHasReport(true);
                notifyActionBarDataChanged(mActivity);
            }

        }, message);
    }


    private void postShare() {
        if (mShareImageDTO == null) {
            loadingShow(true, getString(R.string.activity_complete_activity_create_share_loading));
            speedxMap.snapshot(new MapBase.SnapshotReadyListener() {
                @Override
                public void onSnapshotReady(Bitmap bitmapMap) {
                    if (bitmapMap == null)
                        return;

                    Bitmap bitmapSummary = BitmapUtil.getBitmapByView(mSummary);

                    RecordShare share = new RecordShare(CyclingCompletedActivity.this);
                    mShareView.removeAllViews();
                    mShareView.addView(share);

                    share.build(bitmapSummary,
                            bitmapMap, mActivity, new RecordShare.ShareBuildListener() {
                                @Override
                                public void onShareBuild(final Bitmap bitmap) {
                                    if (bitmap == null)
                                        return;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingDismiss();
                                            String sharePath = BitmapUtil.saveImage(bitmap);
                                            if (!TextUtils.isEmpty(sharePath)) {
                                                mShareImageDTO = new CommonShareImageDTO();
                                                mShareImageDTO.setImagePath(sharePath);
                                                mSharePopupWindow = new CommonSharePopupWindow(
                                                        CyclingCompletedActivity.this,
                                                        mShareImageDTO, "数据模版");
                                                mSharePopupWindow
                                                        .showAtLocation(mSliderLayout,
                                                                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                            }
                                        }
                                    });

                                }
                            });

                }
            });
        } else {
            mSharePopupWindow = new CommonSharePopupWindow(CyclingCompletedActivity.this,
                    mShareImageDTO, "数据模版");
            mSharePopupWindow
                    .showAtLocation(mSliderLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
        SpeedxAnalytics.onEvent(this, "分享数据模版", "click_ridding_history_share_data_report");

    }


    /**
     * 获取List中的最大值
     *
     * @param list
     */
    private double listMax(List<Double> list) {
        if (list.isEmpty()) {
            return 50;
        }

        return Collections.max(list);
    }

    /**
     * 获取List中的最小值
     *
     * @param list
     */
    private double listMin(List<Double> list) {
        if (list.isEmpty()) {
            return 0;
        }

        return Collections.min(list);
    }


}