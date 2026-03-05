package com.example.mimi_projet_zentech.ui.theme

import android.content.Context
import androidx.core.content.edit

class ThemeRepository(context: Context) {

        private val prefs = context.getSharedPreferences(AppSettings.DARK_MODE, Context.MODE_PRIVATE)

        fun isDarkMode(): Boolean {
            return prefs.getBoolean(AppSettings.IS_DARK_MODE, false)
        }

        fun setDarkMode(enabled: Boolean) {
            prefs.edit { putBoolean(AppSettings.IS_DARK_MODE  , enabled) }
        }

}