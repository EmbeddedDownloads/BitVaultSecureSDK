#include <jni.h>
#include "bativeLib.h"

JNIEXPORT jint JNICALL Java_com_embedded_wallet_MainActivity_add
        (JNIEnv * env, jobject obj, jint value1, jint value2) {
    return (value1 + value2);
}
