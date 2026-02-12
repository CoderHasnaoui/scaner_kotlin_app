package com.example.mimi_projet_zentech.ui.theme.data.model

import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
data class TicketInfo(
    val name: String,
    val peopleCount: String,
    val price: String,
    val orderNumber: String ,
    val dateTime: String,
    var isScanned: Boolean = false
)



