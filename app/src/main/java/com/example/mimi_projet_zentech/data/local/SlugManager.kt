package com.example.mimi_projet_zentech.data.local

import android.content.Context
import com.example.mimi_projet_zentech.ui.theme.TokenStrings
import androidx.core.content.edit

class SlugManager (private val context: Context){
    // get Slug
    fun getSlug(): String? {
        return context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .getString(TokenStrings.SELECTE_SLUG, null)
    }

// clear Selected Slug
    fun clearSelectedSlug() {
        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit {
                remove(TokenStrings.SELECTE_SLUG)
            }
    }
    // Save Slug
    fun saveSlug(slug: String) {
        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(TokenStrings.SELECTE_SLUG, slug)
            }
    }
}