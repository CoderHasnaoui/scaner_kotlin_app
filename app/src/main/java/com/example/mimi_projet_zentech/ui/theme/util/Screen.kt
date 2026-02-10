package com.example.mimi_projet_zentech.ui.theme.util



sealed class Screen (val route :String) {
    object Splash : Screen("splash")

    object  Login : Screen("login"  )
    object  Home : Screen("home/{email}"  ){
        fun fullRoute(email : String):String{
        return "home/$email"
        }
    }
}
