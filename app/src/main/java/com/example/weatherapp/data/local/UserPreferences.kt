package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Class that handles saving and retrieving user preferences.
 */
@Singleton
class UserPreferences @Inject constructor(@param:ApplicationContext private val context: Context) {

    private val LAST_CITY_KEY = stringPreferencesKey("last_city")

    val lastCity: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_CITY_KEY]
        }

    suspend fun saveLastCity(city: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_CITY_KEY] = city
        }
    }
}
