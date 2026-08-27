package com.bitgranules.androidproject.data

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Application.dataStore by preferencesDataStore(name = "background_settings")

class SettingsRepository(private val application: Application) {
    private val jsonEngine = Json { ignoreUnknownKeys = true }

    //    darkMode
    private val darkModeKey = booleanPreferencesKey("is_dark_mode")
    val isDarkMode: Flow<Boolean> =
        application.dataStore.data.map { preferences -> preferences[darkModeKey] ?: true }

    suspend fun setDarkMode(enabled: Boolean) {
        application.dataStore.edit { preferences ->
            preferences[darkModeKey] = enabled
        }
    }

    private val BACKGROUND_IMAGES_KEY = stringSetPreferencesKey("bg_images_list")
    val bgImages: Flow<List<String>> = application.dataStore.data.map { preferences ->
        preferences[BACKGROUND_IMAGES_KEY]?.toList() ?: emptyList()
    }

    suspend fun addMultipleBgImages(uris: List<String>) {
        application.dataStore.edit { preferences ->
            val currentSet = preferences[BACKGROUND_IMAGES_KEY] ?: emptySet<String>()
            val updatedSet = currentSet + uris
            preferences[BACKGROUND_IMAGES_KEY] = updatedSet
        }
    }

    suspend fun deleteBgImage(uri: String) {
        application.dataStore.edit { preferences ->
            val currentSet = preferences[BACKGROUND_IMAGES_KEY] ?: emptySet()
            preferences[BACKGROUND_IMAGES_KEY] = currentSet - uri
        }

    }

    // API
    private val CUSTOM_API_KEY = stringSetPreferencesKey(("custom_api_list"))

    val customApis: Flow<List<CustomApiConfig>> = application.dataStore.data.map { preferences ->
        preferences[CUSTOM_API_KEY]?.map { jsonString ->
            jsonEngine.decodeFromString<CustomApiConfig>(jsonString)
        } ?: emptyList()
    }

    suspend fun addUserApi(config: CustomApiConfig) {
        application.dataStore.edit { preferences ->
            val currentSet = preferences[CUSTOM_API_KEY] ?: emptySet()
            val serializedStr = jsonEngine.encodeToString(config)
            preferences[CUSTOM_API_KEY] = currentSet + serializedStr
        }
    }

    suspend fun deleteUserApi(config: CustomApiConfig) {
        application.dataStore.edit { preferences ->
            val currentSet = preferences[CUSTOM_API_KEY] ?: emptySet()
            val targetSerialized = jsonEngine.encodeToString(config)
            preferences[CUSTOM_API_KEY] = currentSet - targetSerialized
        }
    }
    //Quotes
    private val CACHED_QUOTES_KEY = stringPreferencesKey("cached_quotes_json")
    val cachedQuotes: Flow<List<QuoteStruct>> = application.dataStore.data.map { preferences ->
        val jsonString = preferences[CACHED_QUOTES_KEY]
        if (!jsonString.isNullOrEmpty()) {
            try {
                jsonEngine.decodeFromString<List<QuoteStruct>>(jsonString)
            } catch (e: Exception) {
                emptyList() // Fallback if data format gets corrupted
            }
        } else {
            emptyList()
        }
    }

    // 2. Write function: Serializes memory list to raw storage disk payload
    suspend fun saveQuoteCache(quotes: List<QuoteStruct>) {
        application.dataStore.edit { preferences ->
            val serializedStr = jsonEngine.encodeToString(quotes)
            preferences[CACHED_QUOTES_KEY] = serializedStr
        }
    }
    //adMob

}
