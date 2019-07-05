package com.beastbikes.android.modules.social.im.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beastbikes.android.modules.social.im.dao.entity.Friend;
import com.beastbikes.android.persistence.BeastStore.Friend.FriendColumns;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;

public class FriendDao extends ORMLiteAccessObject<Friend> implements
        FriendColumns {

    private static final Logger logger = LoggerFactory
            .getLogger(FriendDao.class);

    public FriendDao(ORMLitePersistenceSupport ps) {
        super(ps, Friend.class);
    }

    /**
     * 根据UserId查询Friend信息
     *
     * @param userId
     * @return
     * @throws PersistenceException
     */
    public List<Friend> queryFriends(String userId) throws PersistenceException {
        return super.query("WHERE " + USER_ID + "=? AND " + STATUS
                + "=0 ORDER BY " + CREATE_TIME + " DESC", userId);
    }

    /**
     * 查询好友信息
     *
     * @param userId
     * @param friendId
     * @return
     */
    public Friend queryFriend(final String userId, final String friendId) {
        try {
            List<Friend> list = super.query("WHERE " + USER_ID + "=? AND "
                    + FRIEND_ID + "=?", userId, friendId);
            if (null != list && !list.isEmpty()) {
                return list.get(0);
            }

            return null;
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 删除好友
     *
     * @param friendId
     */
    public void deleteFriend(final String userId, final String friendId) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(
                com.beastbikes.android.persistence.BeastStore.Friend.CONTENT_CATEGORY);
        sql.append(" WHERE ").append(_ID).append("=?");

        try {
            this.execute(sql.toString(),
                    new String[]{userId + ":" + friendId});
            logger.trace("Delete friend " + friendId + "succes");
        } catch (PersistenceException e) {
            logger.error("Delete friend " + friendId + " error");
        }
    }

    /**
     * (慎用) 修改所有好友关系为非好友
     *
     * @param userId
     */
    public void updateFriends2Stranger(final String userId) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(
                com.beastbikes.android.persistence.BeastStore.Friend.CONTENT_CATEGORY);
        sql.append(" SET ").append(STATUS).append("=").append(1);
        sql.append(" WHERE ").append(USER_ID).append("=?");

        try {
            this.execute(sql.toString(), new String[]{userId});
            logger.error("Update friend status to 1 success");
        } catch (PersistenceException e) {
            logger.error("Update friend status to 1 error");
        }
    }

}
