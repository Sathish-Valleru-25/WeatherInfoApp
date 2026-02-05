package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.UserPreferences
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.di.ApiKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface WeatherRepository {
    val lastSearchedCity: Flow<String?>
    suspend fun getWeatherByCity(city: String): WeatherResponse
    suspend fun saveLastSearchedCity(city: String)
}

/**
 * Repository class for retrieving weather data.
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val userPreferences: UserPreferences,
    @param:ApiKey private val apiKey: String
) : WeatherRepository {

    // Get weather by city name
    override suspend fun getWeatherByCity(city: String): WeatherResponse {
        val response = api.getWeatherByCity(query = city, apiKey = apiKey)
        // Cache the successful search
        saveLastSearchedCity(city)
        return response
    }

    // Get the last searched city from the user preferences
    override val lastSearchedCity: Flow<String?> = userPreferences.lastCity

    override suspend fun saveLastSearchedCity(city: String) {
        userPreferences.saveLastCity(city)
    }
}
