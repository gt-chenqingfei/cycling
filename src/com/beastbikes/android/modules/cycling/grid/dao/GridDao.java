package com.beastbikes.android.modules.cycling.grid.dao;

import com.beastbikes.android.modules.cycling.grid.dao.entity.Grid;
import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by icedan on 15/12/21.
 */
public class GridDao extends ORMLiteAccessObject<Grid> implements BeastStore.Grid.GirdColumns {

    private static final Logger logger = LoggerFactory.getLogger(GridDao.class);

    public GridDao(ORMLitePersistenceSupport ps) {
        super(ps, Grid.class);
    }

    /**
     * query grids by userId
     *
     * @param userId
     * @return list
     */
    public List<Grid> getGridListByUserId(final String userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("WHERE " + USER_ID).append(" =?");
        try {
            return super.query(sb.toString(), userId);
        } catch (PersistenceException e) {
            logger.error("Query grid by userId is error, " + e);
            return null;
        }
    }

    /**
     * update grid count
     *
     * @param gridId
     * @param count
     */
    public void updateGrid(final int gridId, final int count) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(BeastStore.Grid.CONTENT_CATEGORY);
        sql.append(" SET ").append(COUNT).append("=");
        sql.append(count).append(" WHERE ").append(_ID).append("=?");
        try {
            super.execute(sql.toString(), new String[]{String.valueOf(gridId)});
        } catch (PersistenceException e) {
            logger.error("Update grid count is error, " + e);
        }
    }

}
