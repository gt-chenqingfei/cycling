package com.beastbikes.android.modules.cycling.club.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubCache implements Serializable {

    public ClubCache() {

    }

    public ClubCache(List<ClubFeed> clubFeeds) {
        this.clubFeeds = clubFeeds;
    }

    List<ClubFeed> clubFeeds;
    List<ClubPhotoDTO> clubPhotoDTOList;

    public List<ClubPhotoDTO> getClubPhotoDTOList() {
        return clubPhotoDTOList;
    }

    public void setClubPhotoDTOList(List<ClubPhotoDTO> clubPhotoDTOList) {
        this.clubPhotoDTOList = clubPhotoDTOList;
    }

    public List<ClubFeed> getClubFeeds() {
        return clubFeeds;
    }

    public void setClubFeeds(List<ClubFeed> clubFeeds) {
        this.clubFeeds = clubFeeds;
    }
}
