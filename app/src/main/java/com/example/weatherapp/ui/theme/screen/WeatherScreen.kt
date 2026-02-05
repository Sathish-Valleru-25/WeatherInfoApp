package com.example.weatherapp.ui.theme.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.ui.theme.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.theme.uiComponents.WeatherInfoSearch
import com.example.weatherapp.ui.theme.utils.getCityName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val lastCity by viewModel.lastCity.collectAsStateWithLifecycle()
    var cityQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var cityName: String

    // Handle location permission request
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                hasLocationPermission = true
            } else {
                cityQuery = if(lastCity?.isNotEmpty() == true) lastCity ?: "" else ""
                viewModel.loadLastCity() // fetch last stored city weather info from database if available

            }
        }
    )

    // Request location permission when the composable is first displayed
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            cityName = getCityName(context) ?: ""
            viewModel.searchCity(city = cityName)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Weather App") }
            )
        }
    ) { paddingValues ->
        WeatherInfoSearch(viewModel, cityQuery, paddingValues)
    }
}



