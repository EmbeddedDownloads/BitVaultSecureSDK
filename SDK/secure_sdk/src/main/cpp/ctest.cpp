#include <jni.h>
#include <stdio.h>
/* ctest.c */
extern "C"
int Java_com_embedded_wallet_MainActivity_helloFromC
  (JNIEnv * env, jobject jobj, jint i)
{
	printf("Hello from C!: %d\n", i);
}
