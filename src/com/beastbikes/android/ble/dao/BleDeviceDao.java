package com.beastbikes.android.ble.dao;

import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by icedan on 16/1/28.
 */
public class BleDeviceDao extends ORMLiteAccessObject<BleDevice> implements BeastStore.BleDevices.BleDevicesColumns {

    private static final Logger logger = LoggerFactory.getLogger(BleDeviceDao.class);

    public BleDeviceDao(ORMLitePersistenceSupport support) {
        super(support, BleDevice.class);
    }

    /**
     * 更新最后一次绑定时间
     *
     * @param deviceId
     * @param userId
     * @throws PersistenceException
     */
    public void updateLastBondTime(final String deviceId, final String userId)
            throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(BeastStore.BleDevices.CONTENT_CATEGORY);
        sql.append(" SET ").append(LAST_BIND_TIME).append("=")
                .append(System.currentTimeMillis() / 1000);
        sql.append(" WHERE ").append(DEVICE_ID).append("=? AND ");
        sql.append(USER_ID).append("=?");
        this.execute(sql.toString(), deviceId, userId);
    }

    /**
     * 更新ble device 设备信息
     * @param macAddress
     * @param deviceId
     * @param hardwareType
     * @param brandType
     * @param bikeImage
     * @param frameId
     */
    public void updateBleDeviceInfo(final String macAddress, final String deviceId, final int hardwareType, final int brandType, String bikeImage, String frameId) {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(BeastStore.BleDevices.CONTENT_CATEGORY);
        sql.append(" SET ").append(HARDWARE_TYPE).append("=")
                .append(hardwareType);
        sql.append(" , ").append(MAC_ADDRESS).append("=?");
        sql.append(" , ").append(BRAND_TYPE).append("=").append(brandType);
        sql.append(" , ").append(FRAME_ID).append("=?");
        sql.append(" , ").append(DEVICE_URL).append("=?");
        sql.append(" WHERE ").append(DEVICE_ID).append("=?");

        try {
            this.execute(sql.toString(), macAddress, frameId, bikeImage, deviceId);
            logger.error("Update ble device info is success");
        } catch (PersistenceException e) {
            logger.error("Update ble device info error, " + e);
        }
    }

    /**
     * 更新最后一次绑定时间
     *
     * @param deviceId
     * @param userId
     * @throws PersistenceException
     */
    public void updateDeviceName(final String deviceName, final String deviceId, final String userId)
            throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(BeastStore.BleDevices.CONTENT_CATEGORY);
        sql.append(" SET ").append(DEVICE_NAME).append("=? ");
        sql.append(" WHERE ").append(DEVICE_ID).append("=? AND ");
        sql.append(USER_ID).append("=?");
        this.execute(sql.toString(), deviceName, deviceId, userId);
    }

    /**
     * 查询蓝牙设备
     *
     * @param deviceId
     * @param userId
     * @return
     */
    public BleDevice queryBleDevice(final String deviceId, final String userId) {
        String sql = "WHERE " + DEVICE_ID + "=? AND " + USER_ID + "=?";
        try {
            List<BleDevice> devices = super.query(sql, deviceId, userId);
            if (null != devices && devices.size() > 0) {
                return devices.get(0);
            }

            return null;
        } catch (PersistenceException e) {
            logger.error("Query ble device by device id " + deviceId);
            return null;
        }
    }

    /**
     * 根据DeviceId查询蓝牙设备
     *
     * @param deviceId
     * @return
     */
    public BleDevice queryBleDeviceByDeviceId(final String deviceId) {
        String sql = "WHERE " + DEVICE_ID + "=?";
        try {
            List<BleDevice> devices = super.query(sql, deviceId);
            if (null != devices && devices.size() > 0) {
                return devices.get(0);
            }

            return null;
        } catch (PersistenceException e) {
            logger.error("Query ble device by device id " + deviceId);
            return null;
        }
    }

    /**
     * 查询蓝牙设备
     *
     * @param userId
     * @return
     */
    public List<BleDevice> queryBleDevice(final String userId) {
        String sql = "WHERE " + USER_ID + "=? order by " + LAST_BIND_TIME + " desc";
        try {
            return super.query(sql, userId);
        } catch (PersistenceException e) {
            logger.error("Query ble device by device id " + userId);
            return null;
        }
    }

}
