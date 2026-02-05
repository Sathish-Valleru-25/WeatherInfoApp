package com.example.weatherapp.ui.theme.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * function to get the city name from the device's location.
 */
suspend fun getCityName(context: Context): String? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return try {
        val location = fusedLocationClient.awaitLastLocation(context)
        getCityNameFromCoordinates(context, location.latitude, location.longitude)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun FusedLocationProviderClient.awaitLastLocation(context: Context): Location {
    return suspendCancellableCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resumeWithException(Exception("Last location was null"))
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }

    }
}

/**
 * function to get the city name from the device's location coordinates.
 */
suspend fun getCityNameFromCoordinates(context: Context, latitude: Double, longitude: Double): String? {
    // 2. Run operation on a background thread
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val cityName = addresses.firstOrNull()?.locality
                        continuation.resume(cityName)
                    }
                }
            } else {
                // The deprecated version is synchronous, so it works fine inside withContext
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.locality
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}