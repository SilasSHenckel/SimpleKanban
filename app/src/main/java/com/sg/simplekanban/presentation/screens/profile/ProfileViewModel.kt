package com.sg.simplekanban.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.domain.AuthUseCase
import com.sg.simplekanban.domain.UserUseCase
import com.sg.simplekanban.presentation.navigation.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var isShowingDeleteAccountDialog by mutableStateOf(false)

    var currentUser : User? = null

    var userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        isLoading = true
        if(userId != null){
            userUseCase.getUser(
                userId!!,
                onError = {
                    isLoading = false
                    print(it.message)
                },
                onSuccess = {
                    isLoading = false
                    currentUser = it
                }
            )
        }
    }

    fun getUserName() : String{
        return currentUser?.name ?: (FirebaseAuth.getInstance().currentUser?.displayName ?: "")
    }

    fun getUserEmail() : String{
        return currentUser?.email ?: (FirebaseAuth.getInstance().currentUser?.email ?: "")
    }

    fun signOut(nav: NavHostController) = viewModelScope.launch {
        isLoading = true
        authUseCase.signOut(
            onError = {
                isLoading = false
                print(it.message)
            },
            onSuccess = {
                isLoading = false
                nav.navigate(AppScreen.Auth.name) {
                    launchSingleTop = true
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    fun deleteAccount(nav: NavHostController, setShowDialog: (Boolean) -> Unit) = viewModelScope.launch {
        isLoading = true
        authUseCase.deleteAccount( onError = {
            isLoading = false
            print(it.message)
        },
            onSuccess = {
                isLoading = false

                setShowDialog(false)

                nav.navigate(AppScreen.Auth.name) {
                    launchSingleTop = true
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}