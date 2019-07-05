package com.beastbikes.android.modules.social.im.biz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.social.im.dao.FriendDao;
import com.beastbikes.android.modules.social.im.dao.entity.Friend;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.business.BusinessObject;
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

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class FriendManager extends AbstractBusinessObject implements
        BusinessObject {

    private static final Logger logger = LoggerFactory
            .getLogger(FriendManager.class);

    private final FriendDao dao;
    private final FriendServiceStub stub;
    private Activity activity;
    private SharedPreferences sp;

    public FriendManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        final BeastBikes app = (BeastBikes) activity.getApplicationContext();
        final ORMLitePersistenceManager pm = app
                .getPersistenceManager();
        this.dao = new FriendDao((ORMLitePersistenceSupport) pm);
        this.activity = activity;
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.stub = factory.create(FriendServiceStub.class, RestfulAPI.BASE_URL, RestfulAPI.getParams(activity));
        if (AVUser.getCurrentUser() != null) {
            this.sp = activity.getSharedPreferences(AVUser.getCurrentUser().getObjectId(), 0);
        }
    }

    public FriendManager(Context activity) {
        super((BusinessContext) activity.getApplicationContext());
        final BeastBikes app = (BeastBikes) activity.getApplicationContext();
        final ORMLitePersistenceManager pm = app
                .getPersistenceManager();
        this.dao = new FriendDao((ORMLitePersistenceSupport) pm);
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.stub = factory.create(FriendServiceStub.class, RestfulAPI.BASE_URL, RestfulAPI.getParams(activity));
        if (AVUser.getCurrentUser() != null) {
            this.sp = activity.getSharedPreferences(AVUser.getCurrentUser().getObjectId(), 0);
        }
    }

    /**
     * 保存好友信息
     *
     * @param userId
     * @param fd
     */
    public void saveFriend(final String userId, final FriendDTO fd) {
        Friend friend = new Friend();
        friend.setId(userId + ":" + fd.getFriendId());
        friend.setUserId(userId);
        friend.setFriendId(fd.getFriendId());
        friend.setAvatar(fd.getAvatar());
        friend.setNickname(fd.getNickname());
        friend.setStatus(0);
        friend.setCreateTime(fd.getCreateTime());
        friend.setRemarks(fd.getRemarks());
        try {
            this.dao.createOrUpdate(friend);
        } catch (PersistenceException e) {
            logger.error("save friend error");
        }
    }

    /**
     * 获取好友列表
     *
     * @param userId User Id
     * @return List
     */
    public List<FriendDTO> getFriends(final String userId) {
        try {
            List<Friend> friends = this.dao.queryFriends(userId);
            if (null == friends || friends.isEmpty()) {
                return null;
            }

            List<FriendDTO> list = new ArrayList<FriendDTO>();
            for (Friend friend : friends) {
                FriendDTO fd = new FriendDTO();
                fd.setFriendId(friend.getFriendId());
                fd.setNickname(friend.getNickname());
                fd.setAvatar(friend.getAvatar());
                fd.setRemarks(friend.getRemarks());
                list.add(fd);
            }

            return list;
        } catch (PersistenceException e) {
            logger.error("query friend error");
        }

        return null;
    }

    /**
     * 存储好友到数据库
     *
     * @param list
     */
    public void saveFriends(List<FriendDTO> list) {
        AVUser user = AVUser.getCurrentUser();
        if(user == null)
            return;

        String userId = AVUser.getCurrentUser().getObjectId();
        List<Friend> friends = new ArrayList<Friend>();
        for (FriendDTO fd : list) {
            Friend friend = new Friend();
            friend.setId(userId + ":" + fd.getFriendId());
            friend.setUserId(userId);
            friend.setFriendId(fd.getFriendId());
            friend.setNickname(fd.getNickname());
            friend.setAvatar(fd.getAvatar());
            friend.setRemarks(fd.getRemarks());
            friend.setStatus(0);
            friend.setCreateTime(fd.getCreateTime());
            friends.add(friend);
        }
        try {
            this.dao.createOrUpdate(friends);
        } catch (PersistenceException e) {
            logger.error("create or update friend error");
        }
    }

    /**
     * 查询本地好友的关系
     *
     * @param friendId
     * @return
     */
    public FriendDTO queryLocalFriendDTO(final String friendId) {
        if (TextUtils.isEmpty(friendId)) {
            return null;
        }

        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return null;
        }

        try {
            Friend friend = this.dao.get(user.getObjectId() + ":" + friendId);
            if (null == friend) {
                return null;
            }

            FriendDTO dto = new FriendDTO();
            dto.setNickname(friend.getNickname());
            dto.setFriendId(friend.getFriendId());
            dto.setRemarks(friend.getRemarks());
            if (dto.getStatus() == 0) {
                dto.setStatus(FriendDTO.FRIEND_STATUS_FOLLOW_AND_FANS);
            } else {
                dto.setStatus(FriendDTO.FRIEND_STATUS_ADD);
            }
            dto.setAvatar(friend.getAvatar());

            return dto;
        } catch (PersistenceException e) {
            logger.error("Query friend by id = " + user.getObjectId() + ":" + friendId + " error, " + e);
            return null;
        }
    }

    /**
     * 查询好友信息
     *
     * @param userId
     * @param friendId
     * @return
     */
    public FriendDTO queryFriendDTO(final String userId, final String friendId) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(friendId)) {
            return null;
        }

        Friend friend = this.dao.queryFriend(userId, friendId);
        if (null != friend) {
            FriendDTO fd = new FriendDTO();
            fd.setFriendId(friend.getFriendId());
            fd.setAvatar(friend.getAvatar());
            fd.setNickname(friend.getNickname());
            fd.setRemarks(friend.getRemarks());
            return fd;
        }

        return null;
    }

    /**
     * 删除好友
     *
     * @param friendId
     */
    public void deleteFriend(final String friendId) {
        if (TextUtils.isEmpty(friendId)) {
            return;
        }

        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        try {
            this.dao.delete(user.getObjectId() + ":" + friendId);
        } catch (PersistenceException e) {
            logger.trace("delete friend " + friendId + " is error");
        }
    }

    /**
     * 更新好友关系
     *
     * @param friendId
     */
    public void updateFriendStatus(final String friendId) {
        if (TextUtils.isEmpty(friendId)) {
            return;
        }

        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        String objectId = user.getObjectId() + ":" + friendId;
        try {
            Friend friend = this.dao.get(objectId);
            if (null == friend) {
                return;
            }

            friend.setStatus(1);
            this.dao.createOrUpdate(friend);
        } catch (PersistenceException e) {
            logger.error("update friend status to 1 error");
        }
    }

    /**
     * （慎用）修改好友关系为非好友
     */
    public void updateFriends2Stranger() {
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        this.dao.updateFriends2Stranger(user.getObjectId());
    }

    /**
     * Add Friend
     *
     * @param userId Friend's User Id
     * @param extra  Extra
     * @return boolean
     */
    public boolean addFriend(final String userId, final String extra) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }

        try {
            final JSONObject result = this.stub.addFriend(userId, extra);
            if (null == result) {
                return false;
            }

            return result.optBoolean("result");
        } catch (Exception e) {
            logger.error("Add friend by " + userId + " is error");
        }
        return false;
    }

    /**
     * Check Friend Requests count
     *
     * @param lastTime the last query time
     * @return int
     */
    public int checkFriendRequestsCount(final long lastTime) {
        try {
            final JSONObject result = this.stub.checkFriendRequestsCount(lastTime);
            if (null == result) {
                return 0;
            }

            return result.optInt("result");
        } catch (Exception e) {
            logger.error("Check friend requests count is error");
        }

        return 0;
    }

    /**
     * Check Friend status
     *
     * @param userId Friend's User Id
     * @return JSONObject
     */
    public JSONObject checkFriendStatus(final String userId) {
        try {
            final JSONObject result = this.stub.checkFriendStatus(userId);
            if (null == result) {
                return null;
            }

            if (result.optInt("code") == 0) {
                return result.optJSONObject("result");
            }
        } catch (Exception e) {
            logger.error("Check friend status is error by " + userId);
        }

        return null;
    }

    /**
     * Clear friends requests
     *
     * @return boolean
     */
    public boolean cleanFriendRequests() {
        try {
            final JSONObject result = this.stub.cleanFriendRequests();
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0 && result.optBoolean("result")) {
                return true;
            }
        } catch (Exception e) {
            logger.error("clean friend requests is error");
        }

        return false;
    }

    /**
     * Command friend request   0 通过, 1 拒绝
     *
     * @param requestId request id
     * @param command   command
     * @return boolean
     */
    public boolean friendRequestCmd(final int requestId, final int command) {
        try {
            final JSONObject result = this.stub.friendRequestCmd(requestId, command);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0 && result.optBoolean("result")) {
                return true;
            }
        } catch (Exception e) {
            logger.error("clean friend requests is error");
        }

        return false;
    }

    /**
     * Get friend request list
     *
     * @param page  Page
     * @param count Count
     * @return List
     */
    public List<FriendDTO> friendRequestsList(final int page, final int count) {
        try {
            final JSONObject result = this.stub.friendRequestsList(page, count);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONArray array = result.optJSONArray("result");
                if (null == array || array.length() <= 0) {
                    return null;
                }

                List<FriendDTO> friends = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    friends.add(new FriendDTO(array.optJSONObject(i)));
                }

                return friends;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return null;
        } catch (Exception e) {
            logger.error("Get friend request list error");
        }

        return null;
    }

    /**
     * Get firends list
     *
     * @param userId User Id
     * @param page   Page
     * @param count  Count
     * @return List
     */
    public List<FriendDTO> friendsList(final String userId, final int page, final int count) {
        try {
            final JSONObject result = this.stub.friendsList(userId, page, count);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                JSONArray array = result.optJSONArray("result");
                if (null == array || array.length() <= 0) {
                    return null;
                }

                final List<FriendDTO> friends = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    friends.add(new FriendDTO(array.optJSONObject(i)));
                }

                this.saveFriends(friends);

                return friends;
            }

            return null;
        } catch (Exception e) {
            logger.error("Get friend list is error");
        }

        return null;
    }

    /**
     * Search friend by nick name
     *
     * @param keyName nick name
     * @param page    page
     * @param count   count
     * @return List
     */
    public List<FriendDTO> searchUserByNickname(final String keyName, final int page, final int count) {
        if (TextUtils.isEmpty(keyName)) {
            return null;
        }

        try {
            final JSONObject result = this.stub.searchUserByNickname(keyName, page, count);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                final JSONArray array = result.optJSONArray("result");
                if (null == array || array.length() <= 0) {
                    return null;
                }

                final List<FriendDTO> friends = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    friends.add(new FriendDTO(array.optJSONObject(i)));
                }

                return friends;
            }


        } catch (Exception e) {
            logger.error("Search friend by " + keyName + " is error");
        }

        return null;
    }

    /**
     * 解除好友关系
     *
     * @param userId User Id
     * @return boolean
     */
    public boolean unfollow(final String userId) {
        try {
            final JSONObject result = this.stub.unfollow(userId);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0 && result.optBoolean("result")) {
                deleteFriend(userId);
                return true;
            }
        } catch (Exception e) {
            logger.error("Unfollow friend is error");
        }

        return false;
    }

    /**
     * Get chat token
     *
     * @return String
     */
    public String getChatToken() {
        try {
            final JSONObject result = this.stub.getChatToken();
            if (null == result) {
                return "";
            }

            if (result.optInt("code") == 0) {
                final JSONObject obj = result.optJSONObject("result");
                if (null == obj) {
                    return "";
                }

                return obj.optString("token");
            }
        } catch (Exception e) {
            logger.error("Get chat token is error");
        }

        return "";
    }

    /**
     * 修改备注
     *
     * @param target_user_id
     * @param remarks
     * @return
     */
    public JSONObject updateSocialInfo(String target_user_id, String remarks) {
        try {
            JSONObject result = this.stub.updateSocialInfo(target_user_id, remarks);
            return result;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 解除好友关系
     *
     * @param nickName User Id
     * @param clubId
     * @return boolean
     */
    public boolean setClubChatNick(final String nickName, final String clubId) throws BusinessException {
        try {
            this.sp.edit().putString(clubId, nickName).commit();

            AVUser user = AVUser.getCurrentUser();
            if (user != null) {
                UserInfo info = new UserInfo(user.getObjectId(), nickName, Uri.parse(user.getAvatar()));
                RongIM.getInstance().setCurrentUserInfo(info);
                RongIM.getInstance().refreshUserInfoCache(info);
            }
            final JSONObject result = this.stub.setClubChatNick(clubId, nickName);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("setClubChatNick is error " + e.toString());
        }

        return false;
    }


    /**
     * getClubChatNick
     *
     * @return String
     */
    public String getClubChatNick(final String userId, final String clubId) throws BusinessException {
        try {
            final JSONObject result = this.stub.getClubChatNick(clubId, userId);

            if (null == result) {
                return "";
            }

            if (result.optInt("code") == 0) {
                final JSONObject obj = result.optJSONObject("result");
                if (null == obj) {
                    return "";
                }

                String chatNick = obj.optString("clubChatNick");
                this.sp.edit().putString(clubId, chatNick).commit();

                return obj.optString("clubChatNick");
            }
        } catch (Exception e) {
            logger.error("Get chat token is error");
        }

        return "";
    }

    /**
     * getChatInfoById
     *
     * @return ProfileDTO
     */
    public ProfileDTO getUserInfoById(final String userId) throws BusinessException {
        try {
            final JSONObject result = this.stub.getChatInfoByIds(userId);

            if (null == result) {
                return null;
            }

            if (result.optInt("code") == 0) {
                final JSONObject obj = result.optJSONObject("result");

                if (null == obj) {
                    return null;
                }

                JSONObject userInfo = obj.optJSONObject(userId);
                if (userInfo != null) {

                    ProfileDTO info = new ProfileDTO(userInfo.optString("uid"),
                            userInfo.optString("nickname"),userInfo.optString("avatar"));
                    return info;
                }
            }
        } catch (Exception e) {
            logger.error("Get chat token is error");
        }

        return null;
    }

}
