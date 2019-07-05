package com.beastbikes.android.authentication.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.biz.AuthenticationManager;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONObject;

@Alias("邮箱找回成功")
@MenuResource(R.menu.find_password_success)
@LayoutResource(R.layout.activity_find_password_success)
public class FindPasswordSuccessActivity extends SessionFragmentActivity {
    @IdResource(R.id.activity_find_password_success_mail)
    private TextView mail;

    @IdResource(R.id.activity_find_password_success_commit)
    private TextView commit;

    private AuthenticationManager authManager;

    public static String EXTRA_MAIL = "mail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        authManager = new AuthenticationManager(this);
        mail.setText(getIntent().getStringExtra(EXTRA_MAIL));
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit.setClickable(false);
                if (TextUtils.isEmpty(mail.getText())) {
                    Toasts.show(FindPasswordSuccessActivity.this, R.string.authentication_email_is_required);
                    commit.setClickable(true);
                    return;
                }
                final String mail_str = mail.getText().toString();
                final LoadingDialog loadingDialog = new LoadingDialog(FindPasswordSuccessActivity.this
                        , getString(R.string.loading_msg), false);
                loadingDialog.setCancelable(true);
                getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog.show();
                    }

                    @Override
                    protected JSONObject doInBackground(String... params) {
                        String email = mail_str;
                        return authManager.findPasswordByEmail(email);
                    }

                    @Override
                    protected void onPostExecute(JSONObject result) {
                        commit.setClickable(true);
                        loadingDialog.cancel();
                        if (null == result) {
                            Toasts.show(FindPasswordSuccessActivity.this,
                                    R.string.authentication_email_not_registered);
                            return;
                        } else if (result.optInt("code") == 0) {
                            Toasts.show(
                                    FindPasswordSuccessActivity.this,
                                    R.string.authentication_sent_password_reset_request_success);
                        } else {
                            Toasts.show(
                                    FindPasswordSuccessActivity.this,
                                    result.optString("message"));
                        }
                    }

                });
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.find_password_success_ok) {

            Intent intent = getIntent();
            intent.putExtra(FindPassWordActivity.EXTRA_RESULT_REDIRECT,
                    FindPassWordActivity.EXTRA_FIND_PWD_RESULT_REGISTER_BY_EMAIL);
            intent.putExtra(FindPassWordActivity.EXTRA_RESULT_EMAIL, mail.getText().toString());
            setResult(RESULT_OK,intent);

            finish();
        }else if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }
}
