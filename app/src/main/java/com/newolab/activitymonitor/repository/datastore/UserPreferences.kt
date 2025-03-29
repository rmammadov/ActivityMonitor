package com.newolab.activitymonitor.repository.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val datastore: androidx.datastore.core.DataStore<Preferences>
) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token_key")
        val LANGUAGE_KEY = stringPreferencesKey("language_key")
    }

    val token: Flow<String?> = datastore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val language: Flow<String> = datastore.data.map { preferences ->
        // Default to "az" if not set
        preferences[LANGUAGE_KEY] ?: "az"
    }

    suspend fun saveToken(token: String) {
        datastore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        datastore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    // Synchronous token retrieval
    fun getTokenSynchronously(): String? {
        return runBlocking {
            token.firstOrNull()
        }
    }

    suspend fun saveLanguage(lang: String) {
        datastore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }
}

