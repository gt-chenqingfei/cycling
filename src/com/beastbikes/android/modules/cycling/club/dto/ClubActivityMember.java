package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.modules.cycling.club.dao.entity.Club;

import java.util.List;

/**
 * Created by zhangyao on 2016/1/14.
 */
public class ClubActivityMember {
    private List<ClubActivityUser> clubActivityUsers;
    private ClubActivityOriginator clubActivityOriginator;
    private boolean isManager;
    private String actId;
    private int count;

    public List<ClubActivityUser> getClubActivityUsers() {
        return clubActivityUsers;
    }

    public void setClubActivityUsers(List<ClubActivityUser> clubActivityUsers) {
        this.clubActivityUsers = clubActivityUsers;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    public ClubActivityOriginator getClubActivityOriginator() {
        return clubActivityOriginator;
    }

    public void setClubActivityOriginator(ClubActivityOriginator clubActivityOriginator) {
        this.clubActivityOriginator = clubActivityOriginator;
    }

    @Override
    public String toString() {
        return "ClubActivityMember{" +
                "clubActivityUsers=" + clubActivityUsers +
                ", clubActivityOriginator=" + clubActivityOriginator +
                ", isManager=" + isManager +
                ", actId='" + actId + '\'' +
                '}';
    }
}
