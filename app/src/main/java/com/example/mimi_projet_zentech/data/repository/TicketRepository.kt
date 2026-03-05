package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.model.Ticket.TicketInfos
import com.example.mimi_projet_zentech.data.remote.AuthApi
import retrofit2.Response

class TicketRepository (private val api: AuthApi) {
    suspend fun checkTicket(ticketNum : String): Response<TicketInfos> {
        return api.checkTicket(ticketNum)
    }
}