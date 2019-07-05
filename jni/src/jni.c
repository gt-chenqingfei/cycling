#include <stdlib.h>
#include <jni.h>

#include <android/log.h>

#define PKCS8_RSA_PRIVATE_KEY "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9FN1w8gfXSBP1/fWtC4gicvB7t+XZ20Qn3eBOaMT1zYf6QtUQ1aAQKIlVDmyidA1/BOgwp07Rvc6V/imAEp4tOGtrP8vedgliVuqMcLeNONSdlzSW66alcayjHrb4+5IYGV9vzMk7qGLHgZX++HJBUKkb1piqATvPJNFlhf1vJAgMBAAECgYA736xhG0oL3EkN9yhx8zG/5RP/WJzoQOByq7pTPCr4m/Ch30qVerJAmoKvpPumN+h1zdEBk5PHiAJkm96sG/PTndEfkZrAJ2hwSBqptcABYk6ED70gRTQ1S53tyQXIOSjRBcugY/21qeswS3nMyq3xDEPKXpdyKPeaTyuK86AEkQJBAM1M7p1lfzEKjNw17SDMLnca/8pBcA0EEcyvtaQpRvaLn61eQQnnPdpvHamkRBcOvgCAkfwa1uboru0QdXii/gUCQQDGmkP+KJPX9JVCrbRt7wKyIemyNM+J6y1ZBZ2bVCf9jacCQaSkIWnIR1S9UM+1CFE30So2CA0CfCDmQy+y7A31AkB8cGFB7j+GTkrLP7SX6KtRboAU7E0q1oijdO24r3xf/Imw4Cy0AAIx4KAuL29GOp1YWJYkJXCVTfyZnRxXHxSxAkEAvO0zkSv4uI8rDmtAIPQllF8+eRBT/deDJBR7ga/k+wctwK/Bd4Fxp9xzeETP0l8/I+IOTagK+Dos8d8oGQUFoQJBAI4NwpfoMFaLJXGY9ok45wXrcqkJgM+SN6i8hQeujXESVHYatAIL/1DgLi+u46EFD69fw0w+c7o0HLlMsYPAzJw="

#define COM_AVOS_AVOSCLOUD_AVOSCLOUD "com/avos/avoscloud/AVOSCloud"
#define COM_BEASTBIKES_ANDROID_BEASTBIKES "com/beastbikes/android/BeastBikes"

#define LEANCLOUD_APP_ID  "1xzez6xivqxdogg3ayf8nuit72ae175eggjtcqpeyahfhkqf"
#define LEANCLOUD_APP_KEY "bv7d83jp0gwhwc6hcfshmcy2ymjpp5h7u3m0wcp18cwlfxsk"

#ifndef TAG
#define TAG __FILE__
#endif

#define LEANCLOUD_APP_ID_DEBUG  "qiz2ee6ucwbte8130yevxnkc8iwltldvtaazx6tl2d4hey3r"
#define LEANCLOUD_APP_KEY_DEBUG "119uiyex7d42pezuuf6hv7afebg58fwjyfry3lit8rswc4b1"

#define MAPBOX_ACCESS_TOKEN  "pk.eyJ1Ijoic3BlZWR4IiwiYSI6ImNpaWp2Z3Q0eTAxN2Z1ZGtuZTF4NHpidmoifQ.n2jwLmXzoUlHinOkt_eMyg"
#define RONG_CLOUD_KEY  "qd46yzrf4o2df"
#define RONG_CLOUD_KEY_DEBUG "25wehl3uwar5w"
#define TWITTER_CONSUMER_KEY  "LRBM0H75rWrU9gNHvlEAA2aOy"
#define TWITTER_CONSUMER_SECRET "gbeWsZvA9ELJSdoBzJ5oLKX0TU09UOwrzdGfo9Tg7DjyGuMe8G"
#define BUGGLY_APPID "900015324"

#define API_URL  "https://api.speedx.com/api/"
#define DEV_BEAST_NEW_HOST  "https://www.speedx.com"
#define DEV_BEAST_HOST_DOMAIN  "www.speedx.com"



static jboolean debug;

static void leancloud_initialize(JNIEnv *env, jobject this, jboolean isDebug) {
    debug = isDebug;
    const jclass AVOSCloud = (*env)->FindClass(env, COM_AVOS_AVOSCLOUD_AVOSCLOUD);
    const jmethodID initialize = (*env)->GetStaticMethodID(env, AVOSCloud, "initialize",
                                                           "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V");
    const jstring appId = (*env)->NewStringUTF(env,
                                               isDebug ? LEANCLOUD_APP_ID_DEBUG : LEANCLOUD_APP_ID);
    const jstring appKey = (*env)->NewStringUTF(env, isDebug ? LEANCLOUD_APP_KEY_DEBUG
                                                             : LEANCLOUD_APP_KEY);

    (*env)->CallStaticVoidMethod(env, AVOSCloud, initialize, this, appId, appKey);
    (*env)->DeleteLocalRef(env, AVOSCloud);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    native_initialize
 * Signature: ()V
 */
static void native_initialize(JNIEnv *env, jobject this, jboolean isDebug) {
    leancloud_initialize(env, this, isDebug);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getHost
 * Signature: ()Ljava/lang/String
 */
static jstring getHost(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, DEV_BEAST_NEW_HOST);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getHostDomain
 * Signature: ()Ljava/lang/String
 */
static jstring getHostDomain(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, DEV_BEAST_HOST_DOMAIN);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getHostPullDomain
 * Signature: ()Ljava/lang/String
 */
static jstring getApiUrl(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, API_URL);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getMapBoxAccessToken
 * Signature: ()Ljava/lang/String
 */
static jstring getMapBoxAccessToken(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, MAPBOX_ACCESS_TOKEN);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getUserPrivateKey


 * Signature: ()Ljava/lang/String
 */
static jstring getUserPrivateKey(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, PKCS8_RSA_PRIVATE_KEY);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getRongCloudKey
 * Signature: ()Ljava/lang/String
 */
static jstring getRongCloudKey(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, debug ? RONG_CLOUD_KEY_DEBUG : RONG_CLOUD_KEY);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getTwitterConsumerKey
 * Signature: ()Ljava/lang/String
 */
static jstring getTwitterConsumerKey(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, TWITTER_CONSUMER_KEY);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getTwitterConsumerSecret
 * Signature: ()Ljava/lang/String
 */
static jstring getTwitterConsumerSecret(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, TWITTER_CONSUMER_SECRET);
}

/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    getBugglyAppId
 * Signature: ()Ljava/lang/String
 */
static jstring getBugglyAppId(JNIEnv *env, jobject this) {
    (*env)->NewStringUTF(env, BUGGLY_APPID);
}


/*
 * Class:     com_beastbikes_android_BeastBikes
 * Method:    native_finalize
 * Signature: ()V
 */
static void native_finalize(JNIEnv *env, jobject this) {
    // TODO
}

const static JNINativeMethod native_methods[] = {
        {"native_initialize",       "(Z)V",                 (void *) native_initialize},
        {"native_finalize",         "()V",                  (void *) native_finalize},
        {"getHost",                 "()Ljava/lang/String;", (void *) getHost},
        {"getHostDomain",           "()Ljava/lang/String;", (void *) getHostDomain},
        {"getApiUrl",               "()Ljava/lang/String;", (void *) getApiUrl},
        {"getMapBoxAccessToken",    "()Ljava/lang/String;", (void *) getMapBoxAccessToken},
        {"getUserPrivateKey",       "()Ljava/lang/String;", (void *) getUserPrivateKey},
        {"getRongCloudKey",         "()Ljava/lang/String;", (void *) getRongCloudKey},
        {"getTwitterConsumerKey",   "()Ljava/lang/String;", (void *) getTwitterConsumerKey},
        {"getTwitterConsumerSecret","()Ljava/lang/String;", (void *) getTwitterConsumerSecret},
        {"getBugglyAppId",          "()Ljava/lang/String;", (void *) getBugglyAppId}
};

void register_native_methods(JNIEnv *env) {
    const jclass BeastBikes = (*env)->FindClass(env, COM_BEASTBIKES_ANDROID_BEASTBIKES);
    const size_t n = sizeof(native_methods) / sizeof(JNINativeMethod);

    (*env)->RegisterNatives(env, BeastBikes, native_methods, n);
    (*env)->DeleteLocalRef(env, BeastBikes);
}

void unregister_native_methods(JNIEnv *env) {
    const jclass BeastBikes = (*env)->FindClass(env, COM_BEASTBIKES_ANDROID_BEASTBIKES);

    (*env)->UnregisterNatives(env, BeastBikes);
    (*env)->DeleteLocalRef(env, BeastBikes);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm,void *reserved){
    JNIEnv *env = NULL;

    if (JNI_OK != (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4)) {
        return -1;
    }

    register_native_methods(env);

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    if (JNI_OK != (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4)) {
        return;
    }

    unregister_native_methods(env);
}
