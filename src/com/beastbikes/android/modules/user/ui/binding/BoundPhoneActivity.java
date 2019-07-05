package com.beastbikes.android.modules.user.ui.binding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.AccountBindManager;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.utils.RxBus;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.List;

@LayoutResource(R.layout.activity_bound_phone)
public class BoundPhoneActivity extends SessionFragmentActivity {
    @IdResource(R.id.activity_bound_phone)
    private EditText phone;

    @IdResource(R.id.activity_bound_phone_verificationcode)
    private EditText verificationcode;

    @IdResource(R.id.activity_bound_phone_area_code)
    private TextView area_code;

    @IdResource(R.id.activity_bound_phone_verificationcode_ok)
    private TextView ok;

    @IdResource(R.id.activity_bound_phone_verificationcode_tv)
    private TextView verificationcode_tv;

    @IdResource(R.id.activity_bound_phone_first)
    private EditText first;

    @IdResource(R.id.activity_bound_phone_second)
    private EditText second;

    @IdResource(R.id.activity_bound_phone_first_linear)
    private LinearLayout first_linear;

    @IdResource(R.id.activity_bound_phone_second_linear)
    private LinearLayout second_linear;

    private static final int REQUEST_COUNTRY = 0x123;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private String PREF_PHONE = "phone";

    private AccountBindManager accountBindManager;

    public static final String BINDING = "bindPhone";

    public static final String MOBILEPHONE = "mobilephone";
    public static final String EXTRA_ISSERPASSWARD = "isSetPassWard";

    private boolean isSetPassWard = true;
    private TimeCount time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.activity_bound_phone_title));
        }
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        accountBindManager = new AccountBindManager(this);
        initView();
    }

    private void initView() {
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null)
            return;
        if (!getIntent().getBooleanExtra(EXTRA_ISSERPASSWARD, true) ||
                avUser.getSignType() == AuthenticationFactory.TYPE_MOBILE_PHONE ||
                avUser.getSignType() == AuthenticationFactory.TYPE_EMAIL) {
            isSetPassWard = false;
        }
        if (isSetPassWard) {
            first_linear.setVisibility(View.VISIBLE);
            second_linear.setVisibility(View.VISIBLE);
        } else {
            first_linear.setVisibility(View.GONE);
            second_linear.setVisibility(View.GONE);
        }
        phone.setText(sharedPreferences.getString(PREF_PHONE, ""));
        area_code.setText("+" +  LocaleManager.getCountryCode(this));

    }

    public void finish() {
        if (time != null)
            time.cancel();
        super.finish();
        editor.putString(PREF_PHONE, phone.getText().toString());
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }


    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_bound_phone_area_code:
                startActivityForResult(new Intent(this, CountryPageActivity.class), REQUEST_COUNTRY);
                break;
            case R.id.activity_bound_phone_verificationcode_ok:
                if (issendVeriFicationcode()) {
                    if (isNetworkAvailable(this)) {
                        sendVeriFicationcode();
                    } else {
                        Toasts.showOnUiThread(this,
                                getString(R.string.account_failure));
                    }
                }
                break;
            case R.id.activity_bound_phone_verificationcode_commit: //绑定手机
                if (isCommit()) {
                    binding();
                }
                break;
        }

    }

    private void binding() {
        final String area_code_str = area_code.getText().toString();
        final String phone_str = phone.getText().toString();
        final String verificationcode_str = verificationcode.getText().toString();
        final int verificationcode = Integer.parseInt(verificationcode_str);
        final LoadingDialog dialog = new LoadingDialog(this,
                getString(R.string.loading_msg), false);
        dialog.show();
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<AccountDTO>>() {
            @Override
            protected List<AccountDTO> doInBackground(Void... params) {
                try {
                    if (isSetPassWard) {
                        return accountBindManager.bindUser(area_code_str.replace(" ", "") + phone_str, getPassWard()
                                , MOBILEPHONE, verificationcode, "");
                    } else {
                        return accountBindManager.bindUser(area_code_str.replace(" ", "") + phone_str, ""
                                , MOBILEPHONE, verificationcode, "");
                    }
                } catch (BusinessException e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<AccountDTO> accountDTOs) {
                super.onPostExecute(accountDTOs);
                if (dialog.isShowing())
                    dialog.dismiss();
                if (accountDTOs == null)
                    return;
                Intent intent = new Intent(BoundPhoneActivity.this, BandingSuccessActivity.class);
                intent.putExtra(BandingSuccessActivity.EXTRA_TYPE, BandingSuccessActivity.EXTRA_PHONE);
                intent.putExtra(BandingSuccessActivity.AUTHKEY, area_code_str.replace(" ", "") + phone_str);
                BoundPhoneActivity.this.startActivity(intent);
                RxBus.getDefault().post(new BandingSuccessActivity.Accounts(accountDTOs));
                finish();
            }
        });
    }

    private String getPassWard() {
        String firstStr = first.getText().toString();
        String secondStr = second.getText().toString();
        if ((firstStr.length() > 16 && firstStr.length() < 6) ||
                (secondStr.length() > 16 && secondStr.length() < 6) || TextUtils.isEmpty(firstStr) ||
                TextUtils.isEmpty(secondStr)) {
            Toasts.showOnUiThread(this,
                    getString(R.string.activity_bound_phone_password_illegal_length_str));
            return null;
        } else if (!firstStr.equals(secondStr)) {
            Toasts.showOnUiThread(this,
                    getString(R.string.activity_bound_phone_password_illegal_change_str));
            return null;
        }

        return firstStr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_COUNTRY:// 选择区号
                        area_code.setText("+" + data.getStringExtra(CountryPageActivity.EXTTA_COUNTRY_CODE));
                        break;
                }
                break;
        }
    }

    private boolean issendVeriFicationcode() {
        boolean isCommit = true;
        String phone_str = phone.getText().toString();
        if (TextUtils.isEmpty(phone_str)) {
            isCommit = false;
        }
        if (!isCommit) {
            Toasts.showOnUiThread(this, getString(R.string.activity_bound_phone_toast_str));
        }
        return isCommit;
    }

    //发送短信验证码
    private void sendVeriFicationcode() {
        final String area_code_str = area_code.getText().toString();
        final String phone_str = phone.getText().toString();
        final LoadingDialog dialog = new LoadingDialog(this,
                getString(R.string.loading_msg), false);
        dialog.setCancelable(true);
        time = new TimeCount(60000, 1000);
        getAsyncTaskQueue().add(new AsyncTask<Object, Object, Integer>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
            }

            @Override
            protected Integer doInBackground(Object... params) {
                try {
                    return accountBindManager.sendSmscode(area_code_str.replace(" ", "") + phone_str, BINDING);
                } catch (BusinessException e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (dialog.isShowing())
                    dialog.dismiss();
                if (integer == 60) {
                    time.start();
                }
            }
        });

    }

    private boolean isCommit() {
        boolean isCommit = true;
        String phone_str = phone.getText().toString();
        String verificationcode_str = verificationcode.getText().toString();
        if (TextUtils.isEmpty(phone_str)) {
            isCommit = false;
        }
        if (TextUtils.isEmpty(verificationcode_str)) {
            isCommit = false;
        }
        if (!isCommit) {
            Toasts.showOnUiThread(this, getString(R.string.activity_bound_phone_toast_str));
        } else if (isSetPassWard && getPassWard() == null) {
            isCommit = false;
        }

        return isCommit;
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            ok.setText(getString(R.string.authentication_send_valid));
            ok.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            ok.setClickable(false);
            ok.setText(millisUntilFinished / 1000 + "s");
        }
    }

    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
