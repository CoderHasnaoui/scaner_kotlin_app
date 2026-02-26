package com.example.mimi_projet_zentech.ui.theme.data.local

import android.content.Context

class TokenManager(context: Context) {
    // We get the sharedPref once here
    private val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)


    fun getToken(): String? {
        return sharedPref.getString("USER_TOKEN", null)
    }

    fun clearToken() {
        sharedPref.edit()
            .remove("USER_TOKEN")
            .remove("USER_EMAIL")
            .remove("IS_LOGGED_IN")
            .remove("SLUG")
            .apply()
    }
    fun saveUserData(email: String, token: String) {
        sharedPref.edit()
            .putString("USER_TOKEN", token)
            .putString("USER_EMAIL", email)
            .putBoolean("IS_LOGGED_IN", true)
            .apply()
    }
    fun saveSlug(slug :String){
        sharedPref.edit()
            .putString("SLUG" , slug)
            .apply()
    }
    fun clearSelectedSlug() {
        sharedPref.edit().remove("SELECTED_SLUG").apply()
    }
    fun getSlug() : String? = sharedPref.getString("SLUG" , null)

    fun isLoggedIn(): Boolean = sharedPref.getBoolean("IS_LOGGED_IN", false)

    fun getEmail(): String? = sharedPref.getString("USER_EMAIL", null)
}