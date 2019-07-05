#include <stdio.h>
#include <string.h>
#include <signal.h>

#include <jni.h>

#include <android/log.h>

#ifndef TAG
#define TAG __FILE__
#endif

#define FRAME_LEN 128

static JNIEnv *env = NULL;

static struct sigaction signals[NSIG];

static const char *signames[NSIG + 1] = {
        "",
        "SIGHUP",
        "SIGINT",
        "SIGQUIT",
        "SIGILL",
        "SIGTRAP",
        "SIGABRT",
        "SIGBUS",
        "SIGFPE",
        "SIGKILL",
        "SIGUSR1",
        "SIGSEGV",
        "SIGUSR2",
        "SIGPIPE",
        "SIGALRM",
        "SIGTERM",
        "SIGSTKFLT",
        "SIGCHLD",
        "SIGCONT",
        "SIGSTOP",
        "SIGTSTP",
        "SIGTTIN",
        "SIGTTOU",
        "SIGURG",
        "SIGXCPU",
        "SIGXFSZ",
        "SIGVTALRM",
        "SIGPROF",
        "SIGWINCH",
        "SIGIO",
        "SIGPWR",
        "SIGSYS",
        "SIGUNUSED",
};

static void handle_signal(int sig, siginfo_t *info, void *ctx) {
    char buf[1024];
    snprintf(buf, sizeof(buf), "Fatal signal %d (%s) at %p", sig, signames[sig], info->si_addr);

    // Logger logger = LoggerFactory.getLogger(TAG);
    jclass org_slf4j_logger_factory = (*env)->FindClass(env, "org/slf4j/LoggerFactory");
    jmethodID get_logger = (*env)->GetStaticMethodID(env, org_slf4j_logger_factory, "getLogger",
                                                     "(Ljava/lang/String;)Lorg/slf4j/Logger;");
    jobject logger = (*env)->CallStaticObjectMethod(env, org_slf4j_logger_factory, get_logger,
                                                    (*env)->NewStringUTF(env, TAG));
    // logger.error(msg);
    jclass org_slf4j_logger = (*env)->GetObjectClass(env, logger);
    jmethodID error = (*env)->GetMethodID(env, org_slf4j_logger, "error", "(Ljava/lang/String;)V");
    (*env)->CallVoidMethod(env, logger, error, (*env)->NewStringUTF(env, buf));

    signals[sig].sa_handler(sig);
}

static void setup_signal_handler() {
    memset(&signals, 0, sizeof(signals));

    struct sigaction handler;
    memset(&handler, 0, sizeof(handler));
    handler.sa_sigaction = handle_signal;
    handler.sa_flags = SA_SIGINFO;

#define CATCHSIG(x) sigaction(x, &handler, &signals[x])
    CATCHSIG(SIGILL);
    CATCHSIG(SIGABRT);
    CATCHSIG(SIGBUS);
    CATCHSIG(SIGFPE);
    CATCHSIG(SIGSEGV);
    CATCHSIG(SIGSTKFLT);
    CATCHSIG(SIGPIPE);
}

static void cleanup_signal_handler() {
#define UNCATCHSIG(x) sigaction(x, &signals[x], NULL);
    UNCATCHSIG(SIGILL);
    UNCATCHSIG(SIGABRT);
    UNCATCHSIG(SIGBUS);
    UNCATCHSIG(SIGFPE);
    UNCATCHSIG(SIGSEGV);
    UNCATCHSIG(SIGSTKFLT);
    UNCATCHSIG(SIGPIPE);
}

JNIEXPORT jint
JNI_OnLoad(JavaVM
*vm,
void *reserved
)
{
if (JNI_OK != (*vm)->
GetEnv(vm, (
void**) &env, JNI_VERSION_1_4)) {
return
JNI_ERR;
}

setup_signal_handler();

return
JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    cleanup_signal_handler();
}
