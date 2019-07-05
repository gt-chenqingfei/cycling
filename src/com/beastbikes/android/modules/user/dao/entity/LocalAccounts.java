package com.beastbikes.android.modules.user.dao.entity;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caoxiao on 16/2/15.
 */
@DatabaseTable(tableName = BeastStore.Accounts.CONTENT_CATEGORY)
public class LocalAccounts implements PersistentObject {

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns.AUTHTYPE, canBeNull = false)
    private long authType;

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns.STATUS)
    private long status;

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns.AUTHTOKEN)
    private String authToken;

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns.THIRDNICK)
    private String thirdNick;

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns.AUTHKEY)
    private String authkey;

    @DatabaseField(columnName = BeastStore.Accounts.AccountColumns.USER_ID, canBeNull = false)
    private String userId;

    public LocalAccounts() {
    }

    public LocalAccounts(AccountDTO accountDTO) {
        AVUser user = AVUser.getCurrentUser();
        if (user == null)
            return;
        this.id = user.getObjectId() + accountDTO.getAuthType();
        this.authType = accountDTO.getAuthType();
        this.status = accountDTO.getStatus();
        this.authToken = accountDTO.getAccessToken();
        this.thirdNick = accountDTO.getThirdNick();
        this.authkey = accountDTO.getAuthKey();
        this.userId = user.getObjectId();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAuthType() {
        return authType;
    }

    public void setAuthType(long authType) {
        this.authType = authType;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getThirdNick() {
        return thirdNick;
    }

    public void setThirdNick(String thirdNick) {
        this.thirdNick = thirdNick;
    }

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
