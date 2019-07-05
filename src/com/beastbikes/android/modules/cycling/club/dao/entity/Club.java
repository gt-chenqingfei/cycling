package com.beastbikes.android.modules.cycling.club.dao.entity;

import android.os.Parcel;

import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = BeastStore.Clubs.CONTENT_CATEGORY)
public class Club implements PersistentObject {

    private static final long serialVersionUID = -4797455584982225068L;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_ID)
    private String clubId;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_NAME)
    private String clubName;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_DESC)
    private String clubDesc;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_MANAGER_ID)
    private String clubManagerId;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.MAX_MEMBERS)
    private int maxMembers;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_MEMBERS)
    private int clubMembers;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_SCORE)
    private double clubScore;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_MILESTONE)
    private double clubMilestone;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_LOGO)
    private String clubLogo;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_PROVINCE)
    private String clubProvince;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_CITY)
    private String clubCity;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_NOTICE)
    private String clubNotice;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_ACTIVITIES)
    private int activities;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.USER_ID)
    private String userId;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.LEVEL)
    private int level;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.STATUS)
    private int status;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.RANK)
    private int rank;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.ISPRIVATE)
    private int isPrivate;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.CLUB_LEVEL)
    private int clubLevel;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.TYPE)
    private int type;

    @DatabaseField(columnName = BeastStore.Clubs.ClubsColumns.LINKTO)
    private long linkTo;

    public Club() {

    }

    public Club(Parcel source) {
        this.id = source.readString();
        this.clubId = source.readString();
        this.clubName = source.readString();
        this.clubDesc = source.readString();
        this.clubManagerId = source.readString();
        this.maxMembers = source.readInt();
        this.clubMembers = source.readInt();
        this.clubScore = source.readDouble();
        this.clubMilestone = source.readDouble();
        this.clubLogo = source.readString();
        this.clubCity = source.readString();
        this.userId = source.readString();
        this.level = source.readInt();
        this.status = source.readInt();
        this.clubNotice = source.readString();
        this.activities = source.readInt();
        this.clubProvince = source.readString();
        this.rank = source.readInt();
        this.isPrivate = source.readInt();
        this.clubLevel = source.readInt();
        this.type = source.readInt();
        this.linkTo = source.readLong();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubDesc() {
        return clubDesc;
    }

    public void setClubDesc(String clubDesc) {
        this.clubDesc = clubDesc;
    }

    public String getClubManagerId() {
        return clubManagerId;
    }

    public void setClubManagerId(String clubManagerId) {
        this.clubManagerId = clubManagerId;
    }

    public int getClubMembers() {
        return clubMembers;
    }

    public void setClubMembers(int clubMembers) {
        this.clubMembers = clubMembers;
    }

    public double getClubScore() {
        return clubScore;
    }

    public void setClubScore(double clubScore) {
        this.clubScore = clubScore;
    }

    public double getClubMilestone() {
        return clubMilestone;
    }

    public void setClubMilestone(double clubMilestone) {
        this.clubMilestone = clubMilestone;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getClubCity() {
        return clubCity;
    }

    public void setClubCity(String clubCity) {
        this.clubCity = clubCity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getClubProvince() {
        return clubProvince;
    }

    public void setClubProvince(String clubProvince) {
        this.clubProvince = clubProvince;
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }

    public String getClubNotice() {
        return clubNotice;
    }

    public void setClubNotice(String clubNotice) {
        this.clubNotice = clubNotice;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getClubLevel() {
        return clubLevel;
    }

    public void setClubLevel(int clubLevel) {
        this.clubLevel = clubLevel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(long linkTo) {
        this.linkTo = linkTo;
    }
}
