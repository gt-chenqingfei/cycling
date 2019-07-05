package com.beastbikes.android.modules.preferences.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.Wheelview;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnBean;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnCallBack;
import com.beastbikes.android.locale.locationutils.UtilsLocationCallBack;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ImageUploader;
import com.beastbikes.android.modules.cycling.club.dto.ImageInfo;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.utils.Utils;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenqingfei on 16/1/26.
 */

@Alias("个人设置页")
@LayoutResource(R.layout.user_setting_fragment)
public class UserSettingFragment extends SessionFragment implements
        View.OnClickListener, UtilsLocationCallBack, GoogleMapCnCallBack,
        RequestQueueManager, DatePickerDialog.OnDateSetListener {


    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserSettingActivity.class);


    private static final int RC_EDIT_NICKNAME = 1;

    private static final int RC_EDIT_AVATAR_GALLERY = 2;

    private static final int RC_EDIT_AVATAR_CAMERA = 3;

    private static final int RC_EDIT_AVATAR_CROP = 4;

    @IdResource(R.id.user_setting_activity_avatar)
    private ViewGroup grpAvatar;

    @IdResource(R.id.user_setting_activity_avatar_value)
    private CircleImageView imgAvatar;

    @IdResource(R.id.user_setting_activity_nickname)
    private ViewGroup grpNickname;

    @IdResource(R.id.user_setting_activity_nickname_value)
    private TextView txtNickname;

    @IdResource(R.id.user_setting_activity_gender)
    private ViewGroup grpGender;

    @IdResource(R.id.user_setting_activity_gender_value)
    private TextView txtGender;

    @IdResource(R.id.user_setting_activity_location)
    private ViewGroup grpLocation;

    @IdResource(R.id.user_setting_activity_location_value)
    private TextView txtLocation;

    @IdResource(R.id.user_setting_activity_height)
    private ViewGroup grpHeight;

    @IdResource(R.id.user_setting_activity_height_value)
    private TextView txtHeight;

    @IdResource(R.id.user_setting_activity_weight)
    private ViewGroup grpWeight;

    @IdResource(R.id.user_setting_activity_weight_value)
    private TextView txtWeight;


    @IdResource(R.id.user_setting_activity_email)
    private ViewGroup vgEmail;

    @IdResource(R.id.user_setting_activity_email_value)
    private TextView txtEmail;

    @IdResource(R.id.user_setting_activity_birth)
    private ViewGroup vgBirth;

    @IdResource(R.id.user_setting_activity_birth_value)
    TextView txtBirth;

    @IdResource(R.id.user_setting_activity_button_sign_out)
    private TextView btnSignOut;

    private UserManager usrManager;

    private File tempFile = new File(
            Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            getPhotoFileName());

    private static String avatarPath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            + "_Beast" + System.currentTimeMillis() + ".jpg";

    private String path = "";

    private String province;
    private String city;
    private String area;

    private double height = 170;//身高
    private double weight = 65;//体重
    private boolean isChineseVersion = true;

    private int year = 1990;
    private int month = 1;
    private int day = 1;

    private RequestQueue requestQueue;

    @Override
    public final RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.requestQueue = RequestQueueFactory.newRequestQueue(getActivity());


        UtilsLocationManager.getInstance().getLocation(getActivity(), this);
        if (!LocaleManager.isDisplayKM(UserSettingFragment.this.getActivity())) {
            isChineseVersion = false;
            height = 180.34;
            weight = 77.1107029;
        }

        final Intent intent = getActivity().getIntent();
        final AVUser user = AVUser.getCurrentUser();
        if (user == null) {
            getActivity().finish();
            return;
        }
        if (intent.hasExtra(UserSettingActivity.EXTRA_FROM_SETTING)
                && intent.getBooleanExtra(UserSettingActivity.EXTRA_FROM_SETTING, false)) {
        } else if (intent.hasExtra(UserSettingActivity.EXTRA_FROM_AUTH)
                && 2 == intent.getIntExtra(UserSettingActivity.EXTRA_FROM_AUTH, 0)) {
            this.txtEmail
                    .setText(getString(R.string.user_setting_activity_value_email));
            getView().findViewById(R.id.user_setting_fragment_line1).setVisibility(View.GONE);
            getView().findViewById(R.id.user_setting_fragment_line2).setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnSignOut.getLayoutParams();
            params.leftMargin = DensityUtil.dip2px(15, getActivity());
            params.rightMargin = DensityUtil.dip2px(15, getActivity());
            params.topMargin = DensityUtil.dip2px(15, getActivity());
            params.bottomMargin = DensityUtil.dip2px(15, getActivity());

            btnSignOut.setBackgroundResource(R.drawable.border_1px_solid_b9b9b9_width_radius_5dp);
            btnSignOut.setTextColor(getResources().getColor(R.color.text_number_color));
        } else {
            this.btnSignOut.setVisibility(View.GONE);
        }

        this.usrManager = new UserManager(getActivity());
        this.grpAvatar.setOnClickListener(this);
        this.grpGender.setOnClickListener(this);
        this.grpHeight.setOnClickListener(this);
        this.grpLocation.setOnClickListener(this);
        this.grpNickname.setOnClickListener(this);
        this.grpWeight.setOnClickListener(this);
        this.btnSignOut.setOnClickListener(this);
        this.vgBirth.setOnClickListener(this);

        this.txtGender .setText(R.string.user_setting_activity_label_gender_male);
        this.txtEmail.setText(user.getEmail());

        fetchProfileInfo();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        final Intent intent = getActivity().getIntent();
        super.onCreateOptionsMenu(menu, inflater);

        if (intent.hasExtra(UserSettingActivity.EXTRA_FROM_AUTH) && menu != null) {
            menu.getItem(0).setVisible(false);
        }
    }
    private void updateUserInformation() {
        final String nickname = this.txtNickname.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            Toasts.show(getActivity(), R.string.user_setting_activity_save_error);
            return;
        }

        final Activity ctx = getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final File file = new File(path);
                final AVUser user = AVUser.getCurrentUser();
                if (null == user)
                    return;

                final String email = user.getEmail();
                if (TextUtils.isEmpty(email)) {
                    user.setEmail("");
                }

                ProfileDTO dto = new ProfileDTO();
                dto.setUserId(user.getObjectId());
                final String gender = txtGender.getText().toString();

                if (file.exists()) {
                    Toasts.showOnUiThread(ctx, R.string.user_setting_activity_save_waiting);
                    ImageUploader uploader = new ImageUploader();

                    String fileName = null;
                    try {
                        fileName = AlgorithmUtils.md5(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ImageInfo info = uploader.withFile(file, fileName, ImageUploader.TYPE_USER_AVATAR).saveSync();
                    if (info != null) {
                        dto.setAvatar(fileName);
                    }
                }

                dto.setNickname(nickname);
                if (!TextUtils.isEmpty(province)) {
                    dto.setProvince(province);
                    dto.setCity(city);
                    dto.setDistrict(area);
                }

                dto.setSex(gender.equals(ctx.getString(R.string.user_setting_activity_label_gender_male)) ? 1 : 0);

                dto.setHeight(UserSettingFragment.this.height);
                dto.setWeight(UserSettingFragment.this.weight);

                String birthday = txtBirth.getText().toString();
                birthday = birthday.replace(".", "-");
                dto.setBirthday(birthday);

                try {
                    usrManager.updateRemoteUserInfo(dto);

                } catch (BusinessException e) {
                    logger.error("update UserInfo failed", e);
                }
            }
        }).start();

        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
            getActivity().finish();
        }
    }


    private void fetchProfileInfo() {
        final String userId = this.getUserId();
        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ProfileDTO>() {

                    @Override
                    protected ProfileDTO doInBackground(
                            String... params) {
                        try {
                            return usrManager.getProfileFromLocal(params[0]);
                        } catch (BusinessException e) {
                            logger.error("Get RemoteUserInfo failed", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(ProfileDTO rui) {
                        if (null == rui)
                            return;

                        updateUI(rui);
                    }

                }, userId);
    }


    private void updateUI(ProfileDTO dto) {
        final AVUser user = AVUser.getCurrentUser();
        if (null != user && !TextUtils.isEmpty(user.getAvatar())) {
            Picasso.with(getActivity()).load(user.getAvatar()).
                    fit().centerCrop().error(R.drawable.ic_avatar).
                    placeholder(R.drawable.ic_avatar).into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar);
        }

        final String nickname = dto.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            this.txtNickname.setText(nickname);
        } else {
            this.txtNickname.setText(this.txtNickname.getText().toString());
        }

        switch (dto.getSex()) {
            case 0:
                this.txtGender.setText(R.string.user_setting_activity_label_gender_female);
                break;
            default:
                this.txtGender.setText(R.string.user_setting_activity_label_gender_male);
                break;
        }

        this.txtLocation.setText(dto.getLocation());

        if (TextUtils.isEmpty(dto.getEmail()) || dto.getEmail().endsWith("@beastbikes.default.com")) {
            vgEmail.setVisibility(View.GONE);
            getView().findViewById(R.id.user_setting_fragment_line3).setVisibility(View.GONE);
        } else {
            vgEmail.setVisibility(View.VISIBLE);
            txtEmail.setText(dto.getEmail());

        }
        logger.trace("height=" + dto.getHeight() + "weight=" + dto.getWeight());

        if (isChineseVersion) {
            txtHeight.setText((int) dto.getHeight() + "cm");
            txtWeight.setText((int) dto.getWeight() + "kg");
        } else {
            txtHeight.setText(LocaleManager.cm2Feet(dto.getHeight()) + "′" + LocaleManager.cmToInchInt(dto.getHeight()) + "′′");
            txtWeight.setText((int) Math.round(LocaleManager.kgToLb(dto.getWeight())) + "lb");
        }
        this.height = dto.getHeight();
        this.weight = dto.getWeight();

        if (!TextUtils.isEmpty(dto.getBirthday())) {
            String[] split = dto.getBirthday().split("-");
            year = Integer.valueOf(Utils.numericFilter(split[0]));
            month = Integer.valueOf(Utils.numericFilter(split[1]));
            day = Integer.valueOf(Utils.numericFilter(split[2]));
        }

        txtBirth.setText(year + "." + month + "." + day + "");
    }

    private void showDataPicker() {

        String birthday = txtBirth.getText().toString();
        String[] split = birthday.split("\\.");

        if (!TextUtils.isEmpty(birthday)) {
            year = Integer.valueOf(Utils.numericFilter(split[0]));
            month = Integer.valueOf(Utils.numericFilter(split[1])) - 1;
            day = Integer.valueOf(Utils.numericFilter(split[2]));
        } else {
            year = 1990;
            month = 1;
            day = 1;
        }

        if (month <= 0) {
            month = 1;
        }

        DatePickerDialog dpd = DatePickerDialog.newInstance(this, year, month, day);

        dpd.setMaxDate(Calendar.getInstance());
        dpd.setMinDate(Utils.string2Calendar(1900, 1, 1));

        dpd.setAccentColor(getResources().getColor(R.color.bg_theme_black_color));
        dpd.show(getActivity().getFragmentManager(), "DatePickerOfBirth");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_setting_activity_avatar:
                ShowPickDialog();
                break;
            case R.id.user_setting_activity_nickname:
                startActivityForResult(
                        new Intent(getActivity(), EditTextActivity.class)
                                .putExtra(
                                        EditTextActivity.EXTRA_VALUE,
                                        this.txtNickname.getText().toString()
                                                .trim())
                                .putExtra(EditTextActivity.EXTRA_FROM_SETTING, true),
                        RC_EDIT_NICKNAME);
                break;
            case R.id.user_setting_activity_gender: {
                final String gender = this.txtGender.getText().toString();
                if (gender
                        .equals(this
                                .getString(R.string.user_setting_activity_label_gender_male))) {
                    this.txtGender
                            .setText(R.string.user_setting_activity_label_gender_female);
                } else {
                    this.txtGender
                            .setText(R.string.user_setting_activity_label_gender_male);
                }
                break;
            }
            case R.id.user_setting_activity_location:
                this.txtLocation
                        .setText(R.string.user_setting_activity_label_text_locating);

                UtilsLocationManager.getInstance().getLocation(getActivity(), this);
                break;
            case R.id.user_setting_activity_height:

                if (isChineseVersion) {
                    showPopWindowSingle("cm", 140, 200, (int) height, new Wheelview.SelectFinishListener() {
                        @Override
                        public void endSelect(String value) {
                            UserSettingFragment.this.height = Double.parseDouble(value);
                            Log.e("height", UserSettingFragment.this.height + "");
                            txtHeight.setText(String.valueOf(Integer.parseInt(value)) + "cm");
                        }
                    });
                } else {
                    showPopWindow("′", "〃", LocaleManager.cm2Feet(height), LocaleManager.cmToInchInt(height), new Wheelview.SelectFinishListenerTwo() {
                        @Override
                        public void endSelect(String value1, String value2) {
                            txtHeight.setText(value1 + "′" + value2 + "′′");
                            UserSettingFragment.this.height = LocaleManager.inchTOCM(12 * Double.parseDouble(value1) + Double.parseDouble(value2));
                            Log.e("height", UserSettingFragment.this.height + "");
                        }
                    });
                }
                break;
            case R.id.user_setting_activity_weight:

                if (isChineseVersion) {
                    showPopWindowSingle("kg", 40, 150, Math.round((float) weight), new Wheelview.SelectFinishListener() {
                        @Override
                        public void endSelect(String value) {
                            UserSettingFragment.this.weight = Integer.parseInt(value);
                            txtWeight.setText(Math.round((float) UserSettingFragment.this.weight) + "kg");
                            Log.e("weight", "" + UserSettingFragment.this.weight);
                        }
                    });
                } else {
                    showPopWindowSingle("lb", 88, 330, Math.round((float) LocaleManager.kgToLb(weight)), new Wheelview.SelectFinishListener() {
                        @Override
                        public void endSelect(String value) {
                            UserSettingFragment.this.weight = LocaleManager.lbToKg(Double.parseDouble(value));
                            txtWeight.setText(value + "lb");
                            Log.e("weight", UserSettingFragment.this.weight + "lb");
                            Log.e("LB", LocaleManager.kgToLb(UserSettingFragment.this.weight) + "");
                            Log.e("LB", Math.round(LocaleManager.kgToLb(UserSettingFragment.this.weight)) + "");
                        }
                    });
                }
                break;
            case R.id.user_setting_activity_button_sign_out:
                updateUserInformation();
                break;

            case R.id.user_setting_activity_birth:
                showDataPicker();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_EDIT_NICKNAME:
                if (Activity.RESULT_OK == resultCode) {
                    Bundle bundle = data.getExtras();
                    String editable = bundle
                            .getString(EditTextActivity.EXTRA_VALUE);
                    this.txtNickname.setText(editable);
                }
                break;
            case RC_EDIT_AVATAR_GALLERY:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = null;
                    String picturePath = null;
                    try {
                        c = getActivity().getContentResolver().query(selectedImage,
                                filePathColumns, null, null, null);
                        if (null != c && c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(filePathColumns[0]);
                            picturePath = c.getString(columnIndex);
                        } else {
                            AVAnalytics.onError(getActivity(), "get photo path failed:"
                                    + selectedImage.toString());
                        }
                    } catch (Exception e) {
                        AVAnalytics.onError(getActivity(), Log.getStackTraceString(e));
                        logger.error("Get photo path from gallery failed", e);
                    } finally {
                        if (null != c && !c.isClosed()) {
                            c.close();
                        }

                        if (TextUtils.isEmpty(picturePath))
                            picturePath = selectedImage.getPath();
                    }

                    Intent i = new Intent(getActivity(), CutAvatarActivity.class);
                    i.putExtra(CutAvatarActivity.EXTRA_AVATAR_PATH, picturePath);
                    startActivityForResult(i, RC_EDIT_AVATAR_CROP);
                }
                break;
            case RC_EDIT_AVATAR_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Intent i = new Intent(getActivity(), CutAvatarActivity.class);
                    i.putExtra(CutAvatarActivity.EXTRA_AVATAR_PATH,
                            tempFile.getAbsolutePath());
                    startActivityForResult(i, RC_EDIT_AVATAR_CROP);
                }
                break;
            case RC_EDIT_AVATAR_CROP:
                if (resultCode == RC_EDIT_AVATAR_CROP) {
                    final String path = data
                            .getStringExtra(CutAvatarActivity.EXTRA_AVATAR_PATH);
                    this.path = path;

                    if (!TextUtils.isEmpty(path)) {
                        Picasso.with(getActivity()).load("file://" + path).fit()
                                .centerCrop().error(R.drawable.ic_avatar)
                                .placeholder(R.drawable.ic_avatar).into(this.imgAvatar);
                    } else {
                        this.imgAvatar.setImageResource(R.drawable.ic_avatar);
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void ShowPickDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(
                        getString(R.string.user_setting_activity_setting_avatar_title))
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
                                        RC_EDIT_AVATAR_GALLERY);
                                logger.info("select image from gallery");
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
                                        RC_EDIT_AVATAR_CAMERA);
                                logger.info("select image from camera");
                            }
                        }).show();
    }

    private String getPhotoFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'BEAST'_yyyyMMdd_HHmmss");
        return dateFormat.format(new Date()) + ".jpg";
    }


    @Override
    public void onLocationChanged(Location location) {
        GoogleMapCnAPI googleMapCnAPI = new GoogleMapCnAPI();
        googleMapCnAPI.geoCode(this.getRequestQueue(), location.getLatitude(), location.getLongitude(), this);
    }

    @Override
    public void onLocationFail() {

    }

    @Override
    public void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean) {
        this.txtLocation.setText(googleMapCnBean.getAddress());

        String[] address = googleMapCnBean.getAddress().split(",");
        switch (address.length) {
            case 1:
                this.area = address[0];
                break;
            case 2:
                this.province = address[0];
                this.area = address[1];
                break;
            case 3:
                this.province = address[0];
                this.city = address[1];
                this.area = address[2];
                break;
            default:
                break;
        }
    }

    @Override
    public void onGetGeoInfoError(VolleyError volleyError) {

    }

    static boolean bitmap2file(Bitmap bmp) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(avatarPath);
            return bmp.compress(format, 100, stream);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                    stream = null;
                } catch (IOException e) {
                }
            }
        }

    }

    private void showPopWindow(final String unit1, final String unit2, int defaultValue1, int defaultValue2, final Wheelview.SelectFinishListenerTwo selectFinishListenerTwo) {//传入单位和数值回调
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View popWindowView = layoutInflater.inflate(R.layout.popwindowpickheight, null, false);
        final PopupWindow popWindow = new PopupWindow(popWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                UserSettingFragment.this.backgroundAlpha(1);
            }
        });

        ArrayList<String> data1 = new ArrayList<String>();
        for (int i = 4; i < 7; i++) {
            data1.add(i + unit1);
        }
        ArrayList<String> data2 = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            data2.add(i + unit2);
        }

        final Wheelview one = (Wheelview) popWindowView.findViewById(R.id.one);
        one.setData(data1);

        final Wheelview two = (Wheelview) popWindowView.findViewById(R.id.two);
        two.setData(data2);

        if (defaultValue1 - 4 < 0) {
            one.setDefault(0);
        } else if (defaultValue1 - 4 > 2) {
            one.setDefault(2);
        } else {
            one.setDefault(defaultValue1 - 4);
        }
        if (defaultValue2 < 0) {
            two.setDefault(0);
        } else if (defaultValue2 > 11) {
            two.setDefault(11);
        } else {
            two.setDefault(defaultValue2);
        }
        TextView savebtn = (TextView) popWindowView.findViewById(R.id.savebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFinishListenerTwo.endSelect(one.getSelectedText().replace(unit1, ""), two.getSelectedText().replace(unit2, ""));
                popWindow.dismiss();
            }
        });
        TextView cancelbtn = (TextView) popWindowView.findViewById(R.id.cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        UserSettingFragment.this.backgroundAlpha(0.5);
        popWindow.showAtLocation(getView().findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
    }

    private void showPopWindowSingle(final String unit, int start, int end, int defaultValue, final Wheelview.SelectFinishListener selectFinishListener) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View popWindowView = layoutInflater.inflate(R.layout.popwindowpickcommon, null, false);
        final PopupWindow popWindow = new PopupWindow(popWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                UserSettingFragment.this.backgroundAlpha(1);
            }
        });

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            arrayList.add(i + unit);
        }

        final Wheelview wheelView = (Wheelview) popWindowView.findViewById(R.id.one);
        wheelView.setData(arrayList);
        if (defaultValue - start < 0) {
            wheelView.setDefault(0);
        } else if (defaultValue - start > end) {
            wheelView.setDefault(end);
        } else {
            int index = defaultValue - start;
            if(index <wheelView.getListSize()) {
                wheelView.setDefault(defaultValue - start);
            }
        }
        TextView savebtn = (TextView) popWindowView.findViewById(R.id.savebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFinishListener.endSelect(wheelView.getSelectedText().replace(unit, ""));
                popWindow.dismiss();
            }
        });
        TextView cancelbtn = (TextView) popWindowView.findViewById(R.id.cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        UserSettingFragment.this.backgroundAlpha(0.5);
        popWindow.showAtLocation(getView().findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
    }

    public void backgroundAlpha(double d) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = (float) d; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        txtBirth.setText(year + "." + month + "." + dayOfMonth + "");
    }
}
