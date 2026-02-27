package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.remote.AuthApi
import com.example.mimi_projet_zentech.ui.theme.data.model.Ticket.TicketInfos
import retrofit2.Response

class TicketRepository (private val api: AuthApi) {
    suspend fun checkTicket(ticketNum : String): Response<TicketInfos> {
        return api.checkTicket(ticketNum)
    }
}