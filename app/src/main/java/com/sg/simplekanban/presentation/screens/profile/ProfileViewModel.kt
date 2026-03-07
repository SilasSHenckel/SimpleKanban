package com.sg.simplekanban.presentation.screens.profile

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.domain.usecase.AuthUseCase
import com.sg.simplekanban.domain.usecase.UserUseCase
import com.sg.simplekanban.presentation.base.BaseViewModel
import com.sg.simplekanban.presentation.navigation.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase
): BaseViewModel() {

    private val _isShowingDeleteAccountDialog = MutableStateFlow(false)
    val isShowingDeleteAccountDialog = _isShowingDeleteAccountDialog.asStateFlow()

    var currentUser : User? = null

    var userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        getCurrentUser()
    }

    fun setShowDeleteAccountDialog(show: Boolean){
        _isShowingDeleteAccountDialog.value = show
    }

    private fun getCurrentUser() {
        launchWithLoading {
            if(userId != null){
                userUseCase.getUser(
                    userId!!,
                    onError = {
                        stopLoading()
                        print(it.message)
                    },
                    onSuccess = {
                        stopLoading()
                        currentUser = it
                    }
                )
            }
        }
    }

    fun getUserName() : String{
        return currentUser?.name ?: (FirebaseAuth.getInstance().currentUser?.displayName ?: "")
    }

    fun getUserEmail() : String{
        return currentUser?.email ?: (FirebaseAuth.getInstance().currentUser?.email ?: "")
    }

    fun signOut(nav: NavHostController) = viewModelScope.launch {
        launchWithLoading {
            authUseCase.signOut(
                onError = {
                    stopLoading()
                    print(it.message)
                },
                onSuccess = {
                    stopLoading()
                    nav.navigate(AppScreen.Auth.name) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }

    fun deleteAccount(nav: NavHostController, setShowDialog: (Boolean) -> Unit) {
       launchWithLoading {
           authUseCase.deleteAccount( onError = {
               stopLoading()
               print(it.message)
           },
               onSuccess = {
                   stopLoading()

                   setShowDialog(false)

                   nav.navigate(AppScreen.Auth.name) {
                       launchSingleTop = true
                       popUpTo(0) { inclusive = true }
                   }
               }
           )
       }
    }
}