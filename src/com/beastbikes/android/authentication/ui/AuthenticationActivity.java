package com.beastbikes.android.authentication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationBean;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.authentication.AuthenticationException;
import com.beastbikes.android.authentication.biz.AuthenticationManager;
import com.beastbikes.android.authentication.biz.AuthenticationManager.AuthenticationCallback;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.main.MainActivity;
import com.beastbikes.android.main.MeiZuSettingActivity;
import com.beastbikes.android.main.MiuiSettingActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.preferences.ui.UserSettingActivity;
import com.beastbikes.android.modules.preferences.ui.UserSettingActivityFromAuth;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.ui.binding.CountryPageActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.utils.Utils;
import com.beastbikes.android.widget.blureffect.BlurUtil;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

@Alias("注册登录页(不区分注册页或登录页)")
@LayoutResource(R.layout.authentication_activity)
public class AuthenticationActivity extends BaseFragmentActivity implements
        OnClickListener, AuthenticationCallback, Constants {

    //完善个人信息
    private static final int REQUEST_ADD_PROFILE_INFO = 0x1;
    private static final int REQUEST_FIND_PASSWORD = 0x02;

    private static final Logger logger = LoggerFactory
            .getLogger(AuthenticationActivity.class);
    private static final String TAG = "AuthenticationActivity";

    @IdResource(R.id.authentication_activity_sigin_up_switch_fragment)
    private ViewGroup vgSignUpSwitch;

    @IdResource(R.id.authentication_switch_to_sign_up_by_phone)
    private TextView btnSwitchPhone;

    @IdResource(R.id.authentication_switch_to_sign_up_by_email)
    private TextView btnSwitchEmail;

    @IdResource(R.id.authentication_activity_form_switch)
    private TextView btnSwitch;

    @IdResource(R.id.authentication_sign_up_switch)
    private TextView btnSignUpSwitch;

    private ImageButton btnTecent;

    private ImageButton btnWeibo;

    private ImageButton btnWeiXin;

    private ImageButton btnFacebook;

    private ImageButton btnTwitter;

    private ImageButton btnGooglePlus;

    @IdResource(R.id.authentication_activity_sign_in_form)
    private ViewGroup vgSignIn;

    @IdResource(R.id.authentication_sign_in_fragment_email)
    private EditText txtSignInEmail;

    @IdResource(R.id.authentication_sign_in_fragment_password)
    private EditText txtSignInPassword;

    @IdResource(R.id.authentication_sign_up_fragment_username)
    private EditText txtSignUpUsername;

    @IdResource(R.id.authentication_sign_up_fragment_email)
    private EditText txtSignUpEmail;

    @IdResource(R.id.authentication_sign_up_fragment_password)
    private EditText txtSignUpPassword;

    @IdResource(R.id.authentication_activity_sign_up_by_email)
    private ViewGroup vgSignUpByEmail;

    @IdResource(R.id.authentication_activity_sign_up_by_phone)
    private ViewGroup vgSignUpByPhone;

    @IdResource(R.id.authentication_sign_up_fragment_zone)
    private TextView tvZone;

    @IdResource(R.id.authentication_send_valid)
    private Button btnSendVerificationCode;

    @IdResource(R.id.authentication_sign_up_fragment_button_sign_up1)
    private Button btnSignUpPhone;

    @IdResource(R.id.authentication_sign_up_fragment_valid)
    private EditText etSignUpValid;

    @IdResource(R.id.authentication_sign_up_fragment_password1)
    private EditText txtSignUpPasswordPhone;

    @IdResource(R.id.authentication_sign_up_fragment_username1)
    private EditText txtSignUpUsernamePhone;

    @IdResource(R.id.authentication_sign_up_fragment_phone)
    private EditText etSignUpPhone;

    @IdResource(R.id.authentication_sign_in_fragment_button_sign_in)
    private Button btnSignIn;

    @IdResource(R.id.authentication_sign_up_fragment_button_sign_up)
    private Button btnSignUp;

    @IdResource(R.id.authentication_sign_in_fragment_forget_password)
    private View btnForgetPassword;

    @IdResource(R.id.authentication_sign_in_fragment_forget_password_fl)
    private View btnForgetPasswordFL;

    @IdResource(R.id.authentication_blurimage)
    private ImageView blurImage;

    @IdResource(R.id.authentication_blurimage)
    private ImageView fadeImage;

    private TextView bottomView1;

    private TextView bottomView2;

    private AuthenticationManager authManager;

    private LoadingDialog loadingDialog;

    private UserManager userManager;

//    private SNSType snsType;

    private CountDownTimer timer;
    private static final String COUNT_TIME_KEY = "COUNTTIME";
    private int countTimeValue = 0;

    private AnimationSet animation1;

    //    private String wechatLogin = "0";
    private String facebookLogin = "0";

    private String googlePlusLogin = "0";

    private List<View> mViewList = new ArrayList<>();
    private SharedPreferences sp;

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.sp = getSharedPreferences(getPackageName(), 0);

        final BeastBikes app = (BeastBikes) this.getApplication();
        this.authManager = new AuthenticationManager(app);
        animation1 = animationSetInit();
        this.txtSignUpUsername
                .setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        this.btnSwitch.setOnClickListener(this);
        this.btnSignIn.setOnClickListener(this);
        this.btnSignUp.setOnClickListener(this);
        this.btnForgetPassword.setOnClickListener(this);
        this.btnForgetPasswordFL.setOnClickListener(this);
        this.btnSwitchEmail.setOnClickListener(this);
        this.btnSwitchPhone.setOnClickListener(this);
        this.btnSignUpSwitch.setOnClickListener(this);
        this.btnSignUpPhone.setOnClickListener(this);

        this.btnSendVerificationCode.setOnClickListener(this);
        this.tvZone.setOnClickListener(this);
        this.tvZone.setText("+" + LocaleManager.getCountryCode(this));
        //为edittext清空文本框的图片添加点击事件
        this.txtSignInEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getCompoundDrawables() 可以获取一个长度为4的数组，
                //存放drawableLeft，Right，Top，Bottom四个图片资源对象
                //index=2 表示的是 drawableRight 图片资源对象
                Drawable drawable = txtSignInEmail.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                //drawable.getIntrinsicWidth() 获取drawable资源图片呈现的宽度
                if (event.getX() > txtSignInEmail.getWidth() - txtSignInEmail.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    //进入这表示图片被选中，可以处理相应的逻辑了
                    txtSignInEmail.setText("");
                }

                return false;
            }
        });

        this.btnSignUpSwitch.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.userManager = new UserManager(this);
        if (AVUser.getCurrentUser() != null) {
            this.switchToSignInForm();
            txtSignInEmail.setText(AVUser.getCurrentUser().getEmail());
        } else {
            this.switchToSwitchSignUp();
        }

        LayoutInflater mLayoutInflater = getLayoutInflater();

        //可以按照需求进行动态创建Layout,这里暂用静态的xml layout
        View view1 = mLayoutInflater.inflate(R.layout.authentication_sign_by_qq_weibo_weixin, null);
        bottomView1 = (TextView) findViewById(R.id.authentication_activity_bottom_view1);
        bottomView1.setSelected(true);
        View view2 = mLayoutInflater.inflate(R.layout.authentication_sign_by_facebook_twitter_googleplus, null);
        bottomView2 = (TextView) findViewById(R.id.authentication_activity_bottom_view2);
        if (LocaleManager.isChineseTimeZone()) {
            mViewList.add(view1);
            mViewList.add(view2);
        } else {
            mViewList.add(view2);
            mViewList.add(view1);
        }

        btnWeiXin = (ImageButton) view1.findViewById(R.id.authentication_activity_auth_weixin);

        btnTecent = (ImageButton) view1.findViewById(R.id.authentication_activity_auth_tencent);

        btnWeibo = (ImageButton) view1.findViewById(R.id.authentication_activity_auth_weibo);

        this.btnWeiXin.setOnClickListener(this);
        this.btnTecent.setOnClickListener(this);
        this.btnWeibo.setOnClickListener(this);

        this.btnFacebook = (ImageButton) view2.findViewById(R.id.authentication_activity_auth_facebook);

        this.btnTwitter = (ImageButton) view2.findViewById(R.id.authentication_activity_auth_twitter);

        this.btnGooglePlus = (ImageButton) view2.findViewById(R.id.authentication_activity_auth_googleplus);
        this.btnFacebook.setOnClickListener(this);
        this.btnTwitter.setOnClickListener(this);
        this.btnGooglePlus.setOnClickListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.authentication_activity_viewpager);
        AuthentucationPagerAdapter mPagerAdapter = new AuthentucationPagerAdapter();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bottomView1.setSelected(true);
                    bottomView2.setSelected(false);
                } else {
                    bottomView1.setSelected(false);
                    bottomView2.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(mPagerAdapter);
        facebookLogin = OnlineConfigAgent.getInstance().getConfigParams(this, UMENG_OPEN_FACEBOOK_LOGIN);
        if (facebookLogin.equals(UMENG_CLOSE)) {
            btnFacebook.setVisibility(View.GONE);
        }

        googlePlusLogin = OnlineConfigAgent.getInstance().getConfigParams(this, UMENG_OPEN_GOOGLEPLUS_LOGIN);
        if (googlePlusLogin.equals(UMENG_OPEN)) {
            btnGooglePlus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog = null;
        BlurUtil.blurBitmapFree(blurImage);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authentication_send_valid:
                sendSmsCode();
                break;
            case R.id.authentication_sign_up_switch:
                switchSignUpForm();
                break;
            case R.id.authentication_activity_form_switch:
                this.switchForm();
                break;
            case R.id.authentication_sign_in_fragment_button_sign_in:
                this.signIn();
                break;
            case R.id.authentication_sign_up_fragment_button_sign_up:
                this.signUpByEmail();
                break;
            case R.id.authentication_sign_up_fragment_button_sign_up1:
                this.signUpByPhone();
                break;
            case R.id.authentication_sign_in_fragment_forget_password:
            case R.id.authentication_sign_in_fragment_forget_password_fl:
                this.findPassword();
                break;
            case R.id.authentication_switch_to_sign_up_by_email:
                this.switchToSignUpFormByEmail();
                break;
            case R.id.authentication_switch_to_sign_up_by_phone:
                this.switchToSignUpFormByPhone();
                break;
            case R.id.authentication_activity_auth_tencent:
                this.signInByQQ();
                break;
            case R.id.authentication_activity_auth_weibo:
                this.signInByWeibo();
                break;
            case R.id.authentication_activity_auth_weixin:
                this.signInByWeiXin();
                break;
            case R.id.authentication_sign_up_fragment_zone:
                startActivityForResult(new Intent(this, CountryPageActivity.class), CountryPageActivity.REQ_COUNTRY);
                break;
            case R.id.authentication_activity_auth_facebook:
                signInByFaceBook();
                break;
            case R.id.authentication_activity_auth_twitter:
                signInByTwitter();
                break;
            case R.id.authentication_activity_auth_googleplus:
                Platform platform = ShareSDK.getPlatform(GooglePlus.NAME);

                if (platform != null && platform.isClientValid()) {
                    signInByGooglePlus();
                } else {
                    Toasts.show(this, R.string.google_plus_is_not_valid);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CountryPageActivity.REQ_COUNTRY:
                if (data != null) {
                    String country = data.getStringExtra(CountryPageActivity.EXTTA_COUNTRY_CODE);
                    if (!TextUtils.isEmpty(country)) {
                        tvZone.setText("+" + country);
                    }
                }
                break;
            case REQUEST_ADD_PROFILE_INFO:
                this.goHome();
                break;
            case REQUEST_FIND_PASSWORD:
                if (data != null) {
                    int type = data.getIntExtra(FindPassWordActivity.EXTRA_RESULT_REDIRECT,
                            FindPassWordActivity.EXTRA_FIND_PWD_RESULT_REGISTER_BY_PHONE);
                    final String areaCode = data.getStringExtra(FindPassWordActivity.EXTRA_RESULT_AREA_CODE);
                    final String phone = data.getStringExtra(FindPassWordActivity.EXTRA_RESULT_PHONE);
                    final String email = data.getStringExtra(FindPassWordActivity.EXTRA_RESULT_EMAIL);

                    switch (type) {
                        case FindPassWordActivity.EXTRA_FIND_PWD_RESULT_SIGN:
                            switchToSignInForm();
                            txtSignInEmail.setText(phone);
                            break;
                        case FindPassWordActivity.EXTRA_FIND_PWD_RESULT_REGISTER_BY_PHONE:
                            switchToSignUpFormByPhone();
                            etSignUpPhone.setText(phone);
                            tvZone.setText(areaCode);
                            break;
                        case FindPassWordActivity.EXTRA_FIND_PWD_RESULT_REGISTER_BY_EMAIL:
                            switchToSignUpFormByEmail();
                            txtSignUpEmail.setText(email);
                            break;
                    }

                }
                break;
        }

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

    /**
     * 登陆、注册相互切换
     */
    private void switchForm() {
        if (View.GONE == this.vgSignIn.getVisibility()
                && (View.VISIBLE == this.vgSignUpByEmail.getVisibility()
                || View.VISIBLE == this.vgSignUpByPhone.getVisibility()
                || View.VISIBLE == this.vgSignUpSwitch.getVisibility())) {
            this.switchToSignInForm();
        } else if ((View.GONE == this.vgSignUpByEmail.getVisibility()
                || View.GONE == this.vgSignUpByPhone.getVisibility()
                || View.GONE == this.vgSignUpSwitch.getVisibility())
                && View.VISIBLE == this.vgSignIn.getVisibility()) {
            this.switchToSwitchSignUp();
        }
    }

    /**
     * 手机注册、邮箱注册相互切换
     */
    private void switchSignUpForm() {
        if (View.VISIBLE == this.vgSignUpByEmail.getVisibility()) {
            this.switchToSignUpFormByPhone();
        } else {
            this.switchToSignUpFormByEmail();
        }
    }

    /**
     * 切换到邮箱注册
     */
    private void switchToSignUpFormByEmail() {

        animation1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                vgSignUpByEmail.setVisibility(View.VISIBLE);
                vgSignIn.setVisibility(View.GONE);
                vgSignUpByPhone.setVisibility(View.GONE);
                vgSignUpSwitch.setVisibility(View.GONE);
                fadeImage.setAlpha(0.5f);
                BlurUtil.blurOn(AuthenticationActivity.this, blurImage, R.drawable.authentication_bg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                vgSignIn.setVisibility(View.GONE);
                btnSignUpSwitch.setVisibility(View.VISIBLE);
                txtSignUpUsername.requestFocus();
                txtSignUpUsername.setFocusable(true);
                softInputTogglet();
            }

        });
        this.vgSignUpByEmail.startAnimation(animation1);
        this.btnSwitch
                .setText(R.string.authentication_sign_up_fragment_already_have_an_account);
        btnSignUpSwitch.setText(R.string.authentication_switch_to_phone);

    }

    /**
     * 切换到手机号码注册
     */
    private void switchToSignUpFormByPhone() {

        this.btnSwitch
                .setText(R.string.authentication_sign_up_fragment_already_have_an_account);
        btnSignUpSwitch.setText(R.string.authentication_switch_to_email);


        animation1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                vgSignIn.setVisibility(View.GONE);
                vgSignUpByEmail.setVisibility(View.GONE);
                vgSignUpSwitch.setVisibility(View.GONE);
                fadeImage.setAlpha(0.5f);
                BlurUtil.blurOn(AuthenticationActivity.this, blurImage, R.drawable.authentication_bg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vgSignUpByPhone.setVisibility(View.VISIBLE);
                btnSignUpSwitch.setVisibility(View.VISIBLE);
                txtSignUpUsernamePhone.requestFocus();
                txtSignUpUsernamePhone.setFocusable(true);
                softInputTogglet();
            }

        });
        this.vgSignUpByPhone.startAnimation(animation1);

    }

    /**
     * 切换到注册初始页
     */
    private void switchToSwitchSignUp() {
        this.vgSignIn.setVisibility(View.GONE);
        this.vgSignUpByEmail.setVisibility(View.GONE);
        this.vgSignUpByPhone.setVisibility(View.GONE);
        this.vgSignUpSwitch.setVisibility(View.VISIBLE);
        this.btnSignUpSwitch.setVisibility(View.GONE);
        this.btnSwitch
                .setText(R.string.authentication_sign_up_fragment_already_have_an_account);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSignUpValid.getWindowToken(), 0);
        }
        fadeImage.setAlpha(1.0f);
        BlurUtil.blurOff(blurImage, R.drawable.authentication_bg);
    }

    /**
     * 切换到登陆
     */
    private void switchToSignInForm() {
        this.vgSignUpByPhone.setVisibility(View.GONE);
        this.vgSignUpSwitch.setVisibility(View.GONE);
        this.vgSignUpByEmail.setVisibility(View.GONE);
        this.btnSignUpSwitch.setVisibility(View.GONE);

        animation1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                vgSignIn.setVisibility(View.VISIBLE);
                fadeImage.setAlpha(0.5f);
                txtSignInEmail.requestFocus();
                txtSignInEmail.setFocusable(true);
                BlurUtil.blurOn(AuthenticationActivity.this, blurImage, R.drawable.authentication_bg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vgSignUpByEmail.setVisibility(View.GONE);
                softInputTogglet();
            }

        });
        this.vgSignIn.startAnimation(animation1);
        this.btnSwitch
                .setText(R.string.authentication_sign_in_fragment_back_to_sign_up);

    }

    private void signIn() {
        final Context ctx = this;

        if (TextUtils.isEmpty(this.txtSignInEmail.getText())) {
            Toasts.show(ctx, R.string.authentication_email_is_required);
            this.txtSignInEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(this.txtSignInPassword.getText())) {
            Toasts.show(ctx, R.string.authentication_password_is_required);
            this.txtSignInPassword.requestFocus();
            return;
        }

        final LocalUser upo = new LocalUser();
        upo.setUsername(this.txtSignInEmail.getText().toString());
        upo.setPassword(this.txtSignInPassword.getText().toString());

        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_in_loading_msg), false);
        this.loadingDialog.show();

        boolean isEmail = upo.getUsername().contains("@");

        this.authManager.signIn(upo, this, isEmail ? AVUser.SIGN_TYPE_EMAIL : AVUser.SIGN_TYPE_PHONE);
    }

    private void signUpByEmail() {
        final Context ctx = this;

        if (TextUtils.isEmpty(this.txtSignUpUsername.getText().toString().trim())) {
            Toasts.show(ctx, R.string.authentication_username_is_required);
            this.txtSignUpUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(this.txtSignUpEmail.getText())) {
            Toasts.show(ctx, R.string.authentication_email_is_required);
            this.txtSignUpEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(this.txtSignUpPassword.getText())) {
            Toasts.show(ctx, R.string.authentication_password_is_required);
            this.txtSignUpPassword.requestFocus();
            return;
        }

        if (this.txtSignUpPassword.getText().length() < 6) {
            Toasts.show(ctx, R.string.authentication_sign_up_password_error);
            this.txtSignUpPassword.requestFocus();
            return;
        }

        final LocalUser upo = new LocalUser();
        upo.setUsername(this.txtSignUpEmail.getText().toString());
        upo.setEmail(this.txtSignUpEmail.getText().toString());
        upo.setPassword(this.txtSignUpPassword.getText().toString());
        upo.setNickname(this.txtSignUpUsername.getText().toString());

        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_up_loading_msg), false);
        this.loadingDialog.show();

        this.authManager.signUp(upo, null, new AuthenticationCallback() {
            @Override
            public void onResult(AuthenticationException e) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                if (e != null) {
                    switch (e.getErrorNumber()) {
                        case 0:
                            Toasts.show(ctx,
                                    R.string.authentication_username_is_use_err);
                            break;
                        case 1008:
                            if (!TextUtils.isEmpty(txtSignUpEmail.getText())) {
                                txtSignInEmail.setText(txtSignUpEmail.getText());
                                switchToSignInForm();
                                Toasts.show(AuthenticationActivity.this, e.getMessage());
                            }
                            break;
                        default:
                            Toasts.show(ctx, e.getMessage());
                            break;
                    }
                    return;
                }

                SpeedxAnalytics.onEvent(AuthenticationActivity.this, getString(R.string.authentication_event_sign_in), null);
                gotoUserSettingIfNeeded();
            }
        }, AVUser.SIGN_TYPE_EMAIL);
    }

    private void signUpByPhone() {

        final Context ctx = this;

        if (TextUtils.isEmpty(this.txtSignUpUsernamePhone.getText().toString().trim())) {
            Toasts.show(ctx, R.string.authentication_username_is_required);
            this.txtSignUpUsername.requestFocus();
            return;
        }

        String phoneNum = etSignUpPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            Toasts.show(ctx, R.string.activity_club_release_activities_activity_Phone_Number_Prompt);
            this.etSignUpPhone.requestFocus();
            return;
        }

        String vCode = this.etSignUpValid.getText().toString();
        if (TextUtils.isEmpty(vCode)) {
            Toasts.show(ctx, R.string.vcode_hint);
            this.etSignUpValid.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(this.txtSignUpPasswordPhone.getText())) {
            Toasts.show(ctx, R.string.authentication_password_is_required);
            this.txtSignUpPasswordPhone.requestFocus();
            return;
        }

        if (this.txtSignUpPasswordPhone.getText().length() < 6) {
            Toasts.show(ctx, R.string.authentication_sign_up_password_error);
            this.txtSignUpPasswordPhone.requestFocus();
            return;
        }

        final LocalUser upo = new LocalUser();
        upo.setUsername(tvZone.getText().toString() + phoneNum);
        upo.setPassword(this.txtSignUpPasswordPhone.getText().toString());
        upo.setNickname(this.txtSignUpUsernamePhone.getText().toString());

        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_up_loading_msg), false);
        this.loadingDialog.show();

        this.authManager.signUp(upo, vCode, new AuthenticationCallback() {
            @Override
            public void onResult(AuthenticationException e) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (e != null) {
                    switch (e.getErrorNumber()) {
                        case 0:
                            Toasts.show(ctx,
                                    R.string.authentication_username_is_use_err);
                            break;
                        case 1008:
                            if (!TextUtils.isEmpty(etSignUpPhone.getText())) {
                                txtSignInEmail.setText(etSignUpPhone.getText());
                                switchToSignInForm();
                                Toasts.show(AuthenticationActivity.this, e.getMessage());
                            }
                            break;
                        default:
                            Toasts.show(ctx, e.getMessage());
                            break;
                    }
                    return;
                }

                SpeedxAnalytics.onEvent(AuthenticationActivity.this, getString(R.string.authentication_event_sign_in), null);
                gotoUserSettingIfNeeded();
            }
        }, AVUser.SIGN_TYPE_PHONE);
    }


    private void signInByWeibo() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_up_loading_msg), false);
        AuthenticationFactory.authAndGetUserInfo(this, SinaWeibo.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
            @Override
            public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
                if (shareSDKUserInfoBean != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }
                    });

                    LocalUser lu = new LocalUser();
                    lu.setUsername(shareSDKUserInfoBean.getOpenId());
                    lu.setPassword(shareSDKUserInfoBean.getAccessToken());
                    lu.setNickname(shareSDKUserInfoBean.getNickname());
                    authManager.signIn(lu, AuthenticationActivity.this, AVUser.SIGN_TYPE_WEIBO);
                }
            }
        });

    }

    private void signInByQQ() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_in_loading_msg), false);
        AuthenticationFactory.authAndGetUserInfo(this, QQ.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
            @Override
            public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
                if (shareSDKUserInfoBean != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }
                    });

                    LocalUser lu = new LocalUser();
                    lu.setUsername(shareSDKUserInfoBean.getOpenId());
                    lu.setPassword(shareSDKUserInfoBean.getAccessToken());
                    lu.setNickname(shareSDKUserInfoBean.getNickname());
                    authManager.signIn(lu, AuthenticationActivity.this, AVUser.SIGN_TYPE_QQ);
                }
            }
        });

    }

    /**
     * 通过微信登录
     */
    private void signInByWeiXin() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_in_loading_msg), false);
        AuthenticationFactory.authAndGetUserInfo(this, Wechat.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
            @Override
            public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
                if (shareSDKUserInfoBean != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }
                    });

                    LocalUser lu = new LocalUser();
                    lu.setUsername(shareSDKUserInfoBean.getOpenId());
                    lu.setPassword(shareSDKUserInfoBean.getAccessToken());
                    lu.setNickname(shareSDKUserInfoBean.getNickname());
                    authManager.signIn(lu, AuthenticationActivity.this, AVUser.SIGN_TYPE_WECHAT);
                }
            }
        });

    }

    /**
     * 通过facebook
     */
    private void signInByFaceBook() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_in_loading_msg), false);
        AuthenticationFactory.authAndGetUserInfo(this, Facebook.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
            @Override
            public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
                if (shareSDKUserInfoBean != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }
                    });

                    LocalUser lu = new LocalUser();
                    lu.setUsername(shareSDKUserInfoBean.getOpenId());
                    lu.setPassword(shareSDKUserInfoBean.getAccessToken());
                    lu.setNickname(shareSDKUserInfoBean.getNickname());
                    authManager.signIn(lu, AuthenticationActivity.this, AVUser.SIGN_TYPE_FACEBOOK);
                }
            }
        });

    }


    /**
     * 通过twitter登录
     */
    private void signInByTwitter() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_in_loading_msg), false);
        AuthenticationFactory.authAndGetUserInfo(this, Twitter.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
            @Override
            public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
                if (shareSDKUserInfoBean != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }
                    });

                    LocalUser lu = new LocalUser();
                    lu.setUsername(shareSDKUserInfoBean.getOpenId());
                    lu.setPassword(shareSDKUserInfoBean.getAccessToken() + ";" + shareSDKUserInfoBean.getTokenSecret());
                    lu.setNickname(shareSDKUserInfoBean.getNickname());
                    authManager.signIn(lu, AuthenticationActivity.this, AVUser.SIGN_TYPE_TWITTER);
                }
            }
        });

    }

    /**
     * 通过Google plus
     */
    private void signInByGooglePlus() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.authentication_sign_in_loading_msg), false);
        AuthenticationFactory.authAndGetUserInfo(this, GooglePlus.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
            @Override
            public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
                if (shareSDKUserInfoBean != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }
                    });

                    LocalUser lu = new LocalUser();
                    lu.setUsername(shareSDKUserInfoBean.getOpenId());
                    lu.setPassword(shareSDKUserInfoBean.getAccessToken());
                    lu.setNickname(shareSDKUserInfoBean.getNickname());
                    authManager.signIn(lu, AuthenticationActivity.this, AVUser.SIGN_TYPE_GOOGLE_PLUS);
                }
            }
        });

    }

    /**
     * 获取验证码
     */
    private void sendSmsCode() {
        if (TextUtils.isEmpty(etSignUpPhone.getText().toString())) {
            Toasts.show(this, R.string.activity_club_release_activities_activity_Phone_Number_Prompt);
            return;
        }
        btnSendVerificationCode.setClickable(false);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                return new ClubManager(AuthenticationActivity.this).
                        sendSmscode(params[0], ClubManager.MSG_TYPE_BIND_PHONE);
            }

            @Override
            protected void onPostExecute(Integer integer) {

                if (integer != -1) {
                    startCount(integer);
                } else {
                    btnSendVerificationCode.setClickable(true);
//                    btnSendVerificationCode.setBackgroundResource(R.drawable.bg_verificationcodebtn);
                    btnSendVerificationCode.setText(getResources().getString(R.string.get_verification_code));
                }

            }
        }, tvZone.getText().toString() + etSignUpPhone.getText().toString());
    }

    /**
     * 找回密码
     */
    private void findPassword() {
        Intent intent = new Intent(this, FindPassWordActivity.class);
        intent.putExtra(FindPassWordActivity.EXTRA_ACCOUNT, txtSignInEmail.getText().toString());
        startActivityForResult(intent, REQUEST_FIND_PASSWORD);
    }

    private void startCount(int integer) {
        timer = new CountDownTimer(integer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTimeValue = (int) (millisUntilFinished / 1000);
                btnSendVerificationCode.setText(millisUntilFinished / 1000 + "s");

            }

            @Override
            public void onFinish() {
                btnSendVerificationCode.setClickable(true);
                btnSendVerificationCode.setText(getResources().getString(R.string.get_verification_code));
            }
        };
        timer.start();
    }

    @Override
    public void onResult(AuthenticationException e) {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();

        if (null != e) {
            switch (e.getErrorNumber()) {
                case 0:
                    Toasts.show(this, R.string.authentication_sign_in_err_unnetwork);
                    break;

                default:
                    Toasts.show(this, e.getMessage());
                    break;
            }
            return;
        }

        SpeedxAnalytics.onEvent(this, getString(R.string.authentication_event_sign_in), null);

        this.gotoUserSettingIfNeeded();
    }

    /**
     * 用户注册时,调到完善信息界面
     */
    private void gotoUserSettingIfNeeded() {
        final Context ctx = this;
        final AVUser usr = AVUser.getCurrentUser();
        if (null == usr) {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            return;
        }

        final String userId = usr.getObjectId();
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                final String userId = params[0];
                try {

                    return new UserManager(AuthenticationActivity.this).getProfileByUserId(userId);
                } catch (Exception e) {
                    Log.e(TAG, "Fetch user from server failed", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ProfileDTO pd) {
                if (null == pd || !pd.isEdited()) {
                    final Intent intent = new Intent(ctx,
                            UserSettingActivityFromAuth.class);
                    intent.putExtra(UserSettingActivity.EXTRA_FROM_AUTH, 2);
                    startActivityForResult(intent, REQUEST_ADD_PROFILE_INFO);
//                    finish();
                } else {
                    goHome();
                }
            }

        }, userId);
    }

    /**
     * intent to {@link HomeActivity}
     */
    protected void goHome() {
        String brand = Build.BRAND;
        boolean isSetting = this.sp.getBoolean(MainActivity.PREF_GUIDE_SETTING, false);
        if (brand.equalsIgnoreCase("Xiaomi") && !isSetting) {
            this.goMiuiSetting();
            return;
        }

        if (brand.equalsIgnoreCase("Meizu") && !isSetting) {
            this.goMeiZuSetting();
            return;
        }

        AVUser user = AVUser.getCurrentUser();
        if (null != user) {

            android.webkit.CookieManager manager = android.webkit.CookieManager.getInstance();
            manager.setCookie(UrlConfig.DEV_SPEEDX_HOST_DOMAIN, "sessionid=" + user.getSessionToken());
        }

        Intent homeIntent = new Intent(this, HomeActivity.class);
        Intent intent = getIntent();
        if (null != intent) {
            String pushData = intent
                    .getStringExtra(Constants.PUSH_START_ACTIVITY_DATA);
            if (!TextUtils.isEmpty(pushData)) {
                homeIntent.putExtra(Constants.PUSH_START_ACTIVITY_DATA,
                        pushData);
            }
            //rongcloud
            String rongPush = intent.getStringExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY);
            if (!TextUtils.isEmpty(rongPush)) {
                Log.e("rongPush", rongPush);
                homeIntent.putExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY,
                        rongPush);
            } else {
                Log.e("rongPush", "null");
            }
            if (null != getIntent()) {
                Uri schemaData = getIntent().getData();
                if (schemaData != null) {
                    homeIntent.setData(schemaData);
                }
            }
        }
        this.startActivity(homeIntent);
        this.finish();
    }

    /**
     * Jump miui6 follow setting
     */
    private void goMiuiSetting() {
        Intent miuiIntent = new Intent(this, MiuiSettingActivity.class);
        startActivity(miuiIntent);
//        startActivityForResult(miuiIntent, RC_GOTO_MIUI_SETTING_PAGE);
        this.sp.edit().putBoolean(MainActivity.PREF_GUIDE_SETTING, true).apply();
        this.finish();
    }

    /**
     * Jump MEIZU follow setting
     */
    private void goMeiZuSetting() {
        Intent meiZuIntent = new Intent(this, MeiZuSettingActivity.class);
//        startActivityForResult(meiZuIntent, RC_GOTO_MEIZU_SETTING_PAGE);
        this.startActivity(meiZuIntent);
        this.sp.edit().putBoolean(MainActivity.PREF_GUIDE_SETTING, true).apply();
        this.finish();
    }

    private void softInputTogglet() {

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.toggleSoftInputFromWindow(etSignUpValid.getWindowToken(),
                InputMethodManager.SHOW_IMPLICIT, InputMethodManager.RESULT_SHOWN);
    }

    private AnimationSet animationSetInit() {
        final Animation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        final AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        final AnimationSet as = new AnimationSet(this, null);
        as.addAnimation(translate);
        as.addAnimation(alpha);
        as.setDuration(400L);
        return as;
    }

    class AuthentucationPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
//            Log. i("INFO", "instantiate item:"+position);
            ((ViewPager) container).addView(mViewList.get(position), 0);
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

}
