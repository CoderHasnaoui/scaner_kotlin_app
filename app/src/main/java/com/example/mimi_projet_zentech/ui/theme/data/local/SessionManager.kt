package com.example.mimi_projet_zentech.ui.theme.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SessionManager {
    var onTokenExpired: (() -> Unit)? = null  // just a variable that holds a function

    fun notifyTokenExpired() {
        CoroutineScope(Dispatchers.Main).launch {
            onTokenExpired?.invoke()
        }
    }
}