package com.beastbikes.android.ble.protocol.v1;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;

import com.beastbikes.android.ble.protocol.CharacteristicValue;
import com.beastbikes.android.ble.protocol.ProtocolParser;

import java.util.UUID;
@TargetApi(18)
public class ProtocolParserImpl implements ProtocolParser {

    // activity sample notification
    public static final UUID UUID_ACTIVITY_SAMPLE_NOTIFI = UUID.fromString("0751CD20-A473-4EA1-B985-38F488DE94DC");
    // sensor notification
    public static final UUID UUID_SENSOR_NOTIFI = UUID.fromString("0751CD22-A473-4EA1-B985-38F488DE94DC");
    // write configuration
    public static final UUID UUID_CONFIGURATION = UUID.fromString("0751CD23-A473-4EA1-B985-38F488DE94DC");
    // command write
    public static final UUID UUID_COMMAND_REQUEST = UUID.fromString("0751CD28-A473-4EA1-B985-38F488DE94DC");
    // command notification
    public static final UUID UUID_COMMAND_NOTIFI = UUID.fromString("0751CD24-A473-4EA1-B985-38F488DE94DC");
    // activity synchronization notification
    public static final UUID UUID_ACTIVITY_SYNC_NOTIFI = UUID.fromString("0751CD26-A473-4EA1-B985-38F488DE94DC");
    // OTA info write
    public static final UUID UUID_OTA_INFO = UUID.fromString("0751CD27-A473-4EA1-B985-38F488DE94DC");
    // phone exchange Write
    public static final UUID UUID_PHONE_EXCHANGE_WRITE = UUID.fromString("0751CD29-A473-4EA1-B985-38F488DE94DC");
    // phone exchange notification
    public static final UUID UUID_PHONE_EXCHANGE_NOTIFI = UUID.fromString("0751CD2A-A473-4EA1-B985-38F488DE94DC");

    static {
        System.loadLibrary("ble-protocol-parser-jni");
    }

    @Override
    public CharacteristicValue parse(byte[] data) {
        final AbstractCharacteristicValue cv = parseCharacteristic(data);
        if (null != cv) {
            cv.setProtocol(data[0]);
            cv.setCrc(data[data.length - 1]);
        }

        return cv;
    }

    @Override
    public CharacteristicValue parse(BluetoothGattCharacteristic characteristic) {
        return null;
    }

    public native SampleCharacteristic parseSampleCharacteristic(byte[] data);

    public native SensorCharacteristic parseSensorCharacteristic(byte[] data);

    public native ConfigCharacteristic parseConfigCharacteristic(byte[] data);

    public native CommandCharacteristic parseCommandCharacteristic(byte[] data);

    public native SyncDataCharacteristic parseSyncDataCharacteristic(byte[] data);

    public native byte crc8(byte[] data);

    public native int crc16(byte[] data);

    public native int getCheckSum(byte[] data);

    private AbstractCharacteristicValue parseSyncCharacteristic(byte[] data) {
        return parseCharacteristic(data);
    }

    private AbstractCharacteristicValue parseCharacteristic(byte[] data) {
        if (data.length < 2)
            return null;

        switch (data[data.length - 2]) {
            case 0:
                return parseSampleCharacteristic(data);
            case 1:
                // TODO
                return null;
            case 2:
            case 3:
                return parseSensorCharacteristic(data);
            case 5:
                return parseCommandCharacteristic(data);
            default:
                return null;
        }
    }

    /**
     * parser characteristic value
     *
     * @param characteristic characteristic
     * @return
     */
    public AbstractCharacteristicValue parseCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (null == characteristic)
            return null;

        String uuid = characteristic.getUuid().toString();
        byte[] data = characteristic.getValue();
        if (uuid.equals(UUID_ACTIVITY_SAMPLE_NOTIFI.toString())) {
            if (data.length < 2) {
                return null;
            }
            switch (data[1]) {
                case 0x01:
                    break;
            }
            return parseSampleCharacteristic(data);
        }

        if (uuid.equals(UUID_COMMAND_NOTIFI.toString())) {// Command Response notification
            if (data.length < 2) {
                return null;
            }
            switch (data[1]) {
                case 0x01:// Device Information
                    DeviceInfoCommandCharacteristic deviceInfo = (DeviceInfoCommandCharacteristic) parseCommandCharacteristic(data);
                    return parseCommandCharacteristic(data);
                case 0x02:

                    break;
            }
            return parseCommandCharacteristic(data);
        }

        if (uuid.equals(UUID_SENSOR_NOTIFI.toString())) {
            return parseSensorCharacteristic(data);
        }

        if (uuid.equals(UUID_ACTIVITY_SYNC_NOTIFI.toString())) {
            byte cyclingType = 0x01;
            if (data.length > 2) {
                cyclingType = data[1];
            }
            if (cyclingType == 0x01) {
                PreviewDataCharacteristic character = (PreviewDataCharacteristic) parseSyncDataCharacteristic(data);
                CyclingSampleCharacteristic[] sample = character.getSamples();
                return character;
            }
            return null;
        }

        if (uuid.equals(UUID_PHONE_EXCHANGE_NOTIFI.toString())) {
            return parseConfigCharacteristic(data);
        }

        return null;
    }

}
