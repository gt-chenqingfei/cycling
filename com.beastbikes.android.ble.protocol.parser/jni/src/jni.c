#include <stdlib.h>

#include <jni.h>

#if defined(__ANDROID__) || defined(ANDROID)
#  include <android/log.h>
#  define debug(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, __FILE__, fmt, __VA_ARGS__)

#define TAG    "myhello-jni-test" // 这个是自定义的LOG的标识
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#else
#  define debug printf
#endif

#include <ble-protocol-parser.h>
#include <ble-protocol-crc.h>

#define COM_BEASTBIKES_ANDROID_BLE_PROTOCOL_V1_PROTOCOL_PARSER_IMPL "com/beastbikes/android/ble/protocol/v1/ProtocolParserImpl"
#define MAC_ADDRESS_LENGTH			6

// sample
static jobject parse_sample_characteristic(JNIEnv *env, jobject self, jbyteArray bytes)
{
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);
	struct activity_sample *sample = ble_protocol_parse_sample_characteristic(data);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	if (NULL == sample)
		return NULL;

	jclass clazzSampleCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/SampleCharacteristic");
	jmethodID init = (*env)->GetMethodID(env, clazzSampleCharacteristic, "<init>", "()V");
	jmethodID setTotalDistance = (*env)->GetMethodID(env, clazzSampleCharacteristic, "setTotalDistance", "(I)V");
	jmethodID setGpsSpeed = (*env)->GetMethodID(env, clazzSampleCharacteristic, "setGpsSpeed", "(I)V");
	jmethodID setAntPlusSpeed = (*env)->GetMethodID(env, clazzSampleCharacteristic, "setAntPlusSpeed", "(I)V");
	jmethodID setCadence = (*env)->GetMethodID(env, clazzSampleCharacteristic, "setCadence", "(I)V");
	jmethodID setHeartBeatRate = (*env)->GetMethodID(env, clazzSampleCharacteristic, "setHeartBeatRate", "(I)V");
	jmethodID setTotalTime = (*env)->GetMethodID(env, clazzSampleCharacteristic, "setTotalTime", "(I)V");
	jobject sampleCharacteristic = (*env)->NewObject(env, clazzSampleCharacteristic, init);
	(*env)->CallVoidMethod(env, sampleCharacteristic, setTotalDistance, sample->total_distance);
	(*env)->CallVoidMethod(env, sampleCharacteristic, setTotalTime, sample->total_time);
	(*env)->CallVoidMethod(env, sampleCharacteristic, setGpsSpeed, sample->speed_gps);
	(*env)->CallVoidMethod(env, sampleCharacteristic, setAntPlusSpeed, sample->speed_ant_plus);
	(*env)->CallVoidMethod(env, sampleCharacteristic, setCadence, sample->cadence);
	(*env)->CallVoidMethod(env, sampleCharacteristic, setHeartBeatRate, sample->heart_beat_rate);
	free(sample);
	return sampleCharacteristic;
}

/**
 * 解析传感器数据
 */
static jobject parse_sensor_characteristic(JNIEnv *env, jobject self, jbyteArray bytes)
{
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);
	jint len = (*env)->GetArrayLength(env,bytes);
//	LOGD("Icedan sensor parse---------[%d]",len);
	struct sensor_info *sensor = ble_protocol_parse_sensor_characteristic(data);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	if (NULL == sensor) {
		return NULL;
	}

	jobject sensorCharacteristic = NULL;
	jclass clazzSensorCharacteristic = NULL;

	switch (sensor->type) {
		case SENSOR_TYPE_BATTERY: {
			clazzSensorCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/BatterySensorCharacteristic");

			struct battery_sensor_info *battery = (struct battery_sensor_info*) sensor;
			jmethodID init = (*env)->GetMethodID(env, clazzSensorCharacteristic, "<init>", "()V");
			jmethodID setPercentage = (*env)->GetMethodID(env, clazzSensorCharacteristic, "setPercentage", "(I)V");
			jmethodID setChargeState = (*env)->GetMethodID(env, clazzSensorCharacteristic, "setChargeState", "(I)V");
			sensorCharacteristic = (*env)->NewObject(env, clazzSensorCharacteristic, init);
			(*env)->CallVoidMethod(env, sensorCharacteristic, setPercentage, battery->percentage);
			(*env)->CallVoidMethod(env, sensorCharacteristic, setChargeState, battery->charge_state);
			break;
		}
		default:
			break;
	}

	free(sensor);
	return sensorCharacteristic;
}

/**
 * 解析设置相关信息
 */
static jobject parse_ble_protocol_config_characteristic(JNIEnv *env, jobject self, jbyteArray bytes)
{
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);
	struct config *config = ble_protocol_parse_config_characteristic(data);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	if (NULL == config) {
		return NULL;
	}

	jobject configCharacteristic = NULL;
	jclass clazzConfigCharacteristic = NULL;

	switch (config->type) {
		case CONFIG_TYPE_SYSTIME: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/SystimeConfigCharacteristic");

			struct systime_config *systime = (struct systime_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setSystime = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setSystime", "(J)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setSystime, systime->systime);
			break;
		}
		case CONFIG_TYPE_FREQUENCY: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/FrequencyConfigCharacteristic");

			struct frequency_config *frequency = (struct frequency_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setFrequency = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setFrequency", "(I)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setFrequency, frequency->frequency);
			break;
		}
		case CONFIG_TYPE_WHEEL: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/WheelConfigCharacteristic");

			struct wheel_config *wheel = (struct wheel_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setSize = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setSize", "(I)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setSize, wheel->size);
			break;
		}
		case CONFIG_TYPE_BACKLIGHT: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/BackLightConfigCharacteristic");

			struct backlight_config *backlight = (struct backlight_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setDuration = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setDuration", "(I)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setDuration, backlight->duration);
			break;
		}
		case CONFIG_TYPE_BEEP: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/BeepConfigCharacteristic");

			struct beep_config *beep = (struct beep_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setMuted = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setMuted", "(I)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setMuted, beep->muted);
			break;
		}
		case CONFIG_TYPE_LOCALE: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/LocaleConfigCharcateristic");

			struct locale_config *locale = (struct locale_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setLocale = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setLocale", "(I)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setLocale, locale->locale);
			break;
		}
		case CONFIG_TYPE_AUTOLIGHT: {
			clazzConfigCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/AutoLightConfigCharacteristic");

			struct autolight_config *autolight = (struct autolight_config*) config;
			jmethodID init = (*env)->GetMethodID(env, clazzConfigCharacteristic, "<init>", "()V");
			jmethodID setEnabled = (*env)->GetMethodID(env, clazzConfigCharacteristic, "setEnabled", "(I)V");
			configCharacteristic = (*env)->NewObject(env, clazzConfigCharacteristic, init);
			(*env)->CallVoidMethod(env, configCharacteristic, setEnabled, autolight->enabled);
			break;
		}
		default:
			break;
	}

	free(config);

	return configCharacteristic;
}

/**
 * 解析发送命令的回馈信息
 */
