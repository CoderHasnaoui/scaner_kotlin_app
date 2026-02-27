package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
}