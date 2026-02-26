package com.example.mimi_projet_zentech.ui.theme.data.model.Login

import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
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
    suspend fun getMerchantBySlug(slug: String): MerchantGroup? {
        val response = getMerchants()
        return if (response.isSuccessful) {
            response.body()?.find { it.slug == slug }
        } else {
            null
        }
    }
    @GET("group-merchants/{slug}/locations")
    suspend fun getLocations(
        @Path("slug") slug: String
    ): Response<Any>
    @GET("users/me")
    suspend fun getProfile(): Response<UserProfileResponse>

    @GET("public/check-eticket/{ticketNum}")
    suspend fun checkTicket(
        @Path("ticketNum") ticketNum: String?
    ):Response<TicketInfos>

}