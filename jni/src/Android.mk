LOCAL_PATH := $(call my-dir)

#
# libbeastbikes-jni
#
include $(CLEAR_VARS)
LOCAL_MODULE           := beastbikes-jni
LOCAL_MODULE_TAGS      := optional
LOCAL_CFLAGS           := -O3 -DNDEBUG
LOCAL_SRC_FILES        := jni.c
LOCAL_LDLIBS           := -llog
include $(BUILD_SHARED_LIBRARY)
