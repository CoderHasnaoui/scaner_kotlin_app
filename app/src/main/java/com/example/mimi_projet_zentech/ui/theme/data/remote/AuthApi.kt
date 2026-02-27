package com.example.mimi_projet_zentech.ui.theme.data.remote

import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginResponse
import com.example.mimi_projet_zentech.ui.theme.data.model.Ticket.TicketInfos
import com.example.mimi_projet_zentech.ui.theme.data.model.UserProfile.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("users/group-merchants")
    suspend fun getMerchants(): Response<List<MerchantGroup>>

    @GET("users/me")
    suspend fun getProfile(): Response<UserProfileResponse>

    @GET("public/check-eticket/{ticketNum}")
    suspend fun checkTicket(
        @Path("ticketNum") ticketNum: String?
    ): Response<TicketInfos>

    @GET("group-merchants/{slug}/locations")
    suspend fun getLocations(
        @Path("slug") slug: String
    ): Response<Any>
}