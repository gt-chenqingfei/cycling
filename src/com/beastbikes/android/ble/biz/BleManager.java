package com.beastbikes.android.ble.biz;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.dao.BleDeviceDao;
import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.ble.protocol.v1.CyclingActivityCharacteristic;
import com.beastbikes.android.ble.protocol.v1.CyclingSampleCharacteristic;
import com.beastbikes.android.ble.protocol.v1.PreviewDataCharacteristic;
import com.beastbikes.android.ble.protocol.v1.SynchronizationDataCharacteristic;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityState;
import com.beastbikes.android.modules.cycling.activity.dao.LocalActivityDao;
import com.beastbikes.android.modules.cycling.activity.dao.LocalActivitySampleDao;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.activity.util.ActivityType;
import com.beastbikes.android.modules.cycling.activity.util.CalorieCalculator;
import com.beastbikes.android.utils.JSONUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by icedan on 15/12/4.
 */
public class BleManager extends AbstractBusinessObject implements
        BusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(BleManager.class);

    private LocalActivityDao laDao;
    private LocalActivitySampleDao lasDao;
    private BleDeviceDao bdDao;
    private ActivityManager activityManager;
    private BleStub stub;
    private Context context;
    private boolean saveDeviceToServerLock = false;

    public BleManager(Activity context) {
        super((BusinessContext) context.getApplicationContext());
        this.context = context;
        final BeastBikes app = (BeastBikes) BeastBikes.getInstance().getApplicationContext();
        final ORMLitePersistenceManager pm = app.getPersistenceManager();

        final RestfulAPIFactory factory = new RestfulAPIFactory(context);

        this.laDao = new LocalActivityDao((ORMLitePersistenceSupport) pm);
        this.lasDao = new LocalActivitySampleDao((ORMLitePersistenceSupport) pm);
        this.bdDao = new BleDeviceDao((ORMLitePersistenceSupport) pm);
        this.stub = factory.create(BleStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
        this.activityManager = new ActivityManager(context);
    }

    public BleManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        final BeastBikes app = (BeastBikes) BeastBikes.getInstance().getApplicationContext();
        final ORMLitePersistenceManager pm = app
                .getPersistenceManager();
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.laDao = new LocalActivityDao((ORMLitePersistenceSupport) pm);
        this.lasDao = new LocalActivitySampleDao((ORMLitePersistenceSupport) pm);
        this.bdDao = new BleDeviceDao((ORMLitePersistenceSupport) pm);
        this.stub = factory.create(BleStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
        this.activityManager = new ActivityManager(context);
    }

    /**
     * 保存预览数据
     *
     * @param characteristic
     * @param deviceId
     */
    public void savePreviewActivity(PreviewDataCharacteristic characteristic, String deviceId, String macAddress) {
        if (null == characteristic || TextUtils.isEmpty(deviceId)) {
            return;
        }

        final CyclingActivityCharacteristic activity = characteristic.getActivity();
        if (null == activity) {
            return;
        }

        logger.info("中控返回的预览数据：Characteristic = " + activity.toString());

        LocalActivity localActivity = this.laDao.getBleLocalActivity((long) activity.getStopTime() * 1000, deviceId);
        if (null != localActivity) {
            logger.info("保存预览数据 savePreviewActivity = " + localActivity.toString());
            return;
        }

        final LocalActivity cycling = new LocalActivity();
        final String activityId = "ble_" + UUID.randomUUID().toString();
        cycling.setId(activityId);
        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            cycling.setUserId(user.getObjectId());
            cycling.setUsername(user.getUsername());
            BleDevice device = getBleDevice(deviceId, user.getObjectId());
            String brandType = BleDevice.brandType2String(device.getBrandType());
            cycling.setSource(brandType);
            cycling.setCentralName(device.getDeviceName());
        }
        cycling.setDeviceId(deviceId);
        cycling.setCentralId(macAddress);
        cycling.setType(ActivityType.CYCLING.ordinal());
        cycling.setStartTime((long) activity.getStartTime() * 1000);
        cycling.setFinishTime((long) activity.getStopTime() * 1000);
        cycling.setSampleRate(activity.getSampleRate());
        double totalDistance = activity.getTotalDistance();
        cycling.setTotalDistance(totalDistance);
        cycling.setTotalElapsedTime(activity.getTotalTime());
        cycling.setSampleCount(activity.getSampleCount());
        cycling.setBleDataType(activity.getSyncDataType());
        double climbHeight = activity.getClimbHeight();
        cycling.setTotalRisenAltitude(climbHeight);
        // 爬坡距离
        double uphillDistance = Math.sqrt((totalDistance * totalDistance)
                + (climbHeight * climbHeight));
        cycling.setTotalUphillDistance(uphillDistance);
        cycling.setSynced(false);
        cycling.setCoordinate("GPS");
        cycling.setState(0);

        try {
            final CyclingSampleCharacteristic[] samples = characteristic.getSamples();
            if (null == samples || samples.length <= 0) {
                return;
            }

            cycling.setMaxCadence(samples[samples.length - 1].getMaxCadence());
            cycling.setMaxCardiacRate(samples[samples.length - 1].getMaxHeartRate());
            this.laDao.createOrUpdate(cycling);
            logger.info("保存预览数据 Create or update ble cycling activityId = " + activityId + " success," +
                    " LocalActivity = " + cycling.toString());

            this.saveBleSamples(samples, activityId);
        } catch (PersistenceException e) {
            logger.error("保存预览数据 Create or update ble cycling activityId = " + activityId + " error, " + e);
        }
    }

    /**
     * 保存中控的骑行记录打点
     *
     * @param characteristics
     * @param activityId
     */
    public void saveBleSamples(CyclingSampleCharacteristic[] characteristics, String activityId) {
        if (null == characteristics || characteristics.length <= 0) {
            return;
        }

        final List<LocalActivitySample> samples = new ArrayList<>();
        for (int i = 0; i < characteristics.length; i++) {
            final CyclingSampleCharacteristic characteristic = characteristics[i];
            final LocalActivitySample sample = new LocalActivitySample();
            sample.setId(UUID.randomUUID().toString());
            AVUser user = AVUser.getCurrentUser();
            if (null != user) {
                sample.setUserId(user.getObjectId());
            }
            sample.setElapsedTime(characteristic.getTimestamp());
            sample.setTime(i);
            sample.setActivityId(activityId);
            sample.setLatitude0("0");
            sample.setLongitude0("0");
            double latitude = characteristic.getLatitude();
            double longitude = characteristic.getLongitude();
            if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < 180) {
                continue;
            }
            sample.setLatitude1(String.valueOf(latitude));
            sample.setLongitude1(String.valueOf(longitude));
            sample.setAltitude(String.valueOf(characteristic.getAltitude()));
            sample.setVelocity((characteristic.getSpeed() / 100) * 3.6);
            sample.setMaxSpeed((characteristic.getMaxSpeed() / 100) * 3.6);
            sample.setDistance(characteristic.getDistance());
            sample.setCurrTime(characteristic.getTimestamp());
            sample.setCadence(characteristic.getCadence());
            sample.setMaxCadence(characteristic.getMaxCadence());
            sample.setCardiacRate(characteristic.getHeartRate());
            sample.setMaxCardiacRate(characteristic.getMaxHeartRate());
            sample.setSynced(false);
            sample.setOrdinal(i);
            samples.add(sample);
        }

        try {
            this.lasDao.createOrUpdate(samples);
            logger.info("Create or update ble activity sample activityId = " + activityId + " success");
        } catch (PersistenceException e) {
            logger.error("Create or update ble activity sample activityId = " + activityId + " error, " + e);
        }
    }

    // 打点频率
    private int sampleRate = 10;
    private double lastSampleDistance = 0;
    private double totalCalorie = 0;
    private double totalUphillDistance = 0;
    private double lastAltitude = 0;
    private double maxAltitude = 0;
    private double maxSpeed = 0;
    private long startTime = 0;
    private long stopTime = 0;
    private int maxHeartRate = 0;
    private int maxCadence = 0;

    /**
     * 保存同步数据打点
     *
     * @param activityId
     */
    public void saveBleSamples(String activityId, SynchronizationDataCharacteristic syncCharacteristic) {
        if (TextUtils.isEmpty(activityId)) {
            logger.info("保存同步数据Sample点activityId为null ");
            return;
        }
        if (null == syncCharacteristic) {
            logger.info("保存同步数据Sample点characteristic为null");
            return;
        }

        CyclingSampleCharacteristic[] characteristics = syncCharacteristic.getSamples();
        if (null == characteristics || characteristics.length <= 0) {
            return;
        }

        int packetIndex = syncCharacteristic.getCurrentPacketIndex();
        int packetCount = syncCharacteristic.getTotalPacketCount();
        if (packetIndex == 0) {
            this.lastSampleDistance = 0;
            this.totalCalorie = 0;
            this.totalUphillDistance = 0;
            this.maxAltitude = 0;
            this.lastAltitude = 0;
            this.maxSpeed = 0;
            this.startTime = 0;
            this.stopTime = 0;
            try {
                final LocalActivity activity = this.laDao.get(activityId);
                // bleDataType = 2表示已经通过完成，因此不需要再次进行数据操作
                if (null == activity || activity.getBleDataType() == 2) {
                    return;
                }
                sampleRate = activity.getSampleRate();
                this.stopTime = activity.getFinishTime();
            } catch (PersistenceException e) {
            }
        }

        int size = characteristics.length;
        int ordinal = packetIndex * size;
        final List<LocalActivitySample> samples = new ArrayList<>();
        for (int i = 0; i < characteristics.length; i++) {
            if ((i == 0 && packetIndex == 0) || (i == characteristics.length - 1 &&
                    packetIndex == packetCount - 1)) {
                // 第一个打点和最后一个打点不保留
                continue;
            }

            final CyclingSampleCharacteristic characteristic = characteristics[i];
            if (null == characteristic) {
                return;
            }
            final LocalActivitySample sample = new LocalActivitySample();
            AVUser user = AVUser.getCurrentUser();
            if (null != user) {
                sample.setUserId(user.getObjectId());
            }
            sample.setId(UUID.randomUUID().toString());
            sample.setActivityId(activityId);
            sample.setLatitude0("0");
            sample.setLongitude0("0");
            double latitude = characteristic.getLatitude();
            double longitude = characteristic.getLongitude();
            if (latitude > 1000 || latitude < -1000 || longitude > 1000 || longitude < -1000) {
                continue;
            }
            sample.setLatitude1(String.valueOf(latitude));
            sample.setLongitude1(String.valueOf(longitude));
            sample.setAltitude(String.valueOf(characteristic.getAltitude()));
            sample.setVelocity(characteristic.getSpeed() * 3.6);
            sample.setMaxSpeed(characteristic.getMaxSpeed() * 3.6);
            sample.setDistance(characteristic.getDistance());
            sample.setCurrTime(characteristic.getTimestamp());
            sample.setCadence(characteristic.getCadence());
            this.maxCadence = Math.max(this.maxCadence, characteristic.getCadence());
            this.maxHeartRate = Math.max(this.maxHeartRate, characteristic.getHeartRate());
            sample.setMaxCadence(characteristic.getMaxCadence());
            sample.setCardiacRate(characteristic.getHeartRate());
            sample.setMaxCardiacRate(characteristic.getMaxHeartRate());
            sample.setSynced(false);
            sample.setOrdinal(ordinal + i);
            sample.setTime(ordinal + i);
            sample.setElapsedTime(characteristic.getTimestamp());
            samples.add(sample);

            logger.trace("中控同步数据打点：Sample = " + sample.toString());

            double dis = characteristic.getDistance() - lastSampleDistance;
            this.lastSampleDistance = characteristic.getDistance();
            this.totalCalorie += getCalorie(this.sampleRate, dis);
            double altitude = characteristic.getAltitude();
            this.maxAltitude = Math.max(altitude, this.maxAltitude);
            double diffAltitude = altitude - lastAltitude;
            if (diffAltitude > 0 && diffAltitude < 2.5) {
                // 爬坡距离
                double uphillDistance = Math.sqrt((dis * dis) + (diffAltitude * diffAltitude));
                this.totalUphillDistance = this.totalUphillDistance + uphillDistance;
            }
            this.lastAltitude = altitude;
            this.maxSpeed = Math.max(sample.getMaxSpeed(), this.maxSpeed);
            if (packetIndex == 0 && i == 1) {
                this.startTime = (long) characteristic.getTimestamp() * 1000;
                logger.info("同步数据，记录开始时间：" + startTime);
            }

            long finishTime = (long) characteristic.getTimestamp() * 1000;
            if (finishTime > stopTime && packetIndex == packetCount - 1) {
                this.stopTime = finishTime;
                logger.info("同步数据，记录结束时间：" + stopTime);
            }
        }

        try {
            this.lasDao.createOrUpdate(samples);
            logger.info("Create or update ble activity sample activityId = " + activityId + " success");
        } catch (PersistenceException e) {
            logger.error("Create or update ble activity sample activityId = " + activityId + " error", e);
        }

        if (packetIndex == packetCount - 1) {
            try {
                final LocalActivity activity = this.laDao.get(activityId);
                activity.setState(ActivityState.STATE_COMPLETE);
                activity.setTotalCalorie(this.totalCalorie);
                activity.setMaxVelocity(this.maxSpeed);
                activity.setMaxAltitude(this.maxAltitude);
                activity.setMaxCadence(this.maxCadence);
                activity.setMaxCardiacRate(this.maxHeartRate);
                activity.setBleDataType(2);
                if (startTime != 0) {
                    activity.setStartTime(startTime);
                }
                if (stopTime != 0) {
                    activity.setFinishTime(stopTime);
                }
                activity.setTotalUphillDistance(totalUphillDistance);
                this.laDao.createOrUpdate(activity);

                if (null != this.activityManager) {
                    this.activityManager.saveSamples(activity);
                }

                this.totalCalorie = 0;
                this.lastSampleDistance = 0;
                this.maxSpeed = 0;
                this.maxAltitude = 0;
                this.maxCadence = 0;
                this.maxHeartRate = 0;
                logger.info("Create or update ble activity ble data type success, LocalActivity = " + activity.toString());
            } catch (PersistenceException e) {
                logger.error("Create or update ble activity ble data type error, ", e);
            } catch (BusinessException e) {
                logger.error("Upload activity to cloud error, activityId = " + activityId + " error, ", e);
            }
        }
    }

    private double getCalorie(int sampleRate, double distance) {
        final double speedInverse = sampleRate / (distance / 1000);
        final double k = CalorieCalculator.getCoefficient(ActivityType.CYCLING,
                speedInverse / 60);
        AVUser user = AVUser.getCurrentUser();
        double weight = 65;
        if (null != user && user.getWeight() > 0) {
            weight = user.getWeight();
        }
        return CalorieCalculator.calculate(weight,
                sampleRate / 3600f, k);
    }


    /**
     * 获取中控未同步的骑行记录
     *
     * @param userId
     * @param centralId
     * @return
     */
    public List<LocalActivity> getUnSyncLocalActivitiesByCentralId(final String userId, final String centralId) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(centralId)) {
            return null;
        }
        return this.laDao.getUnSyncBleLocalActivitiesByCentralId(userId, centralId);
    }

    /**
     * 保存已绑定的蓝牙设备信息
     *
     * @param session
     */
    public void createOrUpdateBleDevice(CentralSession session) {
        if (null == session) {
            return;
        }

        final AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        BluetoothDevice device = session.getBluetoothDevice();
        if (device == null) {
            return;
        }

        final BleDevice bleDevice = new BleDevice();
        bleDevice.setDeviceName(device.getName());
        bleDevice.setDeviceId(device.getAddress());
        bleDevice.setLastBindTime(System.currentTimeMillis() / 1000);
        bleDevice.setUserId(user.getObjectId());
        bleDevice.setMacAddress(session.getCentralId());
        bleDevice.setHardwareType(session.getHdType());
        final BleDevice queryDevice = bdDao.queryBleDevice(device.getAddress(), user.getObjectId());
        if (null == queryDevice) {
            bleDevice.setId(UUID.randomUUID().toString());
        } else {
            bleDevice.setId(queryDevice.getId());
        }

        bleDevice.setGuaranteeTime(session.getProperty().getGuaranteeTime());

        try {
            bdDao.createOrUpdate(bleDevice);
            logger.info("Create or update to ble device " + device.getName() + ":" + device.getAddress() + " success");
        } catch (PersistenceException e) {
            logger.error("Create or update to ble device " + device.getName() + ":" + device.getAddress() + " error, " + e);
        }
    }

    /**
     * 获取蓝牙设备列表
     *
     * @return
     */
    public List<BleDevice> getBleDevices() {
        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            return this.bdDao.queryBleDevice(user.getObjectId());
        }

        return null;
    }

    /**
     * 获取服务器蓝牙设备列表
     *
     * @return
     */
    public ArrayList<BleDevice> getBleDevicesFromServer() {
        JSONObject jsonObject = this.stub.getBleDevices();
        int code = jsonObject.optInt("code");
        String message = jsonObject.optString("message");
        if (code == 0) {
            JSONArray result = jsonObject.optJSONArray("result");
            ArrayList<BleDevice> bleDevices = new ArrayList<>();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = result.optJSONObject(i);

                    BleDevice bleDevice = new BleDevice();
                    bleDevice.setDeviceName(object.optString("central_name"));
                    bleDevice.setMacAddress(object.optString("central_id"));
                    bleDevice.setUrl(object.optString("bike_image"));
                    bleDevice.setHardwareType(object.optInt("hardware"));
                    bleDevice.setBrandType(object.optInt("bike_type"));
                    bleDevice.setUserId(object.optString("user_id"));
                    bleDevice.setFrameId(object.optString("frame_id"));

                    bleDevices.add(bleDevice);
                }
            }
            return bleDevices;
        } else {
            Toasts.showOnUiThreadWithText(context, message);
        }
        return null;
    }

    /**
     * 获取设备信息
     *
     * @param deviceId
     * @param userId
     * @return
     */
    public BleDevice getBleDevice(String deviceId, String userId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        return this.bdDao.queryBleDevice(deviceId, userId);
    }

    /**
     * 获取设备信息
     *
     * @param deviceId
     * @return
     */
    public BleDevice getBleDevice(String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        return this.bdDao.queryBleDeviceByDeviceId(deviceId);
    }

    /**
     * 更新设备信息到本地
     *
     * @param macAddress
     * @param deviceId
     * @param hardwareType
     * @param brandType
     * @param bikeImage
     * @param frameId
     */
    public void updateBleDeviceInfo(String macAddress, String deviceId, int hardwareType, int brandType, String bikeImage, String frameId) {
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }

        this.bdDao.updateBleDeviceInfo(macAddress, deviceId, hardwareType, brandType, bikeImage, frameId);
    }

    /**
     * 检查中控是否激活
     *
     * @param controlNo
     * @return
     */
    public Map<String, Object> checkCControlActive(String controlNo) throws BusinessException {
        Map<String, Object> map = new HashMap<>();
        int owner = 0;
        boolean isActive = false;
        if (!TextUtils.isEmpty(controlNo)) {
            SharedPreferences packetSp = context.getSharedPreferences(context.getPackageName(), 0);
            isActive = packetSp.getBoolean(Constants.BLE.PREF_BLE_CONTROL_IS_ACTIVE + controlNo, false);
            owner = packetSp.getInt(Constants.BLE.PREF_BLE_CONTROL_ACTIVE_OWNER + controlNo, 0);
            if (!isActive) {
                JSONObject result = stub.checkCControlActive(controlNo);

                if (!JSONUtil.isNull(result)) {
                    int code = result.optInt("code");
                    if (code != 0) {
                        String message = result.optString("message");
                        if (!TextUtils.isEmpty(message)) {
                            Toasts.showOnUiThreadWithText(context, message);
                        }
                    } else {
                        JSONObject object = result.optJSONObject("result");
                        if (object != null) {
                            isActive = object.optBoolean("isActive");
                            owner = object.optInt("owner");
                        }

                    }
                }
            }
        }
        map.put("isActive", isActive);
        map.put("owner", owner);
        return map;
    }

    /**
     * 激活中控设备
     *
     * @param controlNo
     * @throws BusinessException
     */
    public boolean activeCControl(String controlNo) throws BusinessException {
        boolean isActive = false;
        if (!TextUtils.isEmpty(controlNo)) {
            JSONObject result = stub.activeCControl(controlNo);

            if (!JSONUtil.isNull(result)) {
                int code = result.optInt("code");
                if (code == 0) {
                    SharedPreferences packetSp = context.getSharedPreferences(context.getPackageName(), 0);
                    packetSp.edit().putBoolean(Constants.BLE.PREF_BLE_CONTROL_IS_ACTIVE + controlNo, true);
                    AVUser user = AVUser.getCurrentUser();
                    if (user != null) {
                        packetSp.edit().putInt(Constants.BLE.PREF_BLE_CONTROL_ACTIVE_OWNER
                                + controlNo, user.getSpeedxId());
                    }
                    isActive = true;
                }
            }
        }
        return isActive;
    }

    public void syncDeviceTotalDistance(String controlNo, float totalDistance) {
        if (!TextUtils.isEmpty(controlNo)) {
            JSONObject result = stub.syncDeviceTotalDistance(controlNo, totalDistance);

            if (!JSONUtil.isNull(result)) {
                int code = result.optInt("code");
                if (code != 0) {
                    String message = result.optString("message");
                    if (!TextUtils.isEmpty(message)) {
                        Toasts.showOnUiThreadWithText(context, message);
                    }
                }

            }
        }
    }

    /**
     * save device information to server
     *
     * @param centralId    device central id
     * @param centralName  device central name
     * @param hardwareType device hardware type: 0-Central Control, 1-Whole Bike
     * @param brandType    device name
     * @return {@link BleDevice}
     */
    public BleDevice saveDeviceToServer(String centralId, String centralName, int hardwareType, int brandType, String deviceId, boolean save) {
        logger.trace("saveDeviceToServer centralId=[" + centralId + "],centralName=[" + centralName + "]," +
                "hardwareType=[" + hardwareType + "],brandType= [" + brandType + "] ,deviceId=[" + deviceId + "],save=[" + save + "],lock =[" + saveDeviceToServerLock + "]");
        if (saveDeviceToServerLock) {
            return null;
        }

        if (!TextUtils.isEmpty(centralId) && save) {
            saveDeviceToServerLock = true;
            JSONObject result = stub.saveDeviceToServer(centralId, centralName, hardwareType, brandType);
            saveDeviceToServerLock = false;
            BleDevice bleDevice = new BleDevice();
            bleDevice.setHardwareType(hardwareType);
            bleDevice.setBrandType(brandType);
            bleDevice.setDeviceId(deviceId);
            bleDevice.setMacAddress(centralId);

            bleDevice.setDeviceName(centralName);
            if (!JSONUtil.isNull(result)) {
                int code = result.optInt("code");
                if (code == 0) {
                    JSONObject object = result.optJSONObject("result");
                    bleDevice.setUrl(object.optString("bike_image"));
                    bleDevice.setFrameId(object.optString("frame_id"));
                    return bleDevice;
                }
            }
            return bleDevice;
        }

        return null;
    }


}
