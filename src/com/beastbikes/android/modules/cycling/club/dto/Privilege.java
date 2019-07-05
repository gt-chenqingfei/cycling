package com.beastbikes.android.modules.cycling.club.dto;

import java.util.List;

/**
 * Created by chenqingfei on 15/12/28.
 */
public class Privilege {
    private  int curLevel;
    private List<String> privileges;

    public Privilege(int curLevel, List<String> privileges) {
        this.curLevel = curLevel;
        this.privileges = privileges;
    }

    public int getCurLevel() {
        return curLevel;
    }

    public void setCurLevel(int curLevel) {
        this.curLevel = curLevel;
    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }
}
