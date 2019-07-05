package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 俱乐部简要信息(主要用于列表结构中)
 *
 * @author chenqingfei
 */
public class ClubInfoCompact implements Serializable {

    private static final long serialVersionUID = 6214355107311561136L;

    public static final int CLUB_STATUS_NONE = 0;// 未加入
    public static final int CLUB_STATUS_JOINED = 1;// 已加入
    public static final int CLUB_STATUS_APPLY = 2;// 加入申请审核中
    public static final int CLUB_STATUS_CREATE = 3;// 创建申请审核中
    public static final int CLUB_STATUS_ESTABLISHED = 4;// 已创建
    public static final int CLUB_STATUS_QUIT = 5;// 退出中,临时
    public static final int CLUB_STATUS_APPLY_REFUSED = 6;// 被拒绝,临时


    /**
     * 俱乐部id
     *
     * @type String
     */
    private String objectId;

    /**
     * 俱乐部名称
     *
     * @type String
     */
    private String name;

    /**
     * 俱乐部简介
     *
     * @type String
     */
    private String desc;

    /**
     * 俱乐部管理员ID
     *
     * @type String
     */
    private String managerId;

    /**
     * 俱乐部里程
     *
     * @type Number
     */
    private double milestone;

    /**
     * 俱乐部积分
     *
     * @type Number
     */
    private double score;

    /**
     * 俱乐部最大成员数量
     *
     * @type Number
     */
    private int maxMembers = 0;

    /**
     * 当前成员数
     *
     * @type Number
     */
    private int members;

    /**
     * 活动数
     */
    private int activities;

    /**
     * 俱乐部logo
     *
     * @type String
     */
    private String logo;

    /**
     * 俱乐部所在省
     */
    private String province;

    /**
     * 俱乐部所在市
     *
     * @type String
     */
    private String city;

    /**
     * 俱乐部公告
     */
    private String notice;

    /**
     * 俱乐部排行
     */
    private int rank;

    /**
     * 成员Level (0 成员， 128 管理员)
     */
    private int level;
    /**
     * 与俱乐部关系
     */
    private int status;

    private int ordinal;
    /**
     * 最大张数
     */
    private int maxPhotoNum;
    /**
     * 当前张数
     */
    private int curPhotoNum;


    /**
     * 是否为私密俱乐部 1表示私密 0是公开
     */
    private boolean isPrivate;

    /**
     * 俱乐部等级
     */
    private int clubLevel;

    private int type;

    private long linkTo;

    public ClubInfoCompact() {

    }

    public ClubInfoCompact(String name, String avatar, int members,
                           double milestone, String city, String clubId) {
        this.name = name;
        this.logo = avatar;
        this.members = members;
        this.milestone = milestone;
        this.city = city;
        this.objectId = clubId;
    }

    public ClubInfoCompact(JSONObject json) {
        this.objectId = json.optString("objectId");
        this.name = json.optString("name");
        this.desc = json.optString("desc");
        this.managerId = json.optString("managerId");
        this.milestone = json.optDouble("milestone", 0);
        this.score = json.optDouble("score", 0);
        this.maxMembers = json.optInt("maxMembers", 50);
        this.members = json.optInt("members", 1);
        this.logo = json.optString("logo");
        this.province = json.optString("province");
        this.city = json.optString("city");
        this.activities = json.optInt("activities", 0);
        this.notice = json.optString("notice");
        this.rank = json.optInt("rank");
        this.status = json.optInt("status");
        this.maxPhotoNum = json.optInt("maxPhotoNum");
        this.curPhotoNum = json.optInt("curPhotoNum");
        this.isPrivate = json.optBoolean("isPrivate");
        this.clubLevel = json.optInt("level");
        this.type = json.optInt("type");
        this.linkTo = json.optLong("linkTo");
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMilestone() {
        return milestone;
    }

    public void setMilestone(double milestone) {
        this.milestone = milestone;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        if (members >= 0) {
            this.members = members;
        }
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
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

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
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

    public int getMaxPhotoNum() {
        return maxPhotoNum;
    }

    public void setMaxPhotoNum(int maxPhotoNum) {
        this.maxPhotoNum = maxPhotoNum;
    }

    public int getCurPhotoNum() {
        return curPhotoNum;
    }

    public void setCurPhotoNum(int curPhotoNum) {
        this.curPhotoNum = curPhotoNum;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
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
