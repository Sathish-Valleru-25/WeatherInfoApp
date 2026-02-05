package com.example.weatherapp.ui.theme.uiComponents

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.ui.theme.viewmodel.WeatherUiState
import com.example.weatherapp.ui.theme.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.theme.utils.isValidInput
import com.example.weatherapp.ui.theme.utils.toFahrenheit
import com.example.weatherapp.ui.theme.utils.toFormattedTime

/**
 * The main composable for the WeatherInfo search screen.
 */
@Composable
fun WeatherInfoSearch(viewModel: WeatherViewModel, cityName: String, paddingValues: PaddingValues) {
    var cityTextFiledInput by remember  { mutableStateOf(cityName) }
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(cityName) {
        if (cityName.isNotEmpty()) {
            cityTextFiledInput = cityName
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(  paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = cityTextFiledInput,
            onValueChange = { cityTextFiledInput = it },
            label = { Text("Enter City Name") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (isValidInput(cityTextFiledInput)) {
                        viewModel.searchCity(cityTextFiledInput)
                        focusManager.clearFocus()
                    } else {
                        Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()

                    }
                }
            ),       singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("ip_input_field"),

            )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (isValidInput(cityTextFiledInput)) {
                    viewModel.searchCity(cityTextFiledInput)
                    focusManager.clearFocus()
                } else {
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()

                }
            },
            // Disable button while loading
            enabled = uiState !is WeatherUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("search_button")
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxSize().padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is WeatherUiState.Idle -> {
                    Text(text = "Enter city name to get weather updates.")
                }
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is WeatherUiState.Success -> {
                    // On success, show the results
                    WeatherInfoResult(weatherInfo = state.weather)
                }
                is WeatherUiState.Error -> {
                    // On error, show the error message
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("error_message_text")
                    )
                }
            }
        }
    }
}

/**
 * A separate composable to display the successful result.
 * This keeps the main screen composable cleaner.
 */
@Composable
fun WeatherInfoResult(weatherInfo: WeatherResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Text(
                text = "City: ${weatherInfo?.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Country: ${weatherInfo?.sys?.country}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Weather Conditions
            if(weatherInfo?.weather?.isNotEmpty() ==true){
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Weather: ",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(120.dp) // Align values
                    )
                    Text(text = weatherInfo.weather[0].main)
                    Spacer(modifier = Modifier.width(4.dp))
                    val iconCode = weatherInfo.weather[0].icon
                    val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

                    AsyncImage(
                        model = iconUrl,
                        contentDescription = "Weather Icon: ${weatherInfo.weather[0].description}",
                        modifier = Modifier.size(30.dp).padding(bottom = 4.dp)
                    )


                }
                InfoRow(label = "Description", value = weatherInfo.weather[0].description)
            }


            // Temperature Info
            InfoRow(label = "Current Temperature", value = "${weatherInfo?.main?.temp?.toFahrenheit() ?: ""}\u00B0F " ,  modifier = Modifier.testTag("current_temp"))
            InfoRow(label = "Feels Like", value = "${weatherInfo?.main?.feelsLike?.toFahrenheit() ?: ""}\u00B0F " ,  modifier = Modifier.testTag("feels_like_temp"))
            InfoRow(label = "Min Temperature", value = "${weatherInfo?.main?.tempMin?.toFahrenheit() ?: ""}\u00B0F " ,  modifier = Modifier.testTag("min_temp"))
            InfoRow(label = "Max Temperature", value = "${weatherInfo?.main?.tempMax?.toFahrenheit() ?: ""}\u00B0F " ,  modifier = Modifier.testTag("max_temp"))

            InfoRow(label = "Pressure", value = weatherInfo?.main?.pressure.toString())
            InfoRow(label = "Humidity", value = weatherInfo?.main?.humidity.toString())
            // Wind Info
            InfoRow(label = "Wind Speed", value = weatherInfo?.wind?.speed.toString())
            InfoRow(label = "Wind Guest", value = weatherInfo?.wind?.gust.toString())
            InfoRow(label = "Wind Speed", value = weatherInfo?.wind?.speed.toString())
            InfoRow(label = "Cloud Cover", value = "${weatherInfo?.clouds?.all ?: ""} %")
            InfoRow(label = "Sunrise", value = weatherInfo?.sys?.sunrise?.toFormattedTime() ?: "")
            InfoRow(label = "Sunset", value = weatherInfo?.sys?.sunset?.toFormattedTime() ?: "")

        }
    }
}



/**
 * A helper composable for displaying a labeled piece of information.
 */
@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(120.dp) // Align values
        )
        Text(text = value)
    }
    Spacer(modifier = Modifier.height(8.dp))
}