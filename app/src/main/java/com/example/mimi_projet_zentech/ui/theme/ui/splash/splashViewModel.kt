package com.example.mimi_projet_zentech.ui.theme.ui.splash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.camera.core.impl.utils.ContextUtil.getApplication
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.model.UserProfile.UserProfileResponse
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.UserRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class splashViewModel (application: Application): AndroidViewModel(application) {
    private val _userState  = MutableStateFlow<UserState>(UserState.Loading)
    val userState : StateFlow<UserState> = _userState
    private val userRepository = UserRepository(getApplication<Application>().dataStore)
     val tokenManager  = TokenManager(getApplication())
//    val userName: StateFlow<String> = userRepository.name
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = "Loading..."
//        )
//
//    val userEmail: StateFlow<String> = userRepository.email
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = "Loading..."
//        )
    init {
        checkLoginStatus()
    }
    private fun checkLoginStatus(){
        viewModelScope.launch{
//            val sharedPref = application.getSharedPreferences(
//                SignInStrings.PRE_LOGGIN_NAME,
//                Context.MODE_PRIVATE
//            )
//            val isLoggedIn = sharedPref.getBoolean(SignInStrings.PRE_IS_LOGGED_IN, false)
            val isLoggedIn = tokenManager.isLoggedIn()

            if (isLoggedIn ) {
                _userState.value = UserState.LoginIn
            } else {
                _userState.value = UserState.Logout
            }
        }

    }

}