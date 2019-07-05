package com.beastbikes.android.modules.preferences.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.preferences.ui.dialog.StravaUnBindDialog;
import com.beastbikes.android.modules.strava.biz.StravaManager;
import com.beastbikes.android.modules.strava.ui.StravaAuthWebActivity;
import com.beastbikes.android.modules.user.biz.AccountBindManager;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.utils.SpUtil.UserSP;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.ta.utdid2.android.utils.NetworkUtils;

import java.util.List;

/**
 * Created by chenqingfei on 16/9/27.
 */
@LayoutResource(R.layout.service_manager_activity)
public class ServiceManagerActivity extends SessionFragmentActivity implements View.OnClickListener {
    private static final int REQ_CODE_AUTH = 0X0001;
    @IdResource(R.id.service_manager_activity_contact)
    private TextView mContact;
    private LoadingDialog loadingDialog;
    private AccountBindManager accountBindManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        mContact.setOnClickListener(this);
        accountBindManager = new AccountBindManager(this);
        String token = UserSP.getInstance().getString(this, Constants.PREF_STRAVA_TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            getAccountStatus();
        }
        refreshUI(token);
    }

    @Override
    public void onClick(View v) {
        if (v == mContact) {

            if (!NetworkUtils.isConnected(this))
                return;

            if (v.getTag() == null) {
                final StringBuilder sb = new StringBuilder(
                        "https://www.strava.com/oauth/authorize?client_id="
                                + StravaManager.CLIENT_ID + "&response_type=code&redirect_uri=speedx://strava_callback_url_for_speedx&scope=write&state=mystate&approval_prompt=force");
                final Uri browserUri = Uri.parse(sb.toString());
                final Intent browserIntent = new Intent(this, StravaAuthWebActivity.class);
                browserIntent.setData(browserUri);
                browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                browserIntent.setPackage(getPackageName());
                this.startActivityForResult(browserIntent, REQ_CODE_AUTH);
            } else {
                new StravaUnBindDialog(this, new StravaUnBindDialog.OnClickListener() {

                    @Override
                    public void onOkClick() {
                        unBind((String) mContact.getTag());
                    }
                }).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == REQ_CODE_AUTH) {
            String errorMsg = "";
            String token = data.getStringExtra(StravaAuthWebActivity.EXTRA_TOKEN);
            if (data != null) {
                errorMsg = data.getStringExtra(StravaAuthWebActivity.EXTRA_ERROR_MSG);
            }
            if (resultCode == RESULT_OK) {
                bind(token);
            } else {

                Toasts.show(this, errorMsg);
            }
        }
    }

    private void refreshUI(String token) {
        mContact.setTag(token);
        if (TextUtils.isEmpty(token)) {
            mContact.setText(R.string.service_manager_contact);
        } else {
            mContact.setText(R.string.service_manager_discontact);
        }
    }

    public void bind(final String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }

        getAsyncTaskQueue().add(new AsyncTask<Void, Void, AccountDTO>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(ServiceManagerActivity.this, "", true);
                }
                loadingDialog.show();
            }

            @Override
            protected AccountDTO doInBackground(Void... voids) {

                List<AccountDTO> accounts = null;
                try {
                    accounts = accountBindManager.bindUser(null, token,
                            AuthenticationFactory.SDK_STRAVA, 0, null);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

                return parseBindState(accounts);
            }

            @Override
            protected void onPostExecute(AccountDTO object) {
                super.onPostExecute(object);
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                if (object == null) {
                    return;
                }

                if (object.getStatus() == AccountDTO.STATUS_BOND &&
                        !TextUtils.isEmpty(object.getAccessToken())) {

                    UserSP.getInstance().put(ServiceManagerActivity.this, Constants.PREF_STRAVA_TOKEN,
                            object.getAccessToken()).commit();
                    refreshUI(object.getAccessToken());
                }
            }
        });
    }

    public void unBind(final String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, AccountDTO>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(ServiceManagerActivity.this, "", true);
                }
                loadingDialog.show();
            }

            @Override
            protected AccountDTO doInBackground(Void... voids) {
                List<AccountDTO> accounts = null;
                try {
                    accounts = accountBindManager.unbindUser(null, token, AuthenticationFactory.TYPE_STRAVA);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

                return parseBindState(accounts);
            }

            @Override
            protected void onPostExecute(AccountDTO strava) {
                super.onPostExecute(strava);


                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                UserSP.getInstance().remove(ServiceManagerActivity.this,
                        Constants.PREF_STRAVA_TOKEN).commit();
                refreshUI(null);

            }
        });
    }

    private void getAccountStatus() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, AccountDTO>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(ServiceManagerActivity.this, "", true);
                }
                loadingDialog.show();
            }

            @Override
            protected AccountDTO doInBackground(Void... params) {
                List<AccountDTO> accounts = accountBindManager.bindStatus();

                return parseBindState(accounts);
            }

            @Override
            protected void onPostExecute(AccountDTO strava) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (strava == null)
                    return;
                if (strava.getStatus() == AccountDTO.STATUS_BOND) {
                    if (!TextUtils.isEmpty(strava.getAccessToken())) {
                        UserSP.getInstance().put(ServiceManagerActivity.this,
                                Constants.PREF_STRAVA_TOKEN, strava.getAccessToken()).commit();
                    }
                    refreshUI(strava.getAccessToken());
                }

            }
        });
    }

    private AccountDTO parseBindState(List<AccountDTO> accounts) {
        if (accounts == null)
            return null;
        for (AccountDTO account : accounts) {
            if (account.getAuthType() == AuthenticationFactory.TYPE_STRAVA) {
                return account;
            }
        }
        return null;
    }
}
