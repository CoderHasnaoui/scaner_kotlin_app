package com.example.mimi_projet_zentech.ui.theme.ui.splash

import com.example.mimi_projet_zentech.data.model.UserProfile.UserProfileResponse

sealed  class UserState {
    object  Loading : UserState()
    object LoginIn : UserState()
    object  Logout : UserState()

}