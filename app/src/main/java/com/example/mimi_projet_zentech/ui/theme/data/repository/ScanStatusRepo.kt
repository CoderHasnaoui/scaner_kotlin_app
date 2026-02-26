package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.TicketInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentFormattedDate(): String {
    val formatter = SimpleDateFormat("EEE,MMM dd|hh:mm a", Locale.getDefault())
    return formatter.format(Date())
}


val trapVeganTickets = mutableListOf<TicketInfo>(
    TicketInfo(
        name = "Alice",
        peopleCount = "2",
        price = "$40",
        dateTime = getCurrentFormattedDate(),
        orderNumber = "TV-001"
    ),
    TicketInfo(
        name = "Bob",
        peopleCount = "1",
        price = "$20",
        dateTime = getCurrentFormattedDate(),
        orderNumber = "TV-002"
    ),
    TicketInfo(
        name = "Charlie",
        peopleCount = "4",
        price = "$80",
        dateTime = getCurrentFormattedDate(),
        orderNumber = "TV-003"
    )
)
val perfectPitaTickets = listOf(
    TicketInfo(
        name = "David",
        peopleCount = "3",
        price = "$60",
        dateTime = getCurrentFormattedDate(),
        orderNumber = "PP-001"
    ),
    TicketInfo(
        name = "Khalil",
        peopleCount = "6",
        price = "$100",
        dateTime = getCurrentFormattedDate(),
        orderNumber = "PP-002"
    )
)
val papaLocosTicket = listOf(
    TicketInfo(
        name = "Indriyani Puspita",
        dateTime = getCurrentFormattedDate(),
        peopleCount = "32",
        price = "$320.00",
        orderNumber = "PPL-001"
    ),
    TicketInfo(
        name = "Zentech Admin",

        dateTime = getCurrentFormattedDate(),
        peopleCount = "01",
        price = "$10.00",
        orderNumber = "PPL-002"
    ),
    TicketInfo(
        name = "John Doe",
        dateTime = getCurrentFormattedDate(),
        peopleCount = "05",
        price = "$50.00",
        orderNumber = "PPL-003"
    )
)