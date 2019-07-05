LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE      := ble-protocol-parser-jni
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS      := -O3 -DNDEBUG -D__ANDROID__
LOCAL_C_INCLUDES  := $(LOCAL_PATH)/../../include
LOCAL_SRC_FILES   := \
	../../source/ble-protocol-crc.c \
	../../source/ble-protocol-parser.c \
	../../source/ble-protocol-util.c \
	src/jni.c
LOCAL_LDLIBS      := -llog
include $(BUILD_SHARED_LIBRARY)
