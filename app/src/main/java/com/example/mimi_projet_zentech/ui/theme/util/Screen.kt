package com.example.mimi_projet_zentech.ui.theme.util

import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus


sealed class Screen (val route :String) {
    object Splash : Screen("splash")

    object  Login : Screen("login"  )
    object  Home : Screen("home/{email}"  ){
        fun fullRoute(email : String):String{
        return "home/$email"
        }
    }
    object ScannerScreen : Screen("screen/{buisnesIs}"){
        fun fullRoute(id: Int?):String{
            return "screen/$id"
        }
    }
    object  DeniedScreen : Screen("denied")
    object  Profile :Screen("profile")
//    object ScanStatus : Screen("statusScan")
    object  ScanRes : Screen("statusScan/{buisnisId}/{ticketNum}/{scanRes}"){
        fun getRoute(buisnisId: Int?, ticketNum: String?, scanRes: ScanStatus):String {
        return "statusScan/$buisnisId/$ticketNum/$scanRes"
        }
    }
    }

