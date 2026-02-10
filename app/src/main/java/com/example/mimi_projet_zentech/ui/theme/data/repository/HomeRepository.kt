package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup


class HomeRepository {

    fun getBusinessGroups(): List<BusinessGroup> {
        return listOf(
            BusinessGroup(
                name = "Trap Vegan",
                offices = listOf("Main Office", "Branch A", "Branch B")
            ),
            BusinessGroup(
                name = "The Perfect Pita",
                offices = listOf("Warehouse")
            ),
            BusinessGroup(
                name = "Papa Locos",
                offices = listOf("VCC Office", "Branch 1")
            ) ,
            BusinessGroup(name = "VCC OFFICE" , offices = listOf("VCC Main Office" , "VCC Branch 1" , "VCC Branch 2"))

        )
    }
}
