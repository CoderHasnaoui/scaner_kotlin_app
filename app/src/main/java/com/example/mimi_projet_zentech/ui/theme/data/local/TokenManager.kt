package com.example.mimi_projet_zentech.ui.theme.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {
    // We get the sharedPref once here
    private val sharedPref = EncryptedSharedPreferences.create(
        context,
        "secret_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPref.edit()
            .putString("USER_TOKEN", token)
            .putBoolean("IS_LOGGED_IN", true)
            .apply()
    }
    fun getToken(): String? {
        return sharedPref.getString("USER_TOKEN", null)
    }
    fun clearToken() {
        sharedPref.edit().clear().apply()
    }
    fun saveSlug(slug :String){
        sharedPref.edit()
            .putString("SLUG" , slug)
            .apply()
    }
    fun getSlug() : String? = sharedPref.getString("SLUG" , null)
    fun clearSelectedSlug() {
        sharedPref.edit().remove("SELECTED_SLUG").apply()
    }
    fun isLoggedIn(): Boolean = sharedPref.getBoolean("IS_LOGGED_IN", false)
//    fun getEmail(): String? = sharedPref.getString("USER_EMAIL", null)
}