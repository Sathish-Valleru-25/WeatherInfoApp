
#include <jni.h>
#include <string>

// Helper macros to convert the compile-time definition to a C++ string
#define XSTR(s) STR(s)
#define STR(s) #s

extern "C" JNIEXPORT jstring JNICALL
// The function name MUST match your package name and the Kotlin class.
// Java_package_ClassName_methodName
Java_com_example_weatherapp_di_Keys_getNativeApiKey(JNIEnv *env, jobject thiz) {
    std::string api_key = WEATHER_API_KEY;
    return env->NewStringUTF(api_key.c_str());
}

