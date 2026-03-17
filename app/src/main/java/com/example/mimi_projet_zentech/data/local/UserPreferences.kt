package com.example.mimi_projet_zentech.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object UserKeys {
    val NAME = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
}
class UserRepository(private val dataStore: DataStore<Preferences>) {
    val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")


    val name: Flow<String> = dataStore.data
        .map { it[UserKeys.NAME] ?: "" }

    val email: Flow<String> = dataStore.data
        .map { it[UserKeys.EMAIL] ?: "" }

    suspend fun saveUserInfo(name: String, email: String) {
        dataStore.edit { preferences ->
            preferences[UserKeys.NAME] = name
            preferences[UserKeys.EMAIL] = email
        }
    }
    val BIOMETRIC_ASKED = booleanPreferencesKey("biometric_asked")
    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    fun isBiometricEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }
    }
    suspend fun setBiometricAsked(asked: Boolean) {
        dataStore.edit { it[BIOMETRIC_ASKED] = asked }
    }

    fun isBiometricAsked(): Flow<Boolean> {
        return dataStore.data.map { it[BIOMETRIC_ASKED] ?: false }
    }
}