package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.model.Ticket.TicketInfos
import com.example.mimi_projet_zentech.data.remote.AuthApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class TicketRepository @Inject constructor (@Named("private") private val api: AuthApi) {
    suspend fun checkTicket(ticketNum : String): Response<TicketInfos> {
        return api.checkTicket(ticketNum)
    }
}