package com.example.weatherapp.ui.theme.utils

import kotlin.text.isNotEmpty


/**
 * Validate input string is empty or not.
 */
fun isValidInput(cityName: String): Boolean {
    return  cityName.isNotEmpty()
}