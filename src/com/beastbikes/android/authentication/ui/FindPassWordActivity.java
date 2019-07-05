package com.beastbikes.android.authentication.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.biz.AuthenticationManager;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.ui.binding.CountryPageActivity;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Alias("找回密码")
@LayoutResource(R.layout.activity_find_pass_word)
public class FindPassWordActivity extends SessionFragmentActivity implements View.OnClickListener {
    private static final int REQUEST_COUNTRY = 0x123;
    private static final int REQUEST_FIND_PWD_SUCCESS = 0x124;
    public static final String EXTRA_RESULT_REDIRECT = "extra_result_redirect";
    public static final String EXTRA_RESULT_AREA_CODE = "extra_result_areacode";
    public static final String EXTRA_RESULT_EMAIL = "extra_result_email";
    public static final String EXTRA_RESULT_PHONE = "extra_result_phone";

    public static final int EXTRA_FIND_PWD_RESULT_SIGN = 0X224;
    public static final int EXTRA_FIND_PWD_RESULT_REGISTER_BY_PHONE = 0X225;
    public static final int EXTRA_FIND_PWD_RESULT_REGISTER_BY_EMAIL = 0X226;

    @IdResource(R.id.activity_find_pass_word_mail)
    private View mail;

    @IdResource(R.id.activity_find_pass_word_mail_tv)
    private TextView mail_tv;

    @IdResource(R.id.activity_find_pass_word_mail_line)
    private TextView mail_line;

    @IdResource(R.id.activity_find_pass_word_phone)
    private View phone;

    @IdResource(R.id.activity_find_pass_word_phone_tv)
    private TextView phone_tv;

    @IdResource(R.id.activity_find_pass_word_phone_line)
    private TextView phone_line;

    @IdResource(R.id.activity_find_pass_word_mail_layout)
    private View mail_layout;

    @IdResource(R.id.activity_find_pass_word_phone_layout)
    private View phone_layout;

    @IdResource(R.id.activity_find_pass_word_mail_edit)
    private EditText mail_edit;

    @IdResource(R.id.activity_find_pass_word_mail_commit)
    private TextView mail_commit;

    @IdResource(R.id.activity_find_pass_word_phone_areacode)
    private TextView areacode;

    @IdResource(R.id.activity_find_pass_word_phone_edit)
    private EditText phone_edit;

    @IdResource(R.id.activity_find_pass_word_code_edit)
    private EditText code_edit;

    @IdResource(R.id.activity_find_pass_word_password_edit)
    private EditText password_edit;

    @IdResource(R.id.activity_find_pass_word_commit)
    private TextView phone_commit;

    @IdResource(R.id.activity_find_pass_word_code)
    private TextView send_code;

    public static final String EXTRA_ACCOUNT = "account";


    private android.support.v7.app.ActionBar bar;

    private AuthenticationManager authManager;

    private String acount;
    private TimeCount time;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.mail.setOnClickListener(this);
        this.phone.setOnClickListener(this);
        this.mail_commit.setOnClickListener(this);
        this.send_code.setOnClickListener(this);
        this.phone_commit.setOnClickListener(this);
        this.areacode.setOnClickListener(this);
        authManager = new AuthenticationManager(this);
        acount = getIntent().getStringExtra(EXTRA_ACCOUNT);
        if (!isMobileNum(acount))
            changetomail();
        else
            changetophone();
        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getCountry();
        areacode.setText("+" +
                PhoneNumberUtil.getInstance().getCountryCodeForRegion(language));
    }

    public void finish() {
        if (time != null)
            time.cancel();
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void mail_commit() {
        final LoadingDialog loadingDialog;
        final String mail_str = mail_edit.getText().toString();
        if (TextUtils.isEmpty(mail_str))
            Toasts.showOnUiThread(this, getString(R.string.activity_find_pass_word_mail_empty));
        else {
            loadingDialog = new LoadingDialog(this, getString(R.string.loading_msg), false);
            loadingDialog.setCancelable(true);
            this.getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mail_commit.setClickable(false);
                    loadingDialog.show();
                }

                @Override
                protected JSONObject doInBackground(String... params) {
                    String email = mail_str;

                    return authManager.findPasswordByEmail(email);
                }

                @Override
                protected void onPostExecute(JSONObject result) {
                    mail_commit.setClickable(true);
                    loadingDialog.cancel();
                    if (null == result) {
                        Toasts.show(FindPassWordActivity.this,
                                R.string.authentication_email_not_registered);
                        return;
                    } else if (result.optInt("code") == 0) {
                        //重置成功的操作
                        Intent intent = new Intent(FindPassWordActivity.this,
                                FindPasswordSuccessActivity.class);
                        intent.putExtra(FindPasswordSuccessActivity.EXTRA_MAIL, mail_str);
                        startActivityForResult(intent, REQUEST_FIND_PWD_SUCCESS);
                    } else {
                        Toasts.show(
                                FindPassWordActivity.this,
                                result.optString("message"));
                    }
                }

            });
        }
    }

    private void changetomail() {
        mail_tv.setTextColor(getResources().getColor(R.color.account_management_color));
        mail_line.setVisibility(View.VISIBLE);
        phone_tv.setTextColor(getResources().
                getColor(R.color.text_default));
        phone_line.setVisibility(View.GONE);
        mail_layout.setVisibility(View.VISIBLE);
        phone_layout.setVisibility(View.GONE);
        if (!isMobileNum(acount)) {
            mail_edit.setText(acount);
        }
    }

    private void changetophone() {
        mail_tv.setTextColor(getResources().getColor(R.color.text_default));
        mail_line.setVisibility(View.GONE);
        phone_tv.setTextColor(getResources().getColor(R.color.account_management_color));
        phone_line.setVisibility(View.VISIBLE);
        mail_layout.setVisibility(View.GONE);
        phone_layout.setVisibility(View.VISIBLE);
        if (isMobileNum(acount)) {
            phone_edit.setText(acount);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //点击mail tab
            case R.id.activity_find_pass_word_mail:
                changetomail();
                break;

            //点击phone tab
            case R.id.activity_find_pass_word_phone:
                changetophone();
                break;

            //邮箱找回，确认
            case R.id.activity_find_pass_word_mail_commit:
                mail_commit();
                break;

            case R.id.activity_find_pass_word_phone_areacode:
                startActivityForResult(new Intent(this, CountryPageActivity.class), REQUEST_COUNTRY);
                break;

            case R.id.activity_find_pass_word_code:
                sendSmscode();
                break;
            case R.id.activity_find_pass_word_commit:
                phoneCommit();
                break;
        }
    }

    private void sendSmscode() {
        final LoadingDialog loadingDialog;
        final String areaCodeValue = areacode.getText().toString();
        final String phoneValue = phone_edit.getText().toString();
        if (TextUtils.isEmpty(phoneValue))
            Toasts.showOnUiThread(this, getString(R.string.activity_find_pass_word_phone_empty));
        else {
            loadingDialog = new LoadingDialog(this, getString(R.string.loading_msg), false);
            loadingDialog.setCancelable(true);
            this.getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loadingDialog.show();
                }

                @Override
                protected JSONObject doInBackground(String... params) {
                    String phone = areaCodeValue + phoneValue;
                    return authManager.sendSmscode(phone, "resetPwd");
                }

                @Override
                protected void onPostExecute(JSONObject result) {
                    loadingDialog.cancel();
                    if (null == result) {
                        return;
                    } else if (result.optInt("code") == 1007) {
                        final MaterialDialog dialog = new MaterialDialog(FindPassWordActivity.this);
                        dialog.setMessage(R.string.activity_find_pass_not_account_ms);
                        dialog.setPositiveButton(R.string.activity_find_pass_ok_ms, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = getIntent();
                                intent.putExtra(EXTRA_RESULT_REDIRECT, EXTRA_FIND_PWD_RESULT_REGISTER_BY_PHONE);
                                intent.putExtra(EXTRA_RESULT_AREA_CODE, areaCodeValue);
                                intent.putExtra(EXTRA_RESULT_PHONE, phoneValue);
                                setResult(RESULT_OK, intent);
                                dialog.dismiss();
                                finish();
                            }
                        });
                        dialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    } else if (result.optInt("code") == 0) {
                        time = new TimeCount(60000, 1000);
                        time.start();
                    } else {
                        Toasts.show(
                                FindPassWordActivity.this,
                                result.optString("message"));
                    }
                }

            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_COUNTRY:// 选择区号
                        areacode.setText("+" + data.getStringExtra(CountryPageActivity.EXTTA_COUNTRY_CODE));
                        break;
                    case REQUEST_FIND_PWD_SUCCESS:
                        String email = data.getStringExtra(EXTRA_RESULT_EMAIL);
                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_RESULT_EMAIL, email);
                        intent.putExtra(EXTRA_RESULT_REDIRECT, EXTRA_FIND_PWD_RESULT_REGISTER_BY_EMAIL);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }
                break;

        }
    }

    private void phoneCommit() {
        final LoadingDialog loadingDialog;
        final String areaCodeValue = areacode.getText().toString();
        final String phoneValue = phone_edit.getText().toString();
        final String codeEditValue = code_edit.getText().toString();
        final String password_edit_str = password_edit.getText().toString();
        if (TextUtils.isEmpty(phoneValue))
            Toasts.showOnUiThread(this, getString(R.string.activity_find_pass_word_phone_empty));
        else if (TextUtils.isEmpty(codeEditValue))
            Toasts.showOnUiThread(this, getString(R.string.activity_find_pass_word_cod_empty));
        else if (TextUtils.isEmpty(password_edit_str))
            Toasts.showOnUiThread(this, getString(R.string.activity_find_pass_password_cod_empty));
        else if (password_edit_str.length() < 6 || password_edit_str.length() > 16)
            Toasts.showOnUiThread(this, getString(R.string.activity_find_pass_password_cod_error));
        else {
            loadingDialog = new LoadingDialog(this, getString(R.string.loading_msg), false);
            loadingDialog.setCancelable(true);
            this.getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    phone_commit.setClickable(false);
                    loadingDialog.show();
                }

                @Override
                protected JSONObject doInBackground(String... params) {
                    return authManager.resetPasswordByMobile(areaCodeValue + phoneValue,
                            codeEditValue
                            , password_edit_str);
                }

                @Override
                protected void onPostExecute(JSONObject result) {
                    phone_commit.setClickable(true);
                    loadingDialog.cancel();
                    if (null == result) {
                        return;
                    } else if (result.optInt("code") == 0) {
                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_RESULT_REDIRECT, EXTRA_FIND_PWD_RESULT_REGISTER_BY_PHONE);
                        intent.putExtra(EXTRA_RESULT_PHONE, phoneValue);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toasts.show(
                                FindPassWordActivity.this,
                                result.optString("message"));
                    }
                }

            });

        }

    }

    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("[0-9]+");
        Matcher m = p.matcher(mobiles);
        return m.matches();

    }

    //    //判断是否为邮箱
//    public boolean isEmail(String strEmail) {
//        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
//        Pattern p = Pattern.compile(strPattern);
//        Matcher m = p.matcher(strEmail);
//        return m.matches();
//    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            send_code.setText(getString(R.string.activity_find_pass_word_phone_sendcode_str));
            send_code.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            send_code.setClickable(false);
            send_code.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
