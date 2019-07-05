package com.beastbikes.android.modules.cycling.club.biz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dao.ClubsDao;
import com.beastbikes.android.modules.cycling.club.dao.entity.Club;
import com.beastbikes.android.modules.cycling.club.dto.ApplyDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubLevel;
import com.beastbikes.android.modules.cycling.club.dto.ClubNoticeBean;
import com.beastbikes.android.modules.cycling.club.dto.ClubRankBean;
import com.beastbikes.android.modules.cycling.club.dto.Privilege;
import com.beastbikes.android.modules.cycling.ranking.biz.RankingManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO2;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.utils.JSONUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClubManager extends AbstractBusinessObject implements Constants {

    private static final Logger logger = LoggerFactory.getLogger(ClubManager.class);

    public static final String MSG_TYPE_REG_CLUB = "regClub";
    public static final String MSG_TYPE_GET_PRIZE = "getPrize";
    public static final String MSG_TYPE_BIND_PHONE = "bindPhone";

    /**
     * 推荐排序
     */
    public static final int CLUB_ORDERBY_RECOMMEND = 0;
    /**
     * 积分排序
     */
    public static final int CLUB_ORDERBY_SCORE = 1;
    /**
     * 默认排序
     */
    public static final int CLUB_ORDERBY_NONE = 2;

    /**
     * 加入时间
     */
    public static final int CLUB_MEMBER_ORDERBY_JOINDE = 0;
    /**
     * 里程
     */
    public static final int CLUB_MEMBER_ORDERBY_MILESTONE = 1;
    /**
     * 默认不传
     */
    public static final int CLUB_MEMBER_ORFERBY_NONE = 2;

    public static final int CLUB_RANK_MONTH = 1;//俱乐部排行月
    public static final int CLUB_RANK_TOTAL = 0;//排行总

    public static final int SEND_SMS_CODE_ERROR = -1;

    private final ClubsDao dao;
    private SharedPreferences userSp;

    private ClubServiceStub clubServiceStub;
    private Activity activity;
    private Context context;


    public enum CLUB_ORDERBY {
        RECOMMEND, // 推荐排序
        SCORE, // 积分
        NONE, // 默认不传
    }

    public ClubManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());

        final BeastBikes app = (BeastBikes) activity.getApplicationContext();
        final ORMLitePersistenceManager pm = app.getPersistenceManager();
        this.dao = new ClubsDao((ORMLitePersistenceSupport) pm);

        if (AVUser.getCurrentUser() != null) {
            this.userSp = activity.getSharedPreferences(AVUser.getCurrentUser()
                    .getObjectId(), 0);
        }
        this.context = activity;
        this.activity = activity;
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.clubServiceStub = factory.create(ClubServiceStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(activity));
    }

    public ClubManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        this.context = context;
        final BeastBikes app = (BeastBikes) context.getApplicationContext();
        final ORMLitePersistenceManager pm = app.getPersistenceManager();
        this.dao = new ClubsDao((ORMLitePersistenceSupport) pm);
        if (AVUser.getCurrentUser() != null) {
            this.userSp = context.getSharedPreferences(AVUser.getCurrentUser()
                    .getObjectId(), 0);
        }
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.clubServiceStub = factory.create(ClubServiceStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(context));
    }

    /**
     * get club level
     * @return club level
     */
    public int getClubLevel() {
        return this.userSp.getInt(PREF_CLUB_LEVEL, 0);
    }

    /**
     * 获取我的俱乐部的状态
     *
     * @param userId （用户id）
     * @return ClubInfoCompact （俱乐部状态）
     * @throws BusinessException
     */
    public ClubInfoCompact getMyClub(final String userId)
            throws BusinessException {
        try {
            Club club = this.dao.getMyClubInfo(userId);
            if (null == club)
                return null;

            ClubInfoCompact info = new ClubInfoCompact();
            info.setObjectId(club.getClubId());
            info.setName(club.getClubName());
            info.setLogo(club.getClubLogo());
            info.setDesc(club.getClubDesc());
            info.setManagerId(club.getClubManagerId());
            info.setProvince(club.getClubProvince());
            info.setCity(club.getClubCity());
            info.setMilestone(club.getClubMilestone());
            info.setNotice(club.getClubNotice());
            info.setMaxMembers(club.getMaxMembers());
            info.setMembers(club.getClubMembers());
            info.setActivities(club.getActivities());
            info.setScore(club.getClubScore());
            info.setLevel(club.getLevel());
            info.setStatus(club.getStatus());
            info.setRank(club.getRank());
            info.setType(club.getType());
            info.setLinkTo(club.getLinkTo());
            if (club.getIsPrivate() == 0) {
                info.setIsPrivate(false);
            } else {
                info.setIsPrivate(true);
            }
            info.setClubLevel(club.getClubLevel());
            return info;
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 获取我与俱乐部的关系
     *
     * @param userId （用户Id）
     * @return Club    （俱乐部关系信息）
     * @throws BusinessException
     */
    public Club getMyClubRelation(final String userId) throws BusinessException {
        try {
            final JSONObject result = this.clubServiceStub.getMyClubRelation();
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                Club club = this.dao.getMyClubInfo(userId);
                if (null == club) {
                    club = new Club();
                    club.setId(UUID.randomUUID().toString());
                }

                JSONObject obj = result.optJSONObject("result");
                club.setClubId(obj.optString("clubId"));
                club.setUserId(userId);
                club.setLevel(obj.optInt("level"));
                club.setStatus(obj.optInt("status"));
                this.dao.createOrUpdate(club);
                AVUser user =AVUser.getCurrentUser();
                if(user != null &&
                        (club.getStatus() == ClubInfoCompact.CLUB_STATUS_JOINED
                                || club.getStatus() == ClubInfoCompact.CLUB_STATUS_ESTABLISHED)){
                    user.setClubId(club.getClubId());
                }
                if (!TextUtils.isEmpty(club.getClubId())) {
                    new FriendManager(context).getClubChatNick(userId, club.getClubId());
                }

                Editor editor = this.userSp.edit();
                editor.putString(PREF_CLUB_USER_ID, userId);
                editor.putString(PREF_CLUB_ID, obj.optString("clubId"));
                editor.putInt(PREF_CLUB_LEVEL, obj.optInt("level"));
                editor.putInt(PREF_CLUB_STATUS, obj.optInt("status"));
                editor.apply();

                return club;
            }

            return null;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * @param clubId （俱乐部Id）
     * @return ClubInfoCompact  （俱乐部详情）
     * @throws BusinessException
     */
    public ClubInfoCompact getClubInfo(final String clubId)
            throws BusinessException {
        try {
            final JSONObject result = this.clubServiceStub.getClubInfo(clubId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject club = result.optJSONObject("result");
                final ClubInfoCompact cic = new ClubInfoCompact(club);
                AVUser avUser = AVUser.getCurrentUser();
                if (avUser == null)
                    return null;
                Club clubInfo = this.dao.getMyClubInfo(avUser.getObjectId());
                if (null != cic && null != clubInfo
                        && clubInfo.getClubId().equals(cic.getObjectId())) {
                    clubInfo.setClubDesc(cic.getDesc());
                    clubInfo.setClubName(cic.getName());
                    clubInfo.setClubId(cic.getObjectId());
                    clubInfo.setActivities(cic.getActivities());
                    clubInfo.setClubCity(cic.getCity());
                    clubInfo.setClubDesc(cic.getDesc());
                    clubInfo.setClubLogo(cic.getLogo());
                    clubInfo.setClubManagerId(cic.getManagerId());
                    clubInfo.setClubMembers(cic.getMembers());
                    clubInfo.setClubMilestone(cic.getMilestone());
                    clubInfo.setClubName(cic.getName());
                    clubInfo.setClubNotice(cic.getNotice());
                    clubInfo.setClubProvince(cic.getProvince());
                    clubInfo.setClubScore(cic.getScore());
                    clubInfo.setMaxMembers(cic.getMaxMembers());
                    clubInfo.setStatus(clubInfo.getStatus());
                    clubInfo.setLevel(clubInfo.getLevel());
                    clubInfo.setClubLevel(cic.getClubLevel());
                    clubInfo.setType(cic.getType());
                    clubInfo.setLinkTo(cic.getLinkTo());
                    if (cic.getIsPrivate()) {
                        clubInfo.setIsPrivate(1);
                    } else {
                        clubInfo.setIsPrivate(0);
                    }
                    cic.setStatus(clubInfo.getStatus());
                    cic.setLevel(clubInfo.getLevel());
                    // TODO 是否需要本地化图片个数信息
                    this.dao.createOrUpdate(clubInfo);

                }
                return cic;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 获取俱乐部列表
     *
     * @param orderby canBeNull = true
     * @param city    canBeNull = true
     * @param keyName canBeNull = true
     * @param page    canBeNull ＝ true
     * @param count   canBeNull = true
     * @return List （俱乐部列表）
     * @throws BusinessException
     */
    public List<ClubInfoCompact> getClubList(final CLUB_ORDERBY orderby,
                                             String city, String keyName, final int page,
                                             final int count) throws BusinessException {
        String orderBy = "";
        switch (orderby) {
            case RECOMMEND:
                orderBy = "recommend";
                break;
            case SCORE:
                orderBy = "score";
                break;
            case NONE:
                orderBy = "";
                break;
        }

        if (TextUtils.isEmpty(city)) {
            city = "";
        }

        if (TextUtils.isEmpty(keyName)) {
            keyName = "";
        }

        try {
            final JSONObject result = this.clubServiceStub.getClubList(orderBy,
                    city, keyName, page, count);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                List<ClubInfoCompact> list = new ArrayList<>();

                JSONArray array = result.optJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject info = array.optJSONObject(i);
                    list.add(new ClubInfoCompact(info));
                }
                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 申请、退出、撤销申请俱乐部 （0 申请加入，1 退出，2 撤销申请）
     *
     * @param command  canBeNull = true
     * @param clubId   canBeNull = true
     * @param extra    canBeNull = true
     * @param clubInfo canBeNull = true
     * @return boolean （申请、退出、撤销申请俱乐部）
     * @throws BusinessException
     */
    public boolean postCmdClub(final int command, final String clubId,
                               String extra, final ClubInfoCompact clubInfo)
            throws BusinessException {
        if (TextUtils.isEmpty(extra)) {
            extra = "";
        }

        try {
            final JSONObject result = this.clubServiceStub.postCmdClub(clubId, command, extra);
            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                boolean rs = result.optBoolean("result");

                if (rs) {
                    AVUser user = AVUser.getCurrentUser();
                    if (null == user) {
                        return rs;
                    }

                    Editor editor = this.userSp.edit();

                    switch (command) {
                        case 0:// 申请加入成功

                            Club club = this.dao.getMyClubInfo(user.getObjectId());
                            if (null == club) {
                                club = new Club();
                                club.setId(UUID.randomUUID().toString());
                                club.setUserId(user.getObjectId());
                            }

                            club.setClubId(clubId);
                            club.setLevel(0);

                            club.setStatus(ClubInfoCompact.CLUB_STATUS_APPLY);
                            editor.putInt(PREF_CLUB_STATUS, ClubInfoCompact.CLUB_STATUS_APPLY);

                            if (null != clubInfo) {
                                club.setClubName(clubInfo.getName());
                                club.setClubLogo(clubInfo.getLogo());
                                club.setClubDesc(clubInfo.getDesc());
                                club.setClubManagerId(clubInfo.getManagerId());
                                club.setMaxMembers(clubInfo.getMaxMembers());
                                club.setClubMembers(clubInfo.getMembers());
                                club.setClubMilestone(clubInfo.getMilestone());
                                club.setClubNotice(clubInfo.getNotice());
                                club.setClubProvince(clubInfo.getProvince());
                                club.setClubCity(clubInfo.getCity());
                                club.setClubScore(clubInfo.getScore());
                                club.setActivities(clubInfo.getActivities());
                                club.setRank(clubInfo.getRank());
                                club.setType(clubInfo.getType());
                                club.setLinkTo(clubInfo.getLinkTo());
                                user.setClubName(clubInfo.getName());
                                user.setClubId(clubId);
                                AVUser.saveCurrentUser(user);
                            }

                            this.dao.createOrUpdate(club);

                            editor.putString(PREF_CLUB_USER_ID, user.getObjectId());
                            editor.putString(PREF_CLUB_ID, clubId);
                            editor.putInt(PREF_CLUB_LEVEL, 0);
                            break;
                        case 1:// 退出成功
                        case 2:// 撤销申请成功
                            user.setClubId("");
                            user.setClubName("");
                            AVUser.saveCurrentUser(user);
                            this.dao.deleteClub(clubId);

                            editor.putInt(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY, 0);
                            editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);
                            editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0);
                            editor.putInt(PUSH.PREF_KEY.DOT_GROUP_CHAT, 0);

                            int total = this.userSp.getInt(PREF_RONGCLOUD_NEW_MESSAGE_COUNT,0);
                            int groupCount = this.userSp.getInt(PUSH.PREF_KEY.DOT_GROUP_CHAT,0);
                            total = total - groupCount;
                            editor.putInt(PREF_RONGCLOUD_NEW_MESSAGE_COUNT,total);
                            editor.putInt(PREF_CLUB_STATUS, ClubInfoCompact.CLUB_STATUS_QUIT);
                            editor.putInt(PREF_CLUB_LEVEL, 0);
                            break;
                    }
                    editor.apply();

                }
                return rs;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            return false;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * @return 获取我的俱乐部排行
     * @throws BusinessException
     */
    public ClubInfoCompact getMyClubRank(int rankType) {
        try {
            final JSONObject result = this.clubServiceStub.getMyClubRank(rankType);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject objResult = result.optJSONObject("result");
                ClubInfoCompact info = new ClubInfoCompact(
                        objResult.optJSONObject("myClub"));
                info.setRank(objResult.optInt("rank"));
                return info;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取俱乐部成员排行
     *
     * @param clubId  俱乐部Id
     * @param orderby 排序
     * @param page    页码
     * @param count   每页个数
     * @return 成员排行列表
     * @throws BusinessException
     */
    public List<RankDTO> getClubMemberList(final String clubId,
                                           final int orderby, final int page, final int count)
            throws BusinessException {
        String order = "joined";
        switch (orderby) {
            case CLUB_MEMBER_ORDERBY_JOINDE:
                order = "joined";
                break;
            case CLUB_MEMBER_ORDERBY_MILESTONE:
                order = "milestone";
                break;
            case CLUB_MEMBER_ORFERBY_NONE:
                order = "milestone";
                break;
        }

        try {
            final JSONObject result = this.clubServiceStub.getClubMemberList(clubId, page, count, order);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject resultObj = result.optJSONObject("result");

                List<RankDTO> list = new ArrayList<>();
                RankDTO dto;

                JSONObject managerJson = resultObj.optJSONObject("manager");
                RankDTO manager = new RankDTO(managerJson);
                list.add(manager);

                JSONArray array = resultObj.optJSONArray("members");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject rank = array.optJSONObject(i);
                    dto = new RankDTO();
                    dto.setUserId(rank.optString("userId"));
                    dto.setScore(rank.optDouble("score"));
                    dto.setManager(rank.optBoolean("ismanager"));
                    dto.setLevel(rank.optInt("level"));
                    dto.setMilestone(rank.optDouble("milestone"));
                    dto.setJoined(rank.optString("joined"));
                    dto.setRemarks(rank.optString("remarks"));
                    JSONObject user = rank.optJSONObject("user");
                    if (null != user) {
                        if (user.has("avatarImage")) {
                            dto.setAvatarUrl(user.optString("avatarImage"));
                        } else {
                            dto.setAvatarUrl(user.optString("avatar"));
                        }
                        dto.setNickname(user.optString("nickname"));
                        dto.setProvince(user.optString("province"));
                        dto.setCity(user.optString("city"));
                    }
                    dto.setRankType(RankingManager.NONE);
                    dto.setOrdinal(i + 1);
                    list.add(dto);
                }
                return list;

            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Update Club Notice
     *
     * @param notice Notice
     * @return boolean
     * @throws BusinessException
     */
    public boolean postUpdateClubNotice(final String notice) throws BusinessException {
        if (TextUtils.isEmpty(notice)) {
            return false;
        }

        try {
            final JSONObject result = this.clubServiceStub.postUpdateClubNotice(notice);
            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                Editor editor = userSp.edit();
                editor.putString(CLUB_NOTICE, notice);
                editor.apply();
                boolean rs = result.optBoolean("result");
                if (rs) {
                    try {
                        AVUser user = AVUser.getCurrentUser();
                        if (null != user) {
                            Club club = this.dao.getMyClubInfo(user.getObjectId());
                            if (null != club) {
                                club.setClubNotice(notice);
                                this.dao.update(club);
                            }
                        }
                    } catch (PersistenceException e1) {
                        e1.printStackTrace();
                    }
                }
                return rs;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return false;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 更新俱乐部信息
     *
     * @param logoImage canBeNull=true
     * @param name      canBeNull=true
     * @param province  canBeNull=true
     * @param city      canBeNull=true
     * @param desc      canBeNull=true
     * @param notice    canBeNull=true
     * @return
     * @throws BusinessException
     */
    public boolean postUpdateClubInfo(final String logoImage,
                                      final String name, final String province, final String city,
                                      final String desc, final String notice, final int isPrivate, final String localLogoImage) throws BusinessException {
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return false;
        }

        Club club = null;
        try {
            club = this.dao.getMyClubInfo(user.getObjectId());
        } catch (PersistenceException e1) {
        }

        if (null == club) {
            club = new Club();
            club.setId(UUID.randomUUID().toString());
            club.setUserId(user.getObjectId());
            club.setLevel(128);
            club.setStatus(ClubInfoCompact.CLUB_STATUS_ESTABLISHED);
        }

        if (!TextUtils.isEmpty(localLogoImage)) {
            club.setClubLogo(localLogoImage);
            SpeedxAnalytics.onEvent(BeastBikes.getInstance()
                    .getApplicationContext(), "更换俱乐部logo", null);
        }

        if (!TextUtils.isEmpty(name)) {
            club.setClubName(name);
            SpeedxAnalytics.onEvent(BeastBikes.getInstance()
                    .getApplicationContext(), "更改俱乐部名称", null);
        }

        if (!TextUtils.isEmpty(province)) {
            club.setClubProvince(province);
        }

        if (!TextUtils.isEmpty(city)) {
            club.setClubCity(city);
        }

        if (!TextUtils.isEmpty(desc)) {
            club.setClubDesc(desc);
            SpeedxAnalytics.onEvent(BeastBikes.getInstance()
                    .getApplicationContext(), "更改俱乐部简介", null);
        }

        if (!TextUtils.isEmpty(notice)) {
            club.setClubNotice(notice);
        }
        club.setIsPrivate(isPrivate);
        try {
            final JSONObject result;
            result = this.clubServiceStub.postUpdateClubInfo(name,
                    logoImage, province, city, desc, notice, isPrivate);

            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                Editor editor = userSp.edit();
//                editor.putString(CLUB_LOGO, club.getClubLogo());
                editor.putString(CLUB_NAME, club.getClubName());
                editor.putString(CLUB_DESC, club.getClubDesc());
                editor.apply();
                boolean rs = result.optBoolean("result");
                if (rs) {
                    this.dao.update(club);
                }
                return rs;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return false;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 创建俱乐部
     *
     * @param logoId      俱乐部logo 选填
     * @param name        俱乐部名称
     * @param province    省份
     * @param city        城市
     * @param desc        描述
     * @param realName    真实姓名
     * @param mobilephone 手机号
     * @param qq          qq号 选填
     * @param vcode       验证码
     * @param isPrivate   是否私密俱乐部
     * @return 创建成功的俱乐部信息
     * @throws BusinessException
     */
    public ClubInfoCompact postRegisterClub(final String logoId,
                                            final String name, final String province, final String city,
                                            final String desc, final String realName, final String mobilephone,
                                            final String qq, final String vcode,
                                            final int isPrivate, double latitude, double longitude)
            throws BusinessException {

        try {
            final JSONObject result = this.clubServiceStub.postRegisterClub(name, logoId,
                    province, city, desc, realName, mobilephone, qq, vcode,
                    isPrivate, latitude, longitude);

            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject json = result.optJSONObject("result");
                ClubInfoCompact clubInfo = new ClubInfoCompact(json);
                clubInfo.setStatus(ClubInfoCompact.CLUB_STATUS_ESTABLISHED);
                final AVUser user = AVUser.getCurrentUser();
                if (null == user) {
                    return clubInfo;
                }

                Club club = this.dao.getMyClubInfo(user.getObjectId());
                if (null == club) {
                    club = new Club();
                    club.setId(UUID.randomUUID().toString());
                    club.setUserId(user.getObjectId());
                }

                club.setClubId(clubInfo.getObjectId());
                club.setClubName(clubInfo.getName());
                club.setClubLogo(clubInfo.getLogo());
                club.setClubDesc(clubInfo.getDesc());
                club.setClubManagerId(clubInfo.getManagerId());
                club.setMaxMembers(clubInfo.getMaxMembers());
                club.setClubMembers(clubInfo.getMembers());
                club.setClubMilestone(clubInfo.getMilestone());
                club.setClubNotice(clubInfo.getNotice());
                club.setClubProvince(clubInfo.getProvince());
                club.setClubCity(clubInfo.getCity());
                club.setClubScore(clubInfo.getScore());
                club.setActivities(clubInfo.getActivities());
                club.setRank(clubInfo.getRank());
                club.setLevel(128);
                club.setStatus(clubInfo.getStatus());// 申请成功及默认为审核状态

                user.setClubId(clubInfo.getObjectId());
                user.setClubName(clubInfo.getName());
                this.dao.createOrUpdate(club);


                //ClubInfoCompact里的club level指的是俱乐部的等级,不是人员的等级,人员等级由自己设定,0代表成员, 128代表管理员
                Editor editor = this.userSp.edit();
                editor.putString(PREF_CLUB_ID, clubInfo.getObjectId());
                editor.putString(PREF_CLUB_USER_ID, user.getObjectId());
                editor.putInt(PREF_CLUB_LEVEL, club.getLevel());
                editor.putInt(PREF_CLUB_STATUS, clubInfo.getStatus());
                editor.apply();
                return clubInfo;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 获取未读消息数
     *
     * @return 获取未读消息数
     * @throws BusinessException
     */
    public JSONObject getUnReadCount() throws BusinessException {
        try {
            final JSONObject result = this.clubServiceStub.getUnReadCount();
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optJSONObject("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * @param page  (页码)
     * @param count （每页个数）
     * @return 获取入队申请列表
     * @throws BusinessException
     */
    public List<ApplyDTO> getClubApplyList(final int page, final int count)
            throws BusinessException {
        try {
            final JSONObject result = this.clubServiceStub.getClubApplyList(page, count);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONArray array = result.optJSONArray("result");
                final List<ApplyDTO> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(new ApplyDTO(array.optJSONObject(i)));
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            return null;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 处理入队申请
     *
     * @param applyId 申请ID
     * @param command 0 同意申请，1 删除&忽略申请
     * @return boolean 是否成功
     * @throws BusinessException
     */
    public boolean postClubApply(final String applyId, final int command)
            throws BusinessException {
        if (TextUtils.isEmpty(applyId)) {
            return false;
        }

        try {
            final JSONObject result = this.clubServiceStub.postClubApply(applyId, command);
            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                boolean isOk = result.optBoolean("result");
                if (isOk) {
                    int applyCount = this.userSp.getInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);
                    if (applyCount > 0) {
                        this.userSp.edit()
                                .putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, applyCount - 1)
                                .apply();
                    }
                }
                return isOk;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return false;
        } catch (Exception e) {
            throw new BusinessException(e);
        }

    }

    /**
     * 管理俱乐部成员
     *
     * @param memberId userId
     * @param command  0 剔除成员
     * @return boolean 是否删除成功
     * @throws BusinessException
     */
    public boolean postCmdClubMember(final String memberId, final int command)
            throws BusinessException {
        if (TextUtils.isEmpty(memberId)) {
            return false;
        }

        try {
            final JSONObject result = this.clubServiceStub.postCmdClubMember(memberId, command);
            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optBoolean("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return false;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * api v2.1
     * 获取俱乐部排行榜列表
     * 参数:
     * rankType (int) – 0 总榜, 1 月榜
     * page (int) – 分页 默认1起
     * count (int) – 单页数量
     */
    public List<ClubRankBean> getClubRankList(final int type, int page, int count) throws BusinessException {
//        try {
        final JSONObject result = this.clubServiceStub.getClubRankList(type, page, count);

        if (result == null)
            return null;
        if (result.optInt("code") == 0) {
            JSONArray array = result.optJSONArray("result");
            final List<ClubRankBean> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                list.add(new ClubRankBean(array.optJSONObject(i)));
            }
            return list;
        } else {
            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        }
//        }
//        catch (Exception e){
//            log.e("e",e.);
//            return null;
//        }
    }


    public List<RankDTO2> getClubMemberRankList(int rankType, int page, int count, final String clubId) throws BusinessException {
        JSONObject result = clubServiceStub.getClubMemberRankList(rankType, page, count, clubId);
        if (result == null)
            return null;
        if (result.optInt("code") == 0) {
            List<RankDTO2> list = new ArrayList<>();
            JSONArray array = result.optJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                list.add(new RankDTO2(array.optJSONObject(i)));
            }
            return list;
        } else {
            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        }
    }


    public List<ClubNoticeBean> getClubNoticeList(final String clubId, int page, int count) throws BusinessException {
        if (TextUtils.isEmpty(clubId))
            return null;
        JSONObject result = clubServiceStub.getClubNoticeList(clubId, page, count);
        if (result == null)
            return null;
        if (result.optInt("code") == 0) {
            List<ClubNoticeBean> list = new ArrayList<>();
            JSONArray array = result.optJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                list.add(new ClubNoticeBean(array.optJSONObject(i)));
            }
            return list;
        } else {
            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        }
    }

    public int sendSmscode(String mobilephone, String msgType) {
        if (TextUtils.isEmpty(mobilephone))
            return SEND_SMS_CODE_ERROR;
        JSONObject result = clubServiceStub.sendSmscode(mobilephone, msgType);
        if (result == null)
            return SEND_SMS_CODE_ERROR;
        if (result.optInt("code") == 0) {
            int count = result.optInt("result");
            return count;
        } else {
            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return SEND_SMS_CODE_ERROR;
        }
    }

    public void updateClubStatus(ClubInfoCompact clubInfo) throws BusinessException {
        try {
            if (clubInfo == null)
                return;
            Club club = new Club();
            club.setClubId(clubInfo.getObjectId());
            club.setClubName(clubInfo.getName());
            club.setClubLogo(clubInfo.getLogo());
            club.setClubDesc(clubInfo.getDesc());
            club.setClubManagerId(clubInfo.getManagerId());
            club.setMaxMembers(clubInfo.getMaxMembers());
            club.setClubMembers(clubInfo.getMembers());
            club.setClubMilestone(clubInfo.getMilestone());
            club.setClubNotice(clubInfo.getNotice());
            club.setClubProvince(clubInfo.getProvince());
            club.setClubCity(clubInfo.getCity());
            club.setClubScore(clubInfo.getScore());
            club.setActivities(clubInfo.getActivities());
            club.setRank(clubInfo.getRank());
            club.setLevel(clubInfo.getLevel());
            club.setStatus(clubInfo.getStatus());// 申请成功及默认为审核状态
            this.dao.updateClubStatus(club);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }


    public List<ClubLevel> getClubLevelInfo() throws BusinessException {
        List<ClubLevel> clubLevels = null;
        JSONObject object = clubServiceStub.getClubLevelInfo();
        if (object == null) return clubLevels;
        try {
            if (object.optInt("code") == 0) {
                JSONArray result = object.optJSONArray("result");
                if (!JSONUtil.isNull(result)) {
                    clubLevels = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        ClubLevel level = new ClubLevel(result.optJSONObject(i));
                        clubLevels.add(level);
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException();
        }
        return clubLevels;
    }

    public Privilege getClubPrivilegInfo() throws BusinessException {
        Privilege privilege = null;
        List<String> privileges = null;
        JSONObject object = clubServiceStub.getClubPrivilegInfo();
        if (object == null) return privilege;
        try {
            if (object.optInt("code") == 0) {
                JSONObject result = object.optJSONObject("result");
                if (!JSONUtil.isNull(result)) {
                    JSONArray array = result.optJSONArray("privileges");
                    privileges = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);
                        privileges.add(item.optString("name"));
                    }
                    int level = result.optInt("curLevel");
                    privilege = new Privilege(level, privileges);
                }
            }
        } catch (Exception e) {
            throw new BusinessException();
        }
        return privilege;
    }

    /**
     * 修改状态为已加入俱乐部（在收到俱乐部入队申请之后调用）
     */
    public void updateClubStatus2ApplyPass() {
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }
        try {
            Club club = this.dao.getMyClubInfo(user.getObjectId());
            if (null == club) {
                return;
            }

            club.setStatus(ClubInfoCompact.CLUB_STATUS_JOINED);
            user.setClubId(club.getClubId());
            this.dao.createOrUpdate(club);

            if (null != userSp) {
                this.userSp.edit().putInt(PREF_CLUB_STATUS, ClubInfoCompact.CLUB_STATUS_JOINED).apply();
            }
        } catch (PersistenceException e) {
            logger.error("Update club status to apply pass error, " + e);
        }
    }

    /**
     * 修改状态为空（在收到俱乐部入队申请被拒绝之后调用）
     */
    public void updateClubStatus2ApplyRefuse() {
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }
        try {
            Club club = this.dao.getMyClubInfo(user.getObjectId());
            if (null == club) {
                return;
            }

            club.setStatus(ClubInfoCompact.CLUB_STATUS_NONE);
            user.setClubId(club.getClubId());
            this.dao.deleteClub(club.getClubId());

            if (null != userSp) {
                this.userSp.edit().putInt(PREF_CLUB_STATUS, ClubInfoCompact.CLUB_STATUS_APPLY_REFUSED).apply();
            }
        } catch (PersistenceException e) {
            logger.error("Update club status to apply pass error, " + e);
        }
    }

    /**
     * 转让俱乐部
     *
     * @param memberId
     * @param isQuit   转让完成后是否退出 默认0 0 不退出 1 退出俱乐部
     * @return
     * @throws BusinessException
     */
    public boolean transferClub(String memberId, int isQuit) throws BusinessException {
        boolean ret = false;

        JSONObject object = clubServiceStub.transferClub(memberId, isQuit);
        if (object == null) return ret;
        try {
            if (object.optInt("code") == 0) {
                ret = object.optBoolean("result");
            } else {
                String message = object.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException();
        }
        return ret;
    }

    /**
     * 俱乐部转让提醒
     *
     * @return
     * @throws BusinessException
     */
    public boolean sendClubTransNotify() throws BusinessException {
        boolean ret = false;

        JSONObject object = clubServiceStub.sendClubTransNotify();
        if (object == null) return ret;
        try {
            if (object.optInt("code") == 0) {
                ret = object.optBoolean("result");
            } else {
                String message = object.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException();
        }
        return ret;
    }

    /**
     * 取消俱乐部转让
     *
     * @return
     * @throws BusinessException
     */
    public boolean cancelClubTrans() throws BusinessException {
        boolean ret = false;

        JSONObject object = clubServiceStub.cancelClubTrans();
        if (object == null) return ret;
        try {
            if (object.optInt("code") == 0) {
                ret = object.optBoolean("result");
            } else {
                String message = object.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException();
        }
        return ret;
    }

    /**
     * 获取俱乐部转让状态
     *
     * @return
     * @throws BusinessException
     */
    public String getClubTransStatus() throws BusinessException {
        String ret = null;

        JSONObject object = clubServiceStub.getClubTransStatus();
        if (object == null) return ret;
        try {
            if (object.optInt("code") == 0) {
                JSONArray result = object.optJSONArray("result");
                if (result.length() > 0) {
                    JSONObject object1 = result.getJSONObject(result.length() - 1);
                    if (object1 != null) {
                        ret = object1.optString("nickname");
                    }
                }
            } else {
                String message = object.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException();
        }
        return ret;
    }

}
