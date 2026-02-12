package com.example.mimi_projet_zentech.ui.theme.data.model.Enum


import com.example.mimi_projet_zentech.R

enum class ScanStatus {
    VALID   , ALREADY_SCANNED,NOT_FOUND
}
    fun ScanStatus.getIconDrawable(): Int = when (this) {
        ScanStatus.VALID -> R.drawable.ic_valid // Ticket VAlid Icon
//        ScanStatus.ALREADY_SCANNED -> R.drawable.ic_warning // Already scan Ticket icon
        ScanStatus.ALREADY_SCANNED -> R.drawable.ic_waring // NotFound Ticket Icon
        ScanStatus.NOT_FOUND -> R.drawable.ic_notfound
    }
fun ScanStatus.toTitle():String = when (this) {
    ScanStatus.VALID -> "TICKET VALID"
    ScanStatus.ALREADY_SCANNED -> "TICKET ALREADY SCANNED."
    ScanStatus.NOT_FOUND -> "Tickets Not Found"
}

fun ScanStatus.geBacgound():Int =when(this){
    ScanStatus.VALID ->R.drawable.bg_valid
    ScanStatus.ALREADY_SCANNED ->R.drawable.bg_waring
    ScanStatus.NOT_FOUND -> R.drawable.bg_not_found
}
