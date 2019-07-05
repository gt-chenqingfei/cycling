package com.beastbikes.android.modules.user.ui.binding;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationBean;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.AccountBindManager;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.modules.user.ui.adapter.AccountManagementAdapter;
import com.beastbikes.android.utils.RxBus;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

@LayoutResource(R.layout.activity_account_management)
public class AccountManagementActivity extends SessionFragmentActivity implements
        AccountManagementAdapter.AccountBindGetDataListener, Constants {

    @IdResource(R.id.activity_account_management_account_icon)
    private ImageView icon;

    @IdResource(R.id.activity_account_management_account_name)
    private TextView account_name;

    @IdResource(R.id.activity_account_management_account_tv)
    private TextView account_tv;

    @IdResource(R.id.activity_account_management_lv)
    private ListView lv;

    private List<AccountDTO> accountDTOs = new ArrayList<>();

    private AccountManagementAdapter adapter;

    private AccountBindManager accountBindManager;

    private String facebookLogin = "0";

    private String googlePlusLogin = "0";

    private CompositeSubscription subscription;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.activity_account_management_title));
        }
        accountBindManager = new AccountBindManager(this);
        initView();
        facebookLogin = OnlineConfigAgent.getInstance().getConfigParams(this, UMENG_OPEN_FACEBOOK_LOGIN);
        googlePlusLogin = OnlineConfigAgent.getInstance().getConfigParams(this, UMENG_OPEN_GOOGLEPLUS_LOGIN);
        initAccountList();
        getLocalAccountList();
        getAccountDTOs();

        this.subscription = new CompositeSubscription();
        this.subscription.add(RxBus.getDefault().toObserverable(BandingSuccessActivity.Accounts.class).
                subscribe(new Action1<BandingSuccessActivity.Accounts>() {
                    @Override
                    public void call(BandingSuccessActivity.Accounts accounts) {
                        List<AccountDTO> list = accounts.getList();
                        refreshUI(list);
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.clear();
    }

    private void getLocalAccountList() {
        refreshUI(accountBindManager.getLocalbindStatus());
    }

    private void getAccountDTOs() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<AccountDTO>>() {
            @Override
            protected List<AccountDTO> doInBackground(Void... params) {
                return accountBindManager.bindStatus();
            }

            @Override
            protected void onPostExecute(List<AccountDTO> accountdtos) {
                refreshUI(accountdtos);
            }
        });
    }

    private void initView() {
        adapter = new AccountManagementAdapter(accountDTOs, this, this);
        lv.setAdapter(adapter);
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void refreshAccountBindDate(AuthenticationBean shareSDKUserInfoBean) {
        bindUser(shareSDKUserInfoBean);
    }

    @Override
    public void refreshAccountUnBindDate(AccountDTO accountDTO) {
        unBindUser(accountDTO);
    }

    private void unBindUser(final AccountDTO accountDTO) {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<AccountDTO>>() {

            @Override
            protected List<AccountDTO> doInBackground(Void... voids) {
                try {
                    return accountBindManager.unbindUser(accountDTO.getAuthKey(), accountDTO.getAuthType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<AccountDTO> accountDTOs) {
                if (accountDTOs == null || accountDTOs.size() == 0)
                    return;
                refreshUI(accountDTOs);
            }
        });
    }

    //设置每个item的图标和名称
    private void changeNameAndIcon(AccountDTO accountDTO) {
        switch (accountDTO.getAuthType()) {
            case AuthenticationFactory.TYPE_WEIBO:
                icon.setImageResource(R.drawable.activity_account_management_wieibo_icon_band);
                account_name.setText(getString(R.string.activity_account_management_weibo_str));
                break;
            case AuthenticationFactory.TYPE_EMAIL:
                icon.setImageResource(R.drawable.activity_account_management_mail_icon_banding);
                account_name.setText(getString(R.string.activity_account_management_mail_str));
                break;
            case AuthenticationFactory.TYPE_MOBILE_PHONE:
                icon.setImageResource(R.drawable.activity_account_management_mobile_icon_banding);
                account_name.setText(getString(R.string.activity_account_management_phone_str));
                break;
            case AuthenticationFactory.TYPE_QQ:
                icon.setImageResource(R.drawable.activity_account_management_qq_icon_banding);
                account_name.setText(getString(R.string.activity_account_management_qq_str));
                break;
            case AuthenticationFactory.TYPE_WEIXIN:
                icon.setImageResource(R.drawable.activity_account_management_wechat_icon_banding);
                account_name.setText(getString(R.string.activity_account_management_weixin));
                break;
            case AuthenticationFactory.TYPE_FACEBOOK:
                icon.setImageResource(R.drawable.activity_account_management_facebook_icon_band);
                account_name.setText(getString(R.string.activity_account_management_facebook_str));
                break;
            case AuthenticationFactory.TYPE_GOOGLE_PLUS:
                icon.setImageResource(R.drawable.activity_account_management_googleplus_icon_band);
                account_name.setText(getString(R.string.activity_account_management_googleplus_str));
                break;
            case AuthenticationFactory.TYPE_TWITTER:
                icon.setImageResource(R.drawable.activity_account_management_twitter_icon_band);
                account_name.setText(getString(R.string.activity_account_management_twitter_str));
                break;
        }
    }

    private AccountDTO getCurrentAccount(List<AccountDTO> accountDTOs) {
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null)
            return null;
        for (AccountDTO accountDTO : accountDTOs) {
            if (accountDTO.getAuthType() == avUser.getSignType()) {
                return accountDTO;
            }
        }
        return null;
    }

    private boolean isMail() {
        for (AccountDTO accountDTO : accountDTOs) {
            if (accountDTO.getAuthType() == AuthenticationFactory.TYPE_EMAIL) {
                return accountDTO.getStatus() == AccountDTO.STATUS_BOND;
            }
        }
        return false;
    }

    private void bindUser(final AuthenticationBean shareSDKUserInfoBean) {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<AccountDTO>>() {

            @Override
            protected List<AccountDTO> doInBackground(Void... voids) {
                try {
                    switch (shareSDKUserInfoBean.getType()) {
                        case AuthenticationFactory.SDK_WEIBO:
                        case AuthenticationFactory.SDK_QQ:
                        case AuthenticationFactory.SDK_WECHAT:
                        case AuthenticationFactory.SDK_GOOGLE_PLUS:
                        case AuthenticationFactory.SDK_FACEBOOK:
                            return accountBindManager.bindUser(shareSDKUserInfoBean.getOpenId(),
                                    shareSDKUserInfoBean.getAccessToken(), shareSDKUserInfoBean.getType(),
                                    0, shareSDKUserInfoBean.getNickname());
                        case AuthenticationFactory.SDK_TWITTER:
                            return accountBindManager.bindUser(shareSDKUserInfoBean.getOpenId(),
                                    shareSDKUserInfoBean.getAccessToken() + ";" + shareSDKUserInfoBean.getTokenSecret(),
                                    shareSDKUserInfoBean.getType(), 0, shareSDKUserInfoBean.getNickname());
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<AccountDTO> accountDTOs) {
                if (accountDTOs == null || accountDTOs.size() == 0)
                    return;
                refreshUI(accountDTOs);
            }
        });
    }

    private void refreshUI(List<AccountDTO> accountdtos) {
        if (accountdtos == null || accountdtos.size() == 0)
            return;
        AccountDTO currentAccount = getCurrentAccount(accountdtos);
        if (currentAccount != null) {
            accountdtos.remove(currentAccount);
            account_name.setText(currentAccount.getAccountName());
            account_tv.setText(currentAccount.getThirdNick());
            changeNameAndIcon(currentAccount);
        }
        for (int i = 0; i < accountdtos.size(); i++) {
            AccountDTO accountDTONew = accountdtos.get(i);
            for (int j = 0; j < accountDTOs.size(); j++) {
                AccountDTO accountDTOOld = accountDTOs.get(j);
                if (accountDTOOld.getAuthType() == accountDTONew.getAuthType()) {
                    accountDTOOld.setStatus(accountDTONew.getStatus());
                    accountDTOOld.setAccessToken(accountDTONew.getAccessToken());
                    accountDTOOld.setThirdNick(accountDTONew.getThirdNick());
                    accountDTOOld.setAuthKey(accountDTONew.getAuthKey());
                }
                accountDTOs.set(j, accountDTOOld);
            }
        }
        AccountDTO dto = null;
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null)
            return;
        for (AccountDTO accountDTO : accountDTOs) {
            if (accountDTO.getAuthType() == avUser.getSignType())
                dto = accountDTO;
        }
        if (dto != null) {
            accountDTOs.remove(dto);
        }

        if (!isMail()) {

            AccountDTO mail = null;
            for (AccountDTO accountDTO : accountDTOs) {
                if (accountDTO.getAuthType() == AuthenticationFactory.TYPE_EMAIL)
                    mail = accountDTO;
            }
            if (dto != null) {
                accountDTOs.remove(mail);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initAccountList() {
        accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_weibo_str), R.drawable.activity_account_management_wieibo_icon_band, AuthenticationFactory.TYPE_WEIBO));
        accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_phone_str), R.drawable.activity_account_management_mobile_icon_banding, AuthenticationFactory.TYPE_MOBILE_PHONE));
        accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_qq_str), R.drawable.activity_account_management_qq_icon_banding, AuthenticationFactory.TYPE_QQ));
        accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_wechat_str), R.drawable.activity_account_management_wechat_icon_banding, AuthenticationFactory.TYPE_WEIXIN));
        if (!TextUtils.isEmpty(facebookLogin) && facebookLogin.equals(UMENG_OPEN))
            accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_facebook_str), R.drawable.activity_account_management_facebook_icon_band, AuthenticationFactory.TYPE_FACEBOOK));
        if (!TextUtils.isEmpty(googlePlusLogin) && googlePlusLogin.equals(UMENG_OPEN))
            accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_googleplus_str), R.drawable.activity_account_management_googleplus_icon_band, AuthenticationFactory.TYPE_GOOGLE_PLUS));
        accountDTOs.add(new AccountDTO(getString(R.string.activity_account_management_twitter_str), R.drawable.activity_account_management_twitter_icon_band, AuthenticationFactory.TYPE_TWITTER));
    }

}
