package com.example.mimi_projet_zentech.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SessionManager {
    var onTokenExpired: (() -> Unit)? = null

    fun notifyTokenExpired() {
        CoroutineScope(Dispatchers.Main).launch {
            onTokenExpired?.invoke()
        }
    }
}