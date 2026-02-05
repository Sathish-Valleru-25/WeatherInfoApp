package com.example.weatherapp.di

import javax.inject.Inject
import javax.inject.Singleton

/**
 * A class to hold the APP-ID key.
 */
@Singleton
class Keys @Inject constructor() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    // This declares a 'native' method. The implementation is in C++.
    // The name 'getApiKey' must match the JNI function name in the C++ file.
    private external fun getNativeApiKey(): String

    // This is the public property Hilt will use to get the key.
    val apiKey: String
        get() = getNativeApiKey()
}