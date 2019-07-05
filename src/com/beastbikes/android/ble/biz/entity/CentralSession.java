package com.beastbikes.android.ble.biz.entity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.text.TextUtils;

/**
 * Created by chenqingfei on 16/9/13.
 */
public class CentralSession {

    public static final int HD_TYPE_B08 = 0x00;
    public static final int HD_TYPE_S601 = 0x01;
    public static final int HD_TYPE_B09 = 0x02;
    public static final int HD_TYPE_S603 = 0x03;
    public static final int HD_TYPE_S605 = 0x04;

    public static final int SESSION_STATE_NONE = 0;
    public static final int SESSION_STATE_CONNECTING = 2;
    public static final int SESSION_STATE_CONNECTED = 3;
    public static final int SESSION_STATE_DISCOVERED = 4;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备的唯一标示 , 是从BluetoothDevice address中的得到的
     *
     * @see #address2CentralId
     */
    private String centralId;

    /**
     * 扫描后从LeScanCallback中得到的设备
     */
    private BluetoothDevice bluetoothDevice;

    /**
     * 用BluetoothDevice调用Connect 得到的BluetoothGatt
     */
    private BluetoothGatt bluetoothGatt;

    /**
     * 当前session的状态
     */
    private int state;

    /**
     * 扫描到是设备是否可连接
     */
    private boolean available = false;

    /**
     * 硬件类型
     */
    private int hdType;

    /**
     * 断开后或下次启动,自动附着连接
     */
    private boolean autoAttach = false;

    private boolean unBound = false;

    private InvocationParam property = new InvocationParam();

    public CentralSession(BluetoothDevice bluetoothDevice, int state, boolean available, int hdType) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = state;
        this.centralId = address2CentralId(bluetoothDevice.getAddress());
        this.hdType = hdType;
        this.available = available;
    }

    public CentralSession(String centralId) {
        this.centralId = centralId;
    }

    public String getCentralId() {
        return centralId;
    }

    public void setCentralId(String centralId) {
        this.centralId = centralId;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getHdType() {
        return hdType;
    }

    public void setHdType(int hdType) {
        this.hdType = hdType;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public boolean isAutoAttach() {
        return autoAttach;
    }

    public void setAutoAttach(boolean autoAttach) {
        this.autoAttach = autoAttach;
    }

    /**
     * BluetoothDevice address Convert to centralId
     *
     * @param address
     * @return
     */
    public static String address2CentralId(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        String[] addressArray = address.split(":");

        StringBuilder convert = new StringBuilder();
        for (int i = addressArray.length - 1; i >= 0; i--) {
            convert.append(addressArray[i]);
        }

        return convert.toString();
    }

    /**
     * centralId Convert to BluetoothDevice address
     *
     * @param centralId
     * @return
     */
    public static String centralId2Address(String centralId) {
        if (TextUtils.isEmpty(centralId)) {
            return null;
        }
        StringBuilder convert = new StringBuilder();

        for (int i = centralId.length() - 1; i >= 0; i--) {

            if (i % 2 == 0) {
                convert.append(centralId.charAt(i));
                convert.append(centralId.charAt(i + 1));
                if (i != 0) {
                    convert.append(":");
                }
            }
        }

        return convert.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj) {
            CentralSession session = (CentralSession) obj;

            if (this.available == session.isAvailable() && TextUtils.equals(this.centralId,
                    session.getCentralId())) {
                return true;
            }
        }
        return super.equals(obj);
    }

    /**
     * 判断当前设备是属于整车还是中控
     *
     * @return
     */
    public static boolean isWholeBike(int hdType) {
        boolean isWholeBike = true;
        switch (hdType) {
            case HD_TYPE_B08:
            case HD_TYPE_B09:
                isWholeBike = false;
                break;
            case HD_TYPE_S601:
            case HD_TYPE_S603:
            case HD_TYPE_S605:
                isWholeBike = true;
                break;
            default:
                break;
        }
        return isWholeBike;
    }

    public String getName() {
        if (bluetoothDevice != null) {
            return bluetoothDevice.getName();
        }
        return null;
    }

    public InvocationParam getProperty() {
        if (property == null) {
            property = new InvocationParam();
        }
        return property;
    }

    public void setProperty(InvocationParam property) {
        this.property = property;
    }

    public boolean isUnBound() {
        return unBound;
    }

    public void setUnBound(boolean unBound) {
        this.unBound = unBound;
    }


    @Override
    public String toString() {
        return "{" +
                "  centralId=" + centralId +
                ", available=" + available +
                ", autoAttach=" + autoAttach +
                ", state=" + state +
                ", bluetoothDevice=" + bluetoothDevice +
                ", bluetoothGatt=" + bluetoothGatt +
                ", hdType=" + hdType +
                ", unBound=" + unBound +
                ", name='" + getName() + '\'' +
                '}';
    }

}
