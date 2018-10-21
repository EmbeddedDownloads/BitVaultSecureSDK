#include <jni.h>
/* Header for class com_marakana_NativeLib */

#ifndef _Included_org_example_ndk_nativeLib
#define _Included_org_example_ndk_nativeLib
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_marakana_NativeLib
 * Method:    add
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_embedded_wallet_MainActivity_add
        (JNIEnv *, jobject, jint, jint);


#ifdef __cplusplus
}
#endif
#endif