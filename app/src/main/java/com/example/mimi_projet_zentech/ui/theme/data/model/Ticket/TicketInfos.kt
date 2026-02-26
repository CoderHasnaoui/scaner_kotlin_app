package com.example.mimi_projet_zentech.ui.theme.data.model.Ticket

import com.google.gson.annotations.SerializedName

data class TicketInfos (

        @SerializedName("owner_name") val ownerName: String,
    @SerializedName("event_title") val name: String,

    @SerializedName("nb_of_persons") val nbOfPersons: Int,
    @SerializedName("amount")val amount: Double,
    @SerializedName("uuid") val orderNumber : String ,
    @SerializedName("is_checked") val isScanned: Boolean,
//    @SerializedName("is_expired") val isExpired: Boolean,
//    @SerializedName("nb_of_checks") val nbOfChecks: Int,
//
//    @SerializedName("owner_name") val ownerName: String,

    @SerializedName("created_at") val dateTime: String
)