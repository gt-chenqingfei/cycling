LOCAL_PATH := $(call my-dir)

#
# libtrace
#
include $(CLEAR_VARS)
LOCAL_MODULE           := trace
LOCAL_MODULE_TAGS      := optional
LOCAL_C_INCLUDES       := $(LOCAL_PATH)/../include
LOCAL_CFLAGS           := -O3 -DNDEBUG -DTAG=\"Trace\"
LOCAL_SRC_FILES        := jni.c
LOCAL_LDLIBS           := -llog
include $(BUILD_SHARED_LIBRARY)
