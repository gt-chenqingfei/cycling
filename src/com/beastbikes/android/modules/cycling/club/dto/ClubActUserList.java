package com.beastbikes.android.modules.cycling.club.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangyao on 2016/1/18.
 */
public class ClubActUserList implements Serializable{

    private List<ClubActivityUser> users;

    public List<ClubActivityUser> getUsers() {
        return users;
    }

    public void setUsers(List<ClubActivityUser> users) {
        this.users = users;
    }
    public ClubActUserList(List<ClubActivityUser> users){
        this.users = users;
    }
}
