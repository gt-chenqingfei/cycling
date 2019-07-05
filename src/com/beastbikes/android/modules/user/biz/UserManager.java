package com.beastbikes.android.modules.user.biz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.dto.BleCyclingDTO;
import com.beastbikes.android.modules.cycling.activity.biz.CyclingServiceStub;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.dto.HistogramDTO;
import com.beastbikes.android.modules.user.dto.MedalDTO;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.SeekFriendDTO;
import com.beastbikes.android.modules.user.dto.UserDetailDTO;
import com.beastbikes.android.modules.user.dto.UserStatisticDTO;
import com.beastbikes.android.persistence.BeastPersistenceManager;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.persistence.DataAccessObject;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class UserManager extends AbstractBusinessObject {

    private final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final DataAccessObject<LocalUser> luDao;

    private CyclingServiceStub cyclingServiceStub;
    private UserServiceStub userServiceStub;
    private Context context;
    private Activity activity;

    public static final int SEEK_TYPE_PHONE = 2;
    public static final int SEEK_TYPE_WEIBO = 4;

    public interface LoadLocalProfileListener {
        public void onLoadLocalProfile(ProfileDTO localProfile);
    }

    public UserManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        this.context = context;
        final BeastBikes app = (BeastBikes) context.getApplicationContext();
        final BeastPersistenceManager bpm = app.getPersistenceManager();
        this.luDao = bpm.getDataAccessObject(LocalUser.class);

        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.cyclingServiceStub = factory.create(CyclingServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
        this.userServiceStub = factory.create(UserServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
    }

    public UserManager(Activity context) {
        super((BusinessContext) context.getApplicationContext());
        final BeastBikes app = (BeastBikes) context.getApplicationContext();
        final BeastPersistenceManager bpm = app.getPersistenceManager();
        this.luDao = bpm.getDataAccessObject(LocalUser.class);

        this.activity = context;
        this.context = context;

        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.cyclingServiceStub = factory.create(CyclingServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(activity));
        this.userServiceStub = factory.create(UserServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(activity));
    }

    /**
     * 根据UserId查询User信息
     *
     * @param userId
     * @return
     * @throws PersistenceException
     */
    public LocalUser getLocalUser(String userId) throws BusinessException {
        try {
            if (TextUtils.isEmpty(userId))
                return null;

            return this.luDao.get(userId);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    public ProfileDTO getProfileFromLocal(String userId) throws BusinessException {
        ProfileDTO profileDTO = null;
        LocalUser localUser = getLocalUser(userId);
        if (localUser != null && !TextUtils.isEmpty(localUser.getUserId())
                && !localUser.getUserId().equals("null")) {
            profileDTO = new ProfileDTO();
            profileDTO.setUserId(localUser.getUserId());
            profileDTO.setUsername(localUser.getUsername());
            profileDTO.setNickname(localUser.getNickname());
            profileDTO.setEmail(localUser.getEmail());
            profileDTO.setSex(localUser.getGender());
            profileDTO.setWeight(localUser.getWeight());
            profileDTO.setHeight(localUser.getHeight());
            profileDTO.setProvince(localUser.getProvince());
            profileDTO.setCity(localUser.getCity());
            profileDTO.setDistrict(localUser.getDistrict());
            profileDTO.setTotalDistance(localUser.getTotalDistance());
            profileDTO.setMonthlyDistance(localUser.getMonthlyDistance());
            profileDTO.setLatestActivityTime(localUser.getLatestActivityTime());
            profileDTO.setAvatar(localUser.getAvatar());
            profileDTO.setBirthday(localUser.getBirthday());
            profileDTO.setEdited(localUser.getEdited() == 1);
            profileDTO.setObjectId(localUser.getObjectId());
            profileDTO.setGridNum((int) localUser.getGridNum());
            profileDTO.setSameNum((int) localUser.getSameGrid());
            profileDTO.setClubName(localUser.getClubName());
            profileDTO.setUserIntId((int) localUser.getUserIntId());
            profileDTO.setUpdatedAt(localUser.getUpdatedAt());
            profileDTO.setCreatedAt(localUser.getCreatedAt());
            profileDTO.setClubId(localUser.getClubId());
            profileDTO.setIsOk((int) localUser.getIsOk());
            profileDTO.setFansNum(localUser.getFansNum());
            profileDTO.setFollowNum(localUser.getFollowerNum());
            profileDTO.setFollowStatu(localUser.getFollowStatus());
            profileDTO.setMedalNum(localUser.getMedalNum());
            profileDTO.setSpeedxId(localUser.getSpeedxId());

        }
        return profileDTO;
    }

    public ProfileDTO getProfileByUserId(String userId) throws BusinessException {
        return getProfileByUserId(userId, null);
    }


    /**
     * New restful api v2.0 Query Profile By UserId
     *
     * @param userId
     * @return profile info
     * @throws BusinessException
     */
    public ProfileDTO getProfileByUserId(String userId, LoadLocalProfileListener listener)
            throws BusinessException {
        try {
            if (listener != null) {
                ProfileDTO profileDTO = getProfileFromLocal(userId);
                listener.onLoadLocalProfile(profileDTO);
            }


            final JSONObject result = this.userServiceStub.getUserInfoByUserId(userId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                ProfileDTO profileDTO = new ProfileDTO(result.optJSONObject("result"));
                AVUser avUser = AVUser.getCurrentUser();
                if (avUser != null) {
                    if (TextUtils.equals(profileDTO.getUserId(), avUser.getObjectId())) {
                        updateLocalUserInfo(profileDTO);
                        // 保存心率值
                        SharedPreferences sp = context.getSharedPreferences(avUser.getObjectId(), 0);
                        sp.edit().putInt(Constants.PREF_USER_MAX_HEART_RATE_KEY, profileDTO.getMaxHeartRate()).apply();
                        logger.trace("Save to sp, userId = " + avUser.getObjectId() + ", heartRate = " + profileDTO.getMaxHeartRate());
                    }
                }
                return profileDTO;
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
     * New ResultApi v2.0
     *
     * @param userId
     * @return
     * @throws BusinessException
     */
    public UserDetailDTO getUserDetailByUserId(String userId)
            throws BusinessException {
        try {
            final JSONObject data = this.cyclingServiceStub.getActivityDataByUserId(userId);
            if (null == data) {
                return null;
            }

            int code = data.optInt("code");
            if (code == 0) {
                return new UserDetailDTO(data.optJSONObject("result"));
            } else {
                String message = data.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * New restful api v2.0
     *
     * @param userId User Id
     * @return UserStatisticDTO
     * @throws BusinessException
     */
    public UserStatisticDTO getUserStatisticDataByUserId(String userId)
            throws BusinessException {
        try {
            final JSONObject json = this.cyclingServiceStub.getActivityDetailDataByUserId(userId);
            if (null == json) {
                return null;
            }

            int code = json.optInt("code");
            if (code == 0) {
                return new UserStatisticDTO(json.optJSONObject("result"));
            }

            String message = json.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    public ProfileDTO updateRemoteUserInfo(ProfileDTO dto) throws BusinessException {
        try {
            if (null == dto)
                return null;
            JSONObject userObject = this.userServiceStub.updateUserInfo(dto.getNickname(),
                    dto.getSex() + "", (float) dto.getWeight(), (float) dto.getHeight(),
                    dto.getDistrict(), dto.getCity(), dto.getProvince(), dto.getBirthday(), dto.getAvatar());

            if (null == userObject || userObject.optInt("code") != 0)
                return null;

            JSONObject resultObj = userObject.optJSONObject("result");
            ProfileDTO retDto = null;

            if (resultObj != null) {
                retDto = new ProfileDTO(resultObj);
            }

            if (retDto != null) {
                LocalUser localUser = getLocalUser(retDto.getUserId());
                if (null != localUser) {
                    retDto.setFansNum(localUser.getFansNum());
                    retDto.setFollowNum(localUser.getFollowerNum());
                    retDto.setFollowStatu(localUser.getFollowStatus());
                    retDto.setMedalNum(localUser.getMedalNum());
                    updateLocalUserInfo(retDto);
                }
            }
            return retDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateLocalUserInfo(ProfileDTO dto) throws BusinessException {
        if (null == dto)
            return;
        RongCloudManager.getInstance()
                .setRongCloudUserInfo(dto);

        final LocalUser lu = new LocalUser();
        AVUser au = AVUser.getCurrentUser();
        if (au == null)
            return;
        lu.setId(dto.getUserId());
        lu.setEmail(au.getEmail());
        lu.setUsername(au.getUsername());
        lu.setNickname(dto.getNickname());
        lu.setProvince(dto.getProvince());
        lu.setCity(dto.getCity());
        lu.setGender(dto.getSex());
        lu.setDistrict(dto.getDistrict());
        lu.setWeight(dto.getWeight());
        lu.setHeight(dto.getHeight());
        lu.setTotalDistance(dto.getTotalDistance());
        //
        lu.setUserId(dto.getUserId());
        lu.setUserIntId(dto.getUserIntId());
        lu.setUpdatedAt(dto.getUpdatedAt());
        lu.setCreatedAt(dto.getCreatedAt());
        lu.setClubId(dto.getClubId());
        lu.setObjectId(dto.getObjectId());
        lu.setIsOk(dto.getIsOk());
        lu.setGridNum(dto.getGridNum());
        lu.setBirthday(dto.getBirthday());
        lu.setFansNum(dto.getFansNum());
        lu.setFollowerNum(dto.getFollowNum());
        lu.setFollowStatus(dto.getFollowStatu());
        lu.setSpeedxId(dto.getSpeedxId());

        if (dto.isEdited()) {
            lu.setEdited(1);
        } else {
            lu.setEdited(0);
        }
        lu.setWeeklyDistance(dto.getWeeklyDistance());
        lu.setSameGrid(dto.getSameNum());
        lu.setMonthlyDistance(dto.getMonthlyDistance());
        lu.setClubName(dto.getClubName());
        lu.setAvatar(dto.getAvatar());
        // v2.4.0
        lu.setMedalNum(dto.getMedalNum());

        au.setAvatar(dto.getAvatar());
        au.setDisplayName(dto.getNickname());
        au.setCity(dto.getCity());
        au.setWeight(dto.getWeight());

        //2.4.1
        au.setSpeedxId(dto.getSpeedxId());
        AVUser.saveCurrentUser(au);

        try {
            this.luDao.createOrUpdate(lu);
            final SharedPreferences sp = activity.getSharedPreferences(dto.getObjectId(), 0);
            sp.edit().putLong(Constants.PREF_UPDATE_USERINFO, System.currentTimeMillis()).commit();
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }


    }

    /**
     * 更新用户勋章数
     */
    public void updateUserMedalNum() {
        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            try {
                LocalUser localUser = getLocalUser(user.getObjectId());
                if (null != localUser) {
                    localUser.setMedalNum(localUser.getMedalNum() + 1);
                    createOrUpdate(localUser);
                    final SharedPreferences sp = activity.getSharedPreferences(user.getObjectId(), 0);
                    sp.edit().putLong(Constants.PREF_UPDATE_USERINFO, System.currentTimeMillis()).commit();
                }
            } catch (BusinessException e) {
            }
        }
    }

    public void createOrUpdate(LocalUser... users) throws BusinessException {
        try {
            this.luDao.createOrUpdate(users);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    public void deleteLocalUser(LocalUser localUser) throws BusinessException {
        if (localUser == null) return;
        try {
            this.luDao.delete(localUser);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    public boolean existInLocal(String userId) throws BusinessException {
        try {
            return null == this.luDao.get(userId) ? true : false;
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 获取俱乐部柱状图列表
     *
     * @param type   0:个人 1:俱乐部
     * @param userId type = 0, id = userId; type = 1, id = clubId
     * @param days   default = 30;
     * @return
     */
    public HistogramDTO getDiagram(int type, String userId, int days) {
        JSONObject result = null;
        if (type == 0) {
            result = this.userServiceStub.getUserDiagram(userId, days);
        } else if (type == 1) {
            result = this.userServiceStub.getClubDiagram(userId, days);
        }
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONObject json = result.optJSONObject("result");
            if (null == json) {
                return null;
            }

            return new HistogramDTO(json);
        }

        return null;
    }

    /**
     * 获取粉丝列表
     *
     * @param userId
     * @param page
     * @param count
     */
    public List<FriendDTO> getFansList(String userId, int page, int count) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }

        JSONObject result = this.userServiceStub.getFansList(userId, page, count);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONArray json = result.optJSONArray("result");
            if (null == json || json.length() <= 0) {
                return null;
            }

            List<FriendDTO> list = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.optJSONObject(i);
                if (null == obj) {
                    continue;
                }
                list.add(new FriendDTO(obj));
            }

            return list;
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }


    //写文件
    public File writeSDFile(String strContent) {
        String path3 = Environment.getExternalStorageDirectory().getPath() + File.separator + System.currentTimeMillis();
        File file = new File(path3);
        RandomAccessFile raf = null;
        try {
            if (!file.exists())
                file.createNewFile();
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (IOException e) {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public List<FriendDTO> seekFriends(int seekType, String thirdKey, final String thirdToken, List<SeekFriendDTO> list) {
        File file = null;
        if (list != null && list.size() > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                SeekFriendDTO seekFriendDTO = list.get(i);
                JSONArray seekArray = new JSONArray();
                seekArray.put(seekFriendDTO.getSeekValue());
                seekArray.put(seekFriendDTO.getNickName());
                array.put(seekArray);
            }

            file = writeSDFile(array.toString());
        }
        JSONObject result = null;
        if (file != null) {
            result = this.userServiceStub.seekFriendsWithFile(seekType, thirdKey, thirdToken, file);
        } else {
            result = this.userServiceStub.seekFriends(seekType, thirdKey, thirdToken);
        }
        if (null == result) {
            return null;
        }
        if (file != null && file.exists())
            file.delete();
        if (result.optInt("code") == 0) {
            JSONArray json = result.optJSONArray("result");
            if (null == json || json.length() <= 0) {
                return null;
            }
            List<FriendDTO> resultList = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.optJSONObject(i);
                if (null == obj) {
                    continue;
                }
                resultList.add(new FriendDTO(obj));
            }
            return resultList;
        }
        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    /**
     * 获取关注列表
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    public List<FriendDTO> getFollowList(String userId, int page, int count) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }

        JSONObject result = this.userServiceStub.getFollowList(userId, page, count);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONArray json = result.optJSONArray("result");
            if (null == json || json.length() <= 0) {
                return null;
            }

            List<FriendDTO> list = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.optJSONObject(i);
                if (null == obj) {
                    continue;
                }
                list.add(new FriendDTO(obj));
            }

            return list;
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }


    /**
     * 关注
     *
     * @param userId
     * @return
     */
    public boolean follow(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }

        JSONObject result = this.userServiceStub.follow(userId);
        if (null == result) {
            return false;
        }

        if (result.optInt("code") == 0) {
            boolean response = result.optBoolean("result");
            if (response) {
                SpeedxAnalytics.onEvent(context, "", "click_follow");
                AVUser user = AVUser.getCurrentUser();
                if (null != user) {
                    try {
                        LocalUser localUser = getLocalUser(user.getObjectId());
                        if (localUser != null) {
                            localUser.setFollowerNum(localUser.getFollowerNum() + 1);
                        }
                        this.createOrUpdate(localUser);
                        SharedPreferences sp = context.getSharedPreferences(user.getObjectId(), 0);
                        sp.edit().putLong(Constants.PREF_UPDATE_USERINFO, System.currentTimeMillis()).commit();
                    } catch (BusinessException e) {
                        return response;
                    }
                }
            }
            return response;
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return false;
    }

    /**
     * 取消关注
     *
     * @param userId
     * @return
     */
    public boolean unfollow(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }

        JSONObject result = this.userServiceStub.unfollow(userId);
        if (null == result) {
            return false;
        }

        if (result.optInt("code") == 0) {
            boolean response = result.optBoolean("result");
            if (response) {
                SpeedxAnalytics.onEvent(context, "", "click_cancel attention");
                AVUser user = AVUser.getCurrentUser();
                if (null != user) {
                    try {
                        LocalUser localUser = getLocalUser(user.getObjectId());
                        if (localUser != null) {
                            localUser.setFollowerNum(localUser.getFollowerNum() - 1);
                            this.createOrUpdate(localUser);
                            SharedPreferences sp = context.getSharedPreferences(user.getObjectId(), 0);
                            sp.edit().putLong(Constants.PREF_UPDATE_USERINFO, System.currentTimeMillis()).apply();
                        }
                    } catch (BusinessException e) {
                        return response;
                    }
                }
            }
            return response;
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }

        return false;
    }


    /**
     * 用户反馈
     *
     * @param content
     * @param contact
     * @param type    0是程序错误 1是产品建议
     * @param detail
     * @param logId
     * @return
     */
    public boolean feedback(String content, String contact, int type, String detail, String logId) throws
            BusinessException {
        if (type == 1) {
            logId = null;
            contact = null;
            content = null;
        }

        JSONObject result = this.userServiceStub.feedback(content, contact, type, detail, logId);
        if (null == result) {
            return false;
        }

        if (result.optInt("code") == 0) {
            return true;
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }

        return false;
    }

    /**
     * 获取勋章列表
     *
     * @param isHistory
     * @param page
     * @param count
     * @return
     */
    public List<MedalDTO> getBadgeList(int isHistory, int page, int count, String userId) {

        JSONObject result = this.userServiceStub.getBadgeList(isHistory, page, count, userId);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONArray json = result.optJSONArray("result");
            if (null == json || json.length() <= 0) {
                return null;
            }

            List<MedalDTO> list = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.optJSONObject(i);
                if (null == obj) {
                    continue;
                }
                list.add(new MedalDTO(obj));
            }

            return list;
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    /**
     * 获取勋章详情
     *
     * @param medalId
     * @return
     */
    public MedalDTO getBadgeInfo(int medalId, String userId) {
        JSONObject result = this.userServiceStub.getBadgeInfo(medalId, userId);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONObject json = result.optJSONObject("result");
            if (null != json) {
                return new MedalDTO(json);
            }
        }

        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    public void updateDeviceInfo(double lat, double lon, String deviceToken) throws BusinessException {
        JSONObject result = this.userServiceStub.updateDeviceInfo(lat, lon, deviceToken);
        if (result != null) {

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        }
    }

    /**
     * 获取用户每天里程数据
     *
     * @param userId
     * @param source
     * @param days
     * @return
     */
    public HistogramDTO getUserDiagramBySource(String userId, int source, int days) {
        JSONObject result = this.userServiceStub.getUserDiagramBySource(userId, source, days);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONObject json = result.optJSONObject("result");
            if (null == json) {
                return null;
            }

            return new HistogramDTO(json);
        }

        return null;
    }

    /**
     * 获取用户每天里程数据
     *
     * @param userId
     * @param centralId
     * @param days
     * @return
     */
    public HistogramDTO getUserDiagramByCentral(String userId, String centralId, int days) {
        JSONObject result = this.userServiceStub.getUserDiagramByCentral(userId, centralId, days);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONObject json = result.optJSONObject("result");
            if (null == json) {
                return null;
            }

            return new HistogramDTO(json);
        }

        return null;
    }

    /**
     * 获取中控总数据
     *
     * @param source
     * @return
     */
    public BleCyclingDTO getUserGoalInfoBySource(int source) {
        JSONObject result = this.userServiceStub.getUserGoalInfoBySource(source);
        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONObject json = result.optJSONObject("result");
            if (null == json) {
                return null;
            }

            return new BleCyclingDTO(json);
        }

        return null;
    }


    /**
     * 获取中控总数据
     *
     * @param centralId
     * @return
     */
    public BleCyclingDTO getUserGoalInfoByCentral(String centralId) {
        AVUser user = AVUser.getCurrentUser();
        if (user == null)
            return null;

        JSONObject result = this.userServiceStub.getUserGoalInfoByCentral(centralId, user.getObjectId());

        if (null == result) {
            return null;
        }

        if (result.optInt("code") == 0) {
            JSONObject json = result.optJSONObject("result");
            if (null == json) {
                return null;
            }

            return new BleCyclingDTO(json);
        }

        return null;
    }

    /**
     * 更新用户最大心率
     *
     * @param heartRate 心率
     * @param userId    用户ID
     * @return
     */
    public boolean updateUserHeartRate(String userId, int heartRate) {
        if (heartRate <= 0 || TextUtils.isEmpty(userId)) {
            return false;
        }

        JSONObject result = this.userServiceStub.updateUserInfo(heartRate);
        if (null == result || result.optInt("code") != 0)
            return false;

        JSONObject resultObj = result.optJSONObject("result");
        if (null != resultObj) {
            int cardiacRate = resultObj.optInt("cardiacRate");
            SharedPreferences sp = context.getSharedPreferences(userId, 0);
            sp.edit().putInt(Constants.PREF_USER_MAX_HEART_RATE_KEY, cardiacRate).apply();
            return true;
        }
        return false;
    }

    /**
     * 获取用户最大心率
     *
     * @param userId 用户ID
     * @return
     */
    public int getUserHeartRate(String userId) {
        SharedPreferences sp = context.getSharedPreferences(userId, 0);
        return sp.getInt(Constants.PREF_USER_MAX_HEART_RATE_KEY, 0);
    }

}
