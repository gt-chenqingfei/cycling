package com.beastbikes.android.modules.cycling.grid.dao.entity;

import android.os.Parcel;

import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by icedan on 15/12/21.
 */
@DatabaseTable(tableName = BeastStore.Grid.CONTENT_CATEGORY)
public class Grid implements PersistentObject {

    @DatabaseField(columnName = BeastStore.Grid.GirdColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.Grid.GirdColumns.COUNT)
    private int count;

    @DatabaseField(columnName = BeastStore.Grid.GirdColumns.UNLOCK_AT)
    private String unlockAt;

    @DatabaseField(columnName = BeastStore.Grid.GirdColumns.USER_ID)
    private String userId;

    public Grid() {

    }

    public Grid(Parcel source) {
        this.id = source.readString();
        this.count = source.readInt();
        this.unlockAt = source.readString();
        this.userId = source.readString();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUnlockAt() {
        return unlockAt;
    }

    public void setUnlockAt(String unlockAt) {
        this.unlockAt = unlockAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
