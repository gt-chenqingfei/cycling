package com.beastbikes.android.modules.cycling.club.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangyao on 2016/1/13.
 */
public class ClubActivityInfoList implements Serializable{
    private List<ClubActivityListDTO> list;

    public List<ClubActivityListDTO> getList() {
        return list;
    }

    public void setList(List<ClubActivityListDTO> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ClubActivityInfoList{" +
                "list=" + list +
                '}';
    }

    public ClubActivityInfoList(List<ClubActivityListDTO> list) {
        this.list = list;
    }
}
