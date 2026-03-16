package com.example.mimi_projet_zentech.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.mimi_projet_zentech.ui.theme.TokenStrings
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SlugManager (private val context: Context){
    // get Slug
    fun getSlug(): String? {
        return context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .getString(TokenStrings.SELECTE_SLUG, null)
    }

// clear Selected Slug
    fun clearSelectedSlug() {
        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit()
            . remove(TokenStrings.SELECTE_SLUG)
            .commit()

    }

    // Save Slug
    fun saveSlug(slug: String) {
        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TokenStrings.SELECTE_SLUG, slug)
            .commit()
    }


}