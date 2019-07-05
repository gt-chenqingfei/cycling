package com.beastbikes.android.modules.user.biz;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.modules.user.dao.entity.LocalAccounts;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.persistence.BeastPersistenceManager;
import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.type;

/**
 * Created by zhangyao on 2016/1/26.
 */
public class AccountBindManager extends AbstractBusinessObject implements Constants {
    private AccountBindStub accountBindStub;
    private Activity activity;
    private final ORMLiteAccessObject<LocalAccounts> accountDao;

    public AccountBindManager(Activity context) {
        super((BusinessContext) context.getApplicationContext());
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.activity = context;
        this.accountBindStub = factory.create(AccountBindStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
        final BeastBikes app = (BeastBikes) context.getApplicationContext();
        final BeastPersistenceManager bpm = app.getPersistenceManager();
        this.accountDao = bpm.getDataAccessObject(LocalAccounts.class);
    }

    public List<AccountDTO> getLocalbindStatus() {
        AVUser user = AVUser.getCurrentUser();
        if (user == null)
            return null;
        List<AccountDTO> accounts = new ArrayList<>();
        try {
            List<LocalAccounts> list = accountDao.query("WHERE " + BeastStore.Accounts.AccountColumns.USER_ID + "=?", user.getObjectId());
            if (list == null || list.size() == 0)
                return null;

            for (int i = 0; i < list.size(); i++) {
                AccountDTO accountDTO = new AccountDTO(list.get(i));
                accounts.add(accountDTO);
            }
            return accounts;
        } catch (PersistenceException e) {
            e.printStackTrace();
            Log.e("PersistenceException", "PersistenceException");
        }
        return null;
    }

    public List<AccountDTO> bindStatus() {
        try {
            final JSONObject obj = this.accountBindStub.bindStatus();
            if (obj == null) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                JSONArray result = obj.optJSONArray("result");
                List<AccountDTO> accounts = new ArrayList<>();
                for (int i = 0; i < result.length(); i++) {
                    AccountDTO accountDTO = new AccountDTO(result.optJSONObject(i));
                    accounts.add(accountDTO);
                    LocalAccounts localAccounts = new LocalAccounts(accountDTO);
                    accountDao.createOrUpdate(localAccounts);
                }
                return accounts;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
        }
        return null;
    }

    public List<AccountDTO> bindUser(String authKey, String authToken, String type, int vcode, String nickName)
            throws BusinessException {
        try {
            int authType = 0;
            switch (type) {
                case "mobilephone":
                    authType = 2;
                    break;
                case AuthenticationFactory.SDK_WEIBO:
                    authType = AuthenticationFactory.TYPE_WEIBO;
                    break;
                case AuthenticationFactory.SDK_QQ:
                    authType = AuthenticationFactory.TYPE_QQ;
                    break;
                case AuthenticationFactory.SDK_WECHAT:
                    authType = AuthenticationFactory.TYPE_WEIXIN;
                    break;
                case AuthenticationFactory.SDK_FACEBOOK:
                    authType = AuthenticationFactory.TYPE_FACEBOOK;
                    break;
                case AuthenticationFactory.SDK_TWITTER:
                    authType = AuthenticationFactory.TYPE_TWITTER;
                    break;
                case AuthenticationFactory.SDK_GOOGLE_PLUS:
                    authType = AuthenticationFactory.TYPE_GOOGLE_PLUS;
                    break;
                case AuthenticationFactory.SDK_STRAVA:
                    authType = AuthenticationFactory.TYPE_STRAVA;
                    break;
                default:
                    break;
            }
            final JSONObject resultJson = this.accountBindStub.bindUser(authKey, authToken, authType, vcode, nickName);
            if (null == resultJson) {
                AuthenticationFactory.removeAccountFromType(activity, authType);
                return null;
            }
            int code = resultJson.optInt("code");
            if (code == 0) {
                JSONArray result = resultJson.optJSONArray("result");
                List<AccountDTO> accounts = new ArrayList<>();
                for (int i = 0; i < result.length(); i++) {
                    AccountDTO accountDTO = new AccountDTO(result.optJSONObject(i));
                    accounts.add(accountDTO);
                    LocalAccounts localAccounts = new LocalAccounts(accountDTO);
                    accountDao.createOrUpdate(localAccounts);
                }
                return accounts;
            }
            AuthenticationFactory.removeAccountFromType(activity, authType);

            String message = resultJson.optString("message");
            if (!TextUtils.isEmpty("message")) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
            throw new BusinessException(e);
        }
        return null;
    }

    public List<AccountDTO> unbindUser(String authKey, int type)
            throws BusinessException {
        return unbindUser(authKey, authKey, type);
    }

    public List<AccountDTO> unbindUser(String authKey, String authToken, int type)
            throws BusinessException {
        try {

            final JSONObject resultJson = this.accountBindStub.unbindUser(authKey, authToken, type);
            if (null == resultJson)
                return null;
            int code = resultJson.optInt("code");
            if (code == 0) {
                AuthenticationFactory.removeAccountFromType(activity, type);
                JSONArray result = resultJson.optJSONArray("result");
                List<AccountDTO> accounts = new ArrayList<>();
                for (int i = 0; i < result.length(); i++) {
//                    accounts.add(new AccountDTO(result.optJSONObject(i)));
                    AccountDTO accountDTO = new AccountDTO(result.optJSONObject(i));
                    accounts.add(accountDTO);
                    LocalAccounts localAccounts = new LocalAccounts(accountDTO);
                    accountDao.createOrUpdate(localAccounts);
                }
                return accounts;
            }
            String message = resultJson.optString("message");
            if (!TextUtils.isEmpty("message")) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
            throw new BusinessException(e);
        }
        return null;
    }

    public List<AccountDTO> bindResetPassword(String authKey, String authToken, int type)
            throws BusinessException {
        try {
            final JSONObject resultJson = this.accountBindStub.bindResetPassword(authKey, authToken, type);
            if (null == resultJson)
                return null;
            int code = resultJson.optInt("code");
            if (code == 0) {
                JSONArray result = resultJson.optJSONArray("result");
                List<AccountDTO> accounts = new ArrayList<>();
                for (int i = 0; i < result.length(); i++) {
//                    accounts.add(new AccountDTO(result.optJSONObject(i)));
                    AccountDTO accountDTO = new AccountDTO(result.optJSONObject(i));
                    accounts.add(accountDTO);
                    LocalAccounts localAccounts = new LocalAccounts(accountDTO);
                    accountDao.createOrUpdate(localAccounts);
                }
                return accounts;
            }
            String message = resultJson.optString("message");
            if (!TextUtils.isEmpty("message")) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
            throw new BusinessException(e);
        }
        return null;
    }

    public int sendSmscode(String mobilephone, String msgType) throws BusinessException {
        try {
            final JSONObject obj = this.accountBindStub.sendSmscode(mobilephone, msgType);
            if (obj == null) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
                return 0;
            }
            if (obj.optInt("code") == 0) {
                return obj.getInt("result");
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            Toasts.showOnUiThread(activity, activity.getString(R.string.account_failure));
            return 0;
        }
        return 0;
    }

}