static jobject parse_ble_protocol_command_response_characteristic(JNIEnv *env, jobject self, jbyteArray bytes)
{
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);
	struct command_response *command = ble_protocol_parse_command_response_characteristic(data);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	if (NULL == command) {
		return NULL;
	}

	jobject commandCharacteristic = NULL;
	jclass clazzCommandCharacteristic = NULL;

	switch (command->type) {
		case COMMAND_RESPONSE_TYPE_DEVICE_INFO: {// 0x01, Device Info
			clazzCommandCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/DeviceInfoCommandCharacteristic");

			struct command_response_device_info *deviceInfo = (struct command_response_device_info*) command;
			jmethodID init = (*env)->GetMethodID(env, clazzCommandCharacteristic, "<init>", "()V");
			jmethodID setFirmwareVersion = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setFirmwareVersion", "(I)V");
			jmethodID setFrequency = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setFrequency", "(I)V");
			jmethodID setLocale = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setLocale", "(I)V");
			jmethodID setBacklight = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setBacklight", "(I)V");
			jmethodID setBattery = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setBattery", "(I)V");
			jmethodID setAutolight = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setAutolight", "(I)V");
			jmethodID setMute = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setMute", "(I)V");
			jmethodID setGpsService = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setGpsService", "(I)V");
			jmethodID setMileageUnit = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setMileageUnit", "(I)V");
			jmethodID setHardwareType = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setHardwareType", "(I)V");
			jmethodID setBrandType = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setBrandType", "(I)V");
			jmethodID setWheelType = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setWheelType", "(I)V");
			jmethodID setNotification = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setNotification", "(I)V");
			jmethodID setFavouriteCadence = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setFavouriteCadence", "(I)V");
			jmethodID setShakeUp = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setShakeUp", "(I)V");
			commandCharacteristic = (*env)->NewObject(env, clazzCommandCharacteristic, init);
			(*env)->CallVoidMethod(env, commandCharacteristic, setFirmwareVersion, deviceInfo->firmware_version);
			(*env)->CallVoidMethod(env, commandCharacteristic, setFrequency, deviceInfo->frequency);
			(*env)->CallVoidMethod(env, commandCharacteristic, setLocale, deviceInfo->locale);
			(*env)->CallVoidMethod(env, commandCharacteristic, setBacklight, deviceInfo->backlight);
			(*env)->CallVoidMethod(env, commandCharacteristic, setBattery, deviceInfo->battery);
			(*env)->CallVoidMethod(env, commandCharacteristic, setAutolight, deviceInfo->autolight);
			(*env)->CallVoidMethod(env, commandCharacteristic, setMute, deviceInfo->mute);
			(*env)->CallVoidMethod(env, commandCharacteristic, setGpsService, deviceInfo->gps_location_service);
			(*env)->CallVoidMethod(env, commandCharacteristic, setMileageUnit, deviceInfo->mileage_unit);
			(*env)->CallVoidMethod(env, commandCharacteristic, setHardwareType, deviceInfo->hardware_type);
			(*env)->CallVoidMethod(env, commandCharacteristic, setBrandType, deviceInfo->brand_type);
			(*env)->CallVoidMethod(env, commandCharacteristic, setWheelType, deviceInfo->wheel_diameter);
			(*env)->CallVoidMethod(env, commandCharacteristic, setNotification, deviceInfo->notification_on);
			(*env)->CallVoidMethod(env, commandCharacteristic, setFavouriteCadence, deviceInfo->favourite_cadence);
			(*env)->CallVoidMethod(env, commandCharacteristic, setShakeUp, deviceInfo->shake_up);
			break;
		}
		case COMMAND_RESPONSE_TYPE_RECEIVE_RESPONSE: {// 0x06, Receive Response
			clazzCommandCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/ReveiveResponseCommandCharacteristic");

			struct command_response_data_received *received = (struct command_response_data_received*) command;
			jmethodID init = (*env)->GetMethodID(env, clazzCommandCharacteristic, "<init>", "()V");
			jmethodID setFlags = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setFlags", "(I)V");
			commandCharacteristic = (*env)->NewObject(env, clazzCommandCharacteristic, init);
			(*env)->CallVoidMethod(env, commandCharacteristic, setFlags, received->flags);
			break;
		}
		case COMMAND_RESPONSE_TYPE_OTA_REQUEST_RESPONSE: {// 0x07, OTA Request
			clazzCommandCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/OTARequestCommandCharacteristic");

			struct command_response_ota_start *otaStart = (struct command_response_ota_start*) command;
			jmethodID init = (*env)->GetMethodID(env, clazzCommandCharacteristic, "<init>", "()V");
			jmethodID setProcessType = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setProcessType", "(I)V");
			jmethodID setFlags = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setFlags", "(I)V");
			jmethodID setRequestPacketIndex = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setRequestPacketIndex", "(I)V");
			commandCharacteristic = (*env)->NewObject(env, clazzCommandCharacteristic, init);
			(*env)->CallVoidMethod(env, commandCharacteristic, setProcessType, otaStart->process_type);
			(*env)->CallVoidMethod(env, commandCharacteristic, setFlags, otaStart->flags);
			(*env)->CallVoidMethod(env, commandCharacteristic, setRequestPacketIndex, otaStart->request_packet_index);
			break;
		}
		case COMMAND_RESPONSE_TYPE_AGPS_INFO: {// 0x02, AGPS Info
			clazzCommandCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/AGpsInfoCharacteristic");

			struct command_response_agps_info *agpsInfo = (struct command_response_agps_info*) command;
			jmethodID init = (*env)->GetMethodID(env, clazzCommandCharacteristic, "<init>", "()V");
			jmethodID setUpdateTime = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setUpdateTime", "(I)V");
			commandCharacteristic = (*env)->NewObject(env, clazzCommandCharacteristic, init);
			(*env)->CallVoidMethod(env, commandCharacteristic, setUpdateTime, agpsInfo->update_time);
			break;
		}
		case COMMAND_RESPONSE_TYPE_DEVICE_INFO_EXTEND: {// 0x04, Device Information Extension
			clazzCommandCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/DeviceInfoExtensionCharacteristic");

			struct command_response_device_info_extend *deivce = (struct command_response_device_info_extend*) command;
			jmethodID init = (*env)->GetMethodID(env, clazzCommandCharacteristic, "<init>", "()V");
			jmethodID setGuaranteeTime = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setGuaranteeTime", "(I)V");
			jmethodID setMacAddress = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setMacAddress", "([B)V");
			commandCharacteristic = (*env)->NewObject(env, clazzCommandCharacteristic, init);
			jbyteArray macArray = (*env)->NewByteArray(env, MAC_ADDRESS_LENGTH);
			jbyte *bytes = (*env)->GetByteArrayElements(env, macArray, 0);
			int i;
			for (i = 0; i < MAC_ADDRESS_LENGTH; i++) {
				bytes[i] = deivce->mac_address[i];
			}
			(*env)->SetByteArrayRegion(env, macArray, 0, MAC_ADDRESS_LENGTH, bytes );
			(*env)->CallVoidMethod(env, commandCharacteristic, setMacAddress, macArray);
			(*env)->CallVoidMethod(env, commandCharacteristic, setGuaranteeTime, deivce->guarantee_start_time);
			break;
		}
		case COMMAND_RESPONSE_TYPE_CYCLING_TARGET_INFO: {// 0x03, User Cycling Target Data
			clazzCommandCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v2/CyclingTargetCharacteristic");

			struct command_response_cycling_target_info *targetInfo = (struct command_response_cycling_target_info*) command;
			jmethodID init = (*env)->GetMethodID(env, clazzCommandCharacteristic, "<init>", "()V");
			jmethodID setTargetType = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setTargetType", "(I)V");
			jmethodID setTargetValue = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setTargetValue", "(I)V");
			jmethodID setCurrentValue = (*env)->GetMethodID(env, clazzCommandCharacteristic, "setCurrentValue", "(I)V");
			commandCharacteristic = (*env)->NewObject(env, clazzCommandCharacteristic, init);
			(*env)->CallVoidMethod(env, commandCharacteristic, setTargetType, targetInfo->target_type);
			(*env)->CallVoidMethod(env, commandCharacteristic, setTargetValue, targetInfo->target_value_in_meter);
			(*env)->CallVoidMethod(env, commandCharacteristic, setCurrentValue, targetInfo->current_distance_in_meter);
			break;
		}
		default :
			break;
	}

	free(command);

	return commandCharacteristic;
}

/**
 * 大数据同步, 包括骑行数据同步 & 骑行记录预览
 */
static jobject parse_ble_protocol_activity_sync_characteristic(JNIEnv *env, jobject self, jbyteArray bytes)
{
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);

	struct sync_data *syncData = ble_protocol_parse_activity_sync_characteristic(data);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	if (NULL == syncData) {
		return NULL;
	}

	jobject syncDataCharacteristic = NULL;
	jclass clazzSyncDataCharacteristic = NULL;

	switch (syncData->cycling_data_type) {
		case SYNC_CYCLING_ACTIVITY_DATA_SYNCRONIZATION: {// 同步骑行记录
			clazzSyncDataCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/SynchronizationDataCharacteristic");

			struct sync_data_synchronization *synchronization = (struct sync_data_synchronization*) syncData;
			jmethodID init = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "<init>", "()V");
			jmethodID setTotalPacketCount = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setTotalPacketCount", "(I)V");
			jmethodID setCurrentPacketIndex = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setCurrentPacketIndex", "(I)V");

			jclass clazzSyncSampleCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/CyclingSampleCharacteristic");
			jobjectArray sampleArray = (*env)->NewObjectArray(env, 6, clazzSyncSampleCharacteristic, 0);
			int i;
			for (i = 0; i < 6; i++)
			{
				struct cycling_synchronization_sample cyclingSample = (struct cycling_synchronization_sample) (synchronization->sync_sample_data[i]);
				jmethodID init = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "<init>", "()V");
				jmethodID setSyncDataType = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setSyncDataType", "(I)V");
				jmethodID setTimestamp = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setTimestamp", "(I)V");
				jmethodID setLatitude = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setLatitude", "(D)V");
				jmethodID setLongitude = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setLongitude", "(D)V");
				jmethodID setAltitude = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setAltitude", "(I)V");
				jmethodID setSpeed = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setSpeed", "(I)V");
				jmethodID setMaxSpeed = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setMaxSpeed", "(I)V");
				jmethodID setDistance = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setDistance", "(I)V");
				jmethodID setCadence = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setCadence", "(I)V");
				jmethodID setMaxCadence = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setMaxCadence", "(I)V");
				jmethodID setHeartRate = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setHeartRate", "(I)V");
				jmethodID setMaxHeartRate = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setMaxHeartRate", "(I)V");
				jobject syncSampleCharacteristic = (*env)->NewObject(env, clazzSyncSampleCharacteristic, init);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setSyncDataType, cyclingSample.sync_data_type);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setTimestamp, cyclingSample.timestamp);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setLatitude, cyclingSample.latitude);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setLongitude, cyclingSample.longitude);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setAltitude, cyclingSample.altitude);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setSpeed, cyclingSample.speed);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setMaxSpeed, cyclingSample.max_speed);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setDistance, cyclingSample.distance);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setCadence, cyclingSample.cadence);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setMaxCadence, cyclingSample.max_cadence);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setHeartRate, cyclingSample.heart_rate);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setMaxHeartRate, cyclingSample.max_heart_rate);
				(*env)->SetObjectArrayElement(env, sampleArray, i, syncSampleCharacteristic);
				(*env)->DeleteLocalRef(env, syncSampleCharacteristic);
			}

			jmethodID setSamples = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setSamples", "([Lcom/beastbikes/android/ble/protocol/v1/CyclingSampleCharacteristic;)V");
			syncDataCharacteristic = (*env)->NewObject(env, clazzSyncDataCharacteristic, init);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setTotalPacketCount, synchronization->total_packet_count);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setCurrentPacketIndex, synchronization->current_packet_index);
			if (NULL != sampleArray && (*env)->GetArrayLength(env, sampleArray) > 0) {
				(*env)->CallVoidMethod(env, syncDataCharacteristic, setSamples, sampleArray);
			}
			break;
		}
		case SYNC_CYCLING_ACTIVITY_DATA_PREVIEW: {// 同步预览数据
			clazzSyncDataCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/PreviewDataCharacteristic");

			// 初始化
			jclass clazzCyclingActivityCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/CyclingActivityCharacteristic");

			// 初始化CyclingSample
			jclass clazzSyncSampleCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/CyclingSampleCharacteristic");
			jmethodID sampleInit = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "<init>", "()V");
			jmethodID setSampleSyncDataType = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setSyncDataType", "(I)V");
			jmethodID setTimestamp = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setTimestamp", "(I)V");
			jmethodID setLatitude = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setLatitude", "(D)V");
			jmethodID setLongitude = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setLongitude", "(D)V");
			jmethodID setAltitude = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setAltitude", "(I)V");
			jmethodID setSpeed = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setSpeed", "(I)V");
			jmethodID setMaxSpeed = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setMaxSpeed", "(I)V");
			jmethodID setDistance = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setDistance", "(I)V");
			jmethodID setCadence = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setCadence", "(I)V");
			jmethodID setMaxCadence = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setMaxCadence", "(I)V");
			jmethodID setHeartRate = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setHeartRate", "(I)V");
			jmethodID setMaxHeartRate = (*env)->GetMethodID(env, clazzSyncSampleCharacteristic, "setMaxHeartRate", "(I)V");

			jobjectArray sampleArray = (*env)->NewObjectArray(env, 5, clazzSyncSampleCharacteristic, 0);
			struct sync_data_preview *preview = (struct sync_data_preview*) syncData;

			int i;
			for (i = 0; i < 5; i++)
			{
				struct cycling_synchronization_sample cyclingSample = (struct cycling_synchronization_sample) (preview->preview_sample_data[i]);
				jobject syncSampleCharacteristic = (*env)->NewObject(env, clazzSyncSampleCharacteristic, sampleInit);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setSampleSyncDataType, cyclingSample.sync_data_type);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setTimestamp, cyclingSample.timestamp);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setLatitude, cyclingSample.latitude);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setLongitude, cyclingSample.longitude);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setAltitude, cyclingSample.altitude);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setSpeed, cyclingSample.speed);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setMaxSpeed, cyclingSample.max_speed);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setDistance, cyclingSample.distance);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setCadence, cyclingSample.cadence);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setMaxCadence, cyclingSample.max_cadence);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setHeartRate, cyclingSample.heart_rate);
				(*env)->CallVoidMethod(env, syncSampleCharacteristic, setMaxHeartRate, cyclingSample.max_heart_rate);
				(*env)->SetObjectArrayElement(env, sampleArray, i, syncSampleCharacteristic);
				(*env)->DeleteLocalRef(env, syncSampleCharacteristic);
			}

			jmethodID activityInit = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "<init>", "()V");
			jmethodID setSyncDataType = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setSyncDataType", "(I)V");
			jmethodID setStopTime = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setStopTime", "(I)V");
			jmethodID setStartTime = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setStartTime", "(I)V");
			jmethodID setSampleRate = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setSampleRate", "(I)V");
			jmethodID setTotalDistance = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setTotalDistance", "(I)V");
			jmethodID setTotalTime = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setTotalTime", "(I)V");
			jmethodID setSampleCount = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setSampleCount", "(I)V");
			jmethodID setClimbHeight = (*env)->GetMethodID(env, clazzCyclingActivityCharacteristic, "setClimbHeight", "(I)V");

			struct cycling_preview_activity activity = (struct cycling_preview_activity) preview->preview_activity;

			jobject previewActivityCharacteristic = (*env)->NewObject(env, clazzCyclingActivityCharacteristic, activityInit);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setSyncDataType, activity.sync_data_type);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setStopTime, activity.stop_time);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setStartTime, activity.start_time);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setSampleRate, activity.sample_rate);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setTotalDistance, activity.total_distance);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setTotalTime, activity.total_time);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setSampleCount, activity.sample_count);
			(*env)->CallVoidMethod(env, previewActivityCharacteristic, setClimbHeight, activity.climb_height);

			jmethodID init = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "<init>", "()V");
			syncDataCharacteristic = (*env)->NewObject(env, clazzSyncDataCharacteristic, init);
			jmethodID setTotalPacketCount = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setTotalPacketCount", "(I)V");
			jmethodID setCurrentPacketIndex = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setCurrentPacketIndex", "(I)V");
			jmethodID setActivity = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setActivity", "(Lcom/beastbikes/android/ble/protocol/v1/CyclingActivityCharacteristic;)V");
			jmethodID setSamples = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setSamples", "([Lcom/beastbikes/android/ble/protocol/v1/CyclingSampleCharacteristic;)V");

			(*env)->CallVoidMethod(env, syncDataCharacteristic, setTotalPacketCount, preview->total_packet_count);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setCurrentPacketIndex, preview->current_packet_index);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setActivity, previewActivityCharacteristic);

			if (NULL != sampleArray && (*env)->GetArrayLength(env, sampleArray) > 0) {
				(*env)->CallVoidMethod(env, syncDataCharacteristic, setSamples, sampleArray);
			}

			break;
		}
		case OTA_FIRMWARE_INFO: {// 解析固件版本号
			clazzSyncDataCharacteristic = (*env)->FindClass(env, "com/beastbikes/android/ble/protocol/v1/OTAFirmwareInfoCharacteristic");
			jmethodID otaInfoInit = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "<init>", "()V");
			jmethodID setOverallFirmwareVersion = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setOverallFirmwareVersion", "(I)V");
			jmethodID setMcuFirmwareVersion = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setMcuFirmwareVersion", "(I)V");
			jmethodID setMcuCheckSum = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setMcuCheckSum", "(I)V");
			jmethodID setBleFirmwareVersion = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setBleFirmwareVersion", "(I)V");
			jmethodID setBleCheckSum = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setBleCheckSum", "(I)V");
			jmethodID setUiFirmwareVersion = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setUiFirmwareVersion", "(I)V");
			jmethodID setUiCheckSum = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setUiCheckSum", "(I)V");
			jmethodID setFontFirmwareVersion = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setFontFirmwareVersion", "(I)V");
			jmethodID setFontCheckSum = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setFontCheckSum", "(I)V");
			jmethodID setPowerFirmwareVersion = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setPowerFirmwareVersion", "(I)V");
			jmethodID setPowerCheckSum = (*env)->GetMethodID(env, clazzSyncDataCharacteristic, "setPowerCheckSum", "(I)V");

			syncDataCharacteristic = (*env)->NewObject(env, clazzSyncDataCharacteristic, otaInfoInit);
			struct ota_firmware_info *otaFirmwareInfo = (struct ota_firmware_info *) syncData;
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setOverallFirmwareVersion, otaFirmwareInfo->overall_firmware_version);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setMcuFirmwareVersion, otaFirmwareInfo->mcu_firmware_version);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setMcuCheckSum, otaFirmwareInfo->mcu_checksum);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setBleFirmwareVersion, otaFirmwareInfo->ble_firmware_version);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setBleCheckSum, otaFirmwareInfo->ble_checksum);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setUiFirmwareVersion, otaFirmwareInfo->ui_firmware_version);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setUiCheckSum, otaFirmwareInfo->ui_checksum);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setFontFirmwareVersion, otaFirmwareInfo->font_firmware_version);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setFontCheckSum, otaFirmwareInfo->font_checksum);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setPowerFirmwareVersion, otaFirmwareInfo->power_firmware_version);
			(*env)->CallVoidMethod(env, syncDataCharacteristic, setPowerCheckSum, otaFirmwareInfo->power_checksum);
		}
		default:
			break;
	}

	free(syncData);

	return syncDataCharacteristic;
}

static jbyte parseCrc8(JNIEnv *env, jobject self, jbyteArray bytes) {
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);

	uint8_t* byte_array = (uint8_t *) data;
	jbyte ret = crc8(byte_array+1, 18);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	return ret;
}

static jint parseCrc16(JNIEnv *env, jobject self, jbyteArray bytes) {

	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);

	uint8_t *byte_array = (uint8_t *) data;

	jint ret = crc16(byte_array+1, 197);

	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);

	return ret;
}

static jint getCheckSum(JNIEnv *env, jobject self, jbyteArray bytes) {
	jbyte* data = (*env)->GetByteArrayElements(env, bytes, NULL);
	uint16_t *byte_array = (uint16_t *) data;
	jint length = (*env)->GetArrayLength(env, bytes);
	jint ret = get_checksum_value(byte_array, length);
//	LOGD("Icedan getCheckSum parse---------[%d]",ret);
	(*env)->ReleaseByteArrayElements(env, bytes, data, 0);
	return ret;
}

const static JNINativeMethod native_methods[] = {
		{ "parseSampleCharacteristic",  "([B)Lcom/beastbikes/android/ble/protocol/v1/SampleCharacteristic;",  (void *) parse_sample_characteristic },
		{ "parseSensorCharacteristic", "([B)Lcom/beastbikes/android/ble/protocol/v1/SensorCharacteristic;", (void *) parse_sensor_characteristic   },
		{ "parseConfigCharacteristic",  "([B)Lcom/beastbikes/android/ble/protocol/v1/ConfigCharacteristic;",  (void *) parse_ble_protocol_config_characteristic },
		{ "parseCommandCharacteristic", "([B)Lcom/beastbikes/android/ble/protocol/v1/CommandCharacteristic;", (void *) parse_ble_protocol_command_response_characteristic  },
		{ "parseSyncDataCharacteristic", "([B)Lcom/beastbikes/android/ble/protocol/v1/SyncDataCharacteristic;", (void *) parse_ble_protocol_activity_sync_characteristic },
		{ "crc8", "([B)B", (void *) parseCrc8 },
		{ "crc16", "([B)I", (void *) parseCrc16 },
		{ "getCheckSum", "([B)I", (void *) getCheckSum },
};

void register_native_methods(JNIEnv *env)
{
	const jclass clazzProtocolParserImpl = (*env)->FindClass(env, COM_BEASTBIKES_ANDROID_BLE_PROTOCOL_V1_PROTOCOL_PARSER_IMPL);
	const size_t n = sizeof(native_methods) / sizeof(JNINativeMethod);

	(*env)->RegisterNatives(env, clazzProtocolParserImpl, native_methods, n);
	(*env)->DeleteLocalRef(env, clazzProtocolParserImpl);
}

void unregister_native_methods(JNIEnv *env)
{
	const jclass clazzProtocolParserImpl = (*env)->FindClass(env, COM_BEASTBIKES_ANDROID_BLE_PROTOCOL_V1_PROTOCOL_PARSER_IMPL);

	(*env)->UnregisterNatives(env, clazzProtocolParserImpl);
	(*env)->DeleteLocalRef(env, clazzProtocolParserImpl);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
JNIEnv *env = NULL;

if (JNI_OK != (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4)) {
return -1;
}

register_native_methods(env);

return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved)
{
	JNIEnv *env = NULL;

	if (JNI_OK != (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4)) {
		return;
	}

	unregister_native_methods(env);
}
