package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface that defines the API for retrieving weather data.
 */
interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
    ): WeatherResponse

}
