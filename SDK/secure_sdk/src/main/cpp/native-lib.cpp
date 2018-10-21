#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_embedded_wallet_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject, int i) {
    std::string hello = "Returning from C++"+i;
    return env->NewStringUTF(hello.c_str());
}
