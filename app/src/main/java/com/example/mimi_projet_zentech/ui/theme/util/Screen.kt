package com.example.mimi_projet_zentech.ui.theme.util

import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus


sealed class Screen (val route :String) {
    object Splash : Screen("splash")

    object  Login : Screen("login"  )


    object Home : Screen("home/{forceSelect}") {
        fun getRoute(forceSelect: Boolean = false) = "home/$forceSelect"
    }
//    object  Home : Screen("home/{email}"  ){
//        fun fullRoute(email : String):String{
//        return "home/$email"
//        }
//    }
    object ScannerScreen : Screen("screen")
//    object ScannerScreen : Screen("screen/{slug}")
//    {
//        fun fullRoute(slug: String?):String{
//            return "screen/$slug"
//        }
//    }
    object  DeniedScreen : Screen("denied")
//    object DeniedScreen : Screen("denied/{slug}") {
//        fun fullRoute(slug: String?): String {
//            return "denied/$slug"
//        }
//    }
    // Inside your Screen sealed class
    object  Profile : Screen("profile")
//    object Profile : Screen("profile/{buisnesIs}") {
//        fun fullRoute(id: Int?): String {
//            return "profile/$id"
//        }
//    }
//    object ScanStatus : Screen("statusScan")
//    object ScanRes : Screen("statusScan")
    object  ScanRes : Screen("statusScan/{ticketNum}/{scanRes}"){
        fun getRoute( ticketNum: String?, scanRes: ScanStatus):String {
        return "statusScan/$ticketNum/$scanRes"
        }
    }
    }

