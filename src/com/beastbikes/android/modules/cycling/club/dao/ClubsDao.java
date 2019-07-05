package com.beastbikes.android.modules.cycling.club.dao;

import com.beastbikes.android.modules.cycling.club.dao.entity.Club;
import com.beastbikes.android.persistence.BeastStore.Clubs;
import com.beastbikes.android.persistence.BeastStore.Clubs.ClubsColumns;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClubsDao extends ORMLiteAccessObject<Club> implements ClubsColumns {

    private static final Logger logger = LoggerFactory
            .getLogger(ClubsDao.class);

    public ClubsDao(ORMLitePersistenceSupport ps) {
        super(ps, Club.class);
    }

    /**
     * 根据UserId查询Club信息
     *
     * @param userId
     * @return
     * @throws PersistenceException
     */
    public Club getMyClubInfo(String userId) throws PersistenceException {
        try {
            final List<Club> list = super.query("WHERE " + ClubsColumns.USER_ID
                    + "=?", userId);
            if (null == list || list.isEmpty())
                return null;

            return list.get(0);
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 根据ClubId删除Club信息
     *
     * @param clubId
     */
    public void deleteClub(final String clubId) {
        final StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(Clubs.CONTENT_CATEGORY)
                .append(" WHERE ");
        sql.append(ClubsColumns.CLUB_ID + "=?");
        try {
            this.execute(sql.toString(), new String[]{clubId});
            logger.trace("Delete club " + clubId + "success");
        } catch (PersistenceException e) {
            logger.error("Delete club " + clubId + " error");
        }
    }

    /**
     * 修改我与俱乐部关系
     *
     * @param clubInfo
     */
    public void updateClubStatus(Club clubInfo) throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Clubs.CONTENT_CATEGORY);
        sql.append(" SET ").append(ClubsColumns.STATUS).append("=")
                .append(clubInfo.getStatus());
        sql.append(" , ").append(ClubsColumns.LEVEL + "=")
                .append(clubInfo.getLevel());
        sql.append(" , ").append(ClubsColumns.CLUB_ID + "=?");
        sql.append(" WHERE ").append(ClubsColumns._ID).append("=?");

        try {
            this.execute(sql.toString(), new String[]{clubInfo.getClubId(),
                    clubInfo.getId()});
        } catch (PersistenceException e) {
            logger.error("UPDATE club status error", e);
        }
    }

}
