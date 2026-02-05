package com.example.weatherapp.ui.theme

import com.example.weatherapp.data.model.Clouds
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.Main
import com.example.weatherapp.data.model.Sys
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.model.Wind
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.theme.viewmodel.WeatherUiState
import com.example.weatherapp.ui.theme.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `searchCity success updates uiState to Success`() = runTest {
        // Given
        val cityName = "Los Angeles"
        val weatherResponse = createFakeWeatherResponse(cityName)
        whenever(repository.getWeatherByCity(cityName)).thenReturn(weatherResponse)
        whenever(repository.lastSearchedCity).thenReturn(flowOf(cityName))

        // When
        viewModel.searchCity(cityName)

        // Then
        advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertTrue(finalState is WeatherUiState.Success)
        assertEquals(weatherResponse, (finalState as WeatherUiState.Success).weather)
    }

    @Test
    fun `searchCity error updates uiState to Error`() = runTest {
        // Given
        val cityName = "InvalidCity"
        val errorMessage = "City not found"
        whenever(repository.getWeatherByCity(cityName)).thenThrow(RuntimeException(errorMessage))
        whenever(repository.lastSearchedCity).thenReturn(flowOf(cityName))


        // When
        viewModel.searchCity(cityName)

        // Then
        advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertTrue(finalState is WeatherUiState.Error)
        assertEquals(errorMessage, (finalState as WeatherUiState.Error).message)
    }

    @Test
    fun `loadLastCity with valid city triggers search and updates state`() = runTest {
        // Given
        val lastCity = "Los Angles"
        val weatherResponse = createFakeWeatherResponse(lastCity)
        whenever(repository.lastSearchedCity).thenReturn(flowOf(lastCity))
        whenever(repository.getWeatherByCity(lastCity)).thenReturn(weatherResponse)
        viewModel = WeatherViewModel(repository)

        // When
        viewModel.loadLastCity()

        // Then
        advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertTrue(finalState is WeatherUiState.Success)
        assertEquals(weatherResponse, (finalState as WeatherUiState.Success).weather)
    }

    @Test
    fun `loadLastCity with null city does nothing`() = runTest {
        // Given
        whenever(repository.lastSearchedCity).thenReturn(flowOf(null))
        viewModel = WeatherViewModel(repository)

        // When
        viewModel.loadLastCity()

        // Then
        advanceUntilIdle()
        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `loadLastCity with empty city does nothing`() = runTest {
        // Given
        whenever(repository.lastSearchedCity).thenReturn(flowOf(""))
        viewModel = WeatherViewModel(repository)

        // When
        viewModel.loadLastCity()

        // Then
        advanceUntilIdle()
        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }
}

// Helper function to create a dummy WeatherResponse
fun createFakeWeatherResponse(cityName: String) = WeatherResponse(
    id = 2643743,
    coord = Coord(lon = -0.1257, lat = 51.5085),
    weather = listOf(Weather(id = 804, main = "Clouds", description = "overcast clouds", icon = "04d")),
    base = "stations",
    main = Main(temp = 285.0, feelsLike = 284.0, tempMin = 284.0, tempMax = 286.0, pressure = 1012, humidity = 87, seaLevel = 1012, grndLevel = 1012),
    visibility = 10000,
    wind = Wind(speed = 1.5, deg = 0, gust = 2.0),
    clouds = Clouds(all = 100),
    dt = 1661870400,
    sys = Sys(type = 2, id = 2075535, country = "GB", sunrise = 1661834187, sunset = 1661882592),
    timezone = 3600,
    name = cityName,
    cod = 200
)
