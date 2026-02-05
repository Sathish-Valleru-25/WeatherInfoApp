package com.example.weatherapp.ui.theme.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Extension function to convert Kelvin temperature to Fahrenheit.
 */
fun Double.toFahrenheit(): String {
    val fahrenheit = (this - 273.15) * 9/5 + 32
    return String.format("%.2f", fahrenheit)
}

/**
 * Extension function to convert Unix timestamp to a formatted time string.
 */
fun Long.toFormattedTime(): String {
    val date = Date(this * 1000)
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(date)
}
