package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus


class HomeRepository {



    private val businesses: List<BusinessGroup> = listOf(
            BusinessGroup.create(
                name = "Trap Vegan",
                offices = listOf("Main Office", "Branch A", "Branch B") ,
                tickets = trapVeganTickets

            ),

            BusinessGroup.create(
                name = "The Perfect Pita",
                offices = listOf("Warehouse") ,
                tickets = perfectPitaTickets
            ),

            BusinessGroup.create(
                name = "Papa Locos",
                offices = listOf("VCC Office", "Branch 1") ,
                tickets = papaLocosTicket
            ) ,
            BusinessGroup.create(name = "VCC OFFICE" , offices = listOf("VCC Main Office" , "VCC Branch 1" , "VCC Branch 2"))

        )
    fun getBusinessGroups(): List<BusinessGroup> {
        return businesses
    }

    fun scanTicket(
        businessId: Int?,
        orderNumber: String
    ): ScanStatus {

//         find business
      val business = businesses.find { it.id == businessId }
            ?: return ScanStatus.NOT_FOUND

//       find ticket
        val ticket = business.tickets.find { it.orderNumber == orderNumber }
            ?: return ScanStatus.NOT_FOUND

        // check if already scanned
        if (ticket.isScanned) {
            return ScanStatus.ALREADY_SCANNED
        }

        // mark as scanned
        ticket.isScanned = true

        return ScanStatus.VALID
    }


}
