package com.example.mimi_projet_zentech.ui.theme.data.model

import java.util.concurrent.atomic.AtomicInteger

data class BusinessGroup(
    val id :Int ,
    val name: String,
    val offices: List<String> ,
    val tickets :  MutableList<TicketInfo> = mutableListOf<TicketInfo>()
){
    companion object {
        private val idGenerator = AtomicInteger(1) // Starts at 1

        // Extension-style helper to create a new group with an auto-id
        fun create(
            name: String, offices: List<String>  ,
            tickets: List<TicketInfo> = emptyList()
        ): BusinessGroup {
            return BusinessGroup(
                id = idGenerator.getAndIncrement(),
                name = name,
                offices = offices  ,
                tickets = tickets.toMutableList()
            )
        }
    }
}