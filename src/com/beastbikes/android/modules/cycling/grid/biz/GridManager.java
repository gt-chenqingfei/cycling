package com.beastbikes.android.modules.cycling.grid.biz;

import android.app.Activity;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.grid.dao.GridDao;
import com.beastbikes.android.modules.cycling.grid.dao.entity.Grid;
import com.beastbikes.android.modules.cycling.grid.dto.GridDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
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

/**
 * Created by icedan on 15/12/21.
 */
public class GridManager extends AbstractBusinessObject implements
        BusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(GridManager.class);

    private Activity activity;
    private GridDao gridDao;
    private GridServiceStub stub;

    public GridManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.activity = activity;
        final BeastBikes app = (BeastBikes) BeastBikes.getInstance().getApplicationContext();
        final ORMLitePersistenceManager pm = app
                .getPersistenceManager();
        this.gridDao = new GridDao((ORMLitePersistenceSupport) pm);

        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.stub = factory.create(GridServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(activity));
    }

    /**
     * 获取格子
     *
     * @param userId
     * @return List
     */
    public List<GridDTO> getUserGridList(final String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }

        try {
            JSONObject result = this.stub.getUserGridList(userId);
            if (null == result) {
                return getLocalGridsByUserId(userId);
            }

            if (result.optInt("code") == 0) {
                JSONArray array = result.optJSONArray("result");
                List<GridDTO> list = new ArrayList<>();
                List<Grid> grids = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    GridDTO dto = new GridDTO(array.optJSONObject(i), userId);
                    list.add(dto);
                    Grid grid = new Grid();
                    grid.setId(String.valueOf(dto.getGridId()));
                    grid.setCount(dto.getCount());
                    grid.setUserId(dto.getUserId());
                    grid.setUnlockAt(dto.getUnlockAt());
                    grids.add(grid);
                }

                if (null != grids && !grids.isEmpty()) {
                    try {
                        this.gridDao.createOrUpdate(grids);
                    } catch (Exception e) {
                        logger.error("Create or update grids is error");
                    }
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return getLocalGridsByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * query local grids by userId
     *
     * @param userId
     * @return list
     */
    public List<GridDTO> getLocalGridsByUserId(final String userId) {
        List<Grid> list = this.gridDao.getGridListByUserId(userId);
        if (null == list || list.isEmpty()) {
            return null;
        }

        List<GridDTO> grids = new ArrayList<>();
        for (Grid grid : list) {
            grids.add(new GridDTO(grid));
        }

        return grids;
    }

    /**
     * get local gridId by userId
     *
     * @param userId
     * @return
     */
    public List<Integer> getLocalGridIdsByUserId(final String userId) {
        List<Grid> list = this.gridDao.getGridListByUserId(userId);
        if (null == list || list.isEmpty()) {
            return null;
        }

        List<Integer> gridIds = new ArrayList<>();
        for (Grid grid : list) {
            gridIds.add(Integer.parseInt(grid.getId()));
        }

        return gridIds;
    }

    /**
     * Get grid by gridId
     *
     * @param gridId
     * @return GridDTO
     */
    public GridDTO getGrid(final String gridId) {
        try {
            Grid grid = this.gridDao.get(gridId);
            return new GridDTO(grid);
        } catch (PersistenceException e) {
            logger.error("Get grid by gridId is error, " + e);
            return null;
        }
    }

    /**
     * Save grid list
     *
     * @param grids
     */
    public void saveGrids(List<GridDTO> grids) {
        if (null == grids || grids.isEmpty()) {
            return;
        }

        List<Grid> list = new ArrayList<>();
        for (GridDTO dto : grids) {
            Grid grid = new Grid();
            grid.setId(String.valueOf(dto.getGridId()));
            grid.setUnlockAt(dto.getUnlockAt());
            grid.setCount(dto.getCount());
            grid.setUserId(dto.getUserId());
            list.add(grid);
        }

        try {
            this.gridDao.createOrUpdate(list);
        } catch (PersistenceException e) {
            logger.error("Create or update grid list is error, " + e);
        }
    }

    /**
     * Create gridId
     *
     * @param latitude
     * @param longitude
     * @return int
     */
    public int getGridId(double latitude, double longitude) {
        return (int) (Math.floor((longitude + 180) * 100.0) +
                Math.floor((latitude + 90) * 100.0) * 36000);
    }
}
