package com.example.mimi_projet_zentech.ui.theme.data.repository

import android.util.Log
import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.model.TicketInfo
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository.Companion.businesses


class HomeRepository {

    companion object {

    val businesses: List<BusinessGroup> = listOf(
        BusinessGroup.create("Trap Vegan", listOf("Main Office"), trapVeganTickets),
        BusinessGroup.create("The Perfect Pita", listOf("Warehouse", "blackHous" , "white house" , "Karma"), perfectPitaTickets),
        BusinessGroup.create("Papa Locos", listOf("VCC Office" , "VLL Office" ,"LLC Office "), papaLocosTicket),
        BusinessGroup.create("Amazon Lily", listOf("lily Office" , "vigabank Office" ,"kobi sinsho "), papaLocosTicket)
    )
    }
    fun getBusinessGroups() = businesses

    fun scanTicket(
        businessId: Int?,
        orderNumber: String
    ): ScanStatus {
        val searchId = businessId // Since we know it's an Int now from Logcat

        // 1. Find Business
        val business = businesses.find { it.id == searchId }
            ?: return ScanStatus.NOT_FOUND

        // 2. DEBUG: Print every ticket in THIS business
        Log.d("SCAN_TEST", "--- Checking Tickets for ${business.name} ---")
        business.tickets.forEach {
            Log.d("SCAN_TEST", "Ticket in List: '${it.orderNumber}'")
        }
        Log.d("SCAN_TEST", "Scanned Ticket: '$orderNumber'")

        // 3. Find Ticket
        // Use .trim() to ensure no hidden spaces break the match
        val ticket = business.tickets.find{it.orderNumber==orderNumber}?:return ScanStatus.NOT_FOUND
//        val ticket = business.tickets.find { listTicket ->
//            orderNumber.contains(listTicket.orderNumber) ||
//                    listTicket.orderNumber.contains(orderNumber)
//        } ?: return ScanStatus.NOT_FOUND

        // 4. Validate
        if (ticket.isScanned) return ScanStatus.ALREADY_SCANNED

        ticket.isScanned = true
        return ScanStatus.VALID
    }
    fun getTicke(buisnesId: Int?, ticketNum: String?): TicketInfo? {
        // Debug Tip: Use find { it.id == buisnesId }
        val business = businesses.find { it.id == buisnesId }
        return business?.tickets?.find { it.orderNumber == ticketNum }
    }
    fun getBuisnesById(id:Int?): BusinessGroup? {
        return  businesses.find { it.id == id }
    }


}
