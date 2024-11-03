package com.sg.simplekanban.ui.screens.auth


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.sg.simplekanban.data.model.response.Response
import com.sg.simplekanban.data.repository.AuthRepository
import com.sg.simplekanban.data.repository.OneTapSignInResponse
import com.sg.simplekanban.data.repository.SignInWithGoogleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val oneTapClient: SignInClient
): ViewModel() {

    val isUserAuthenticated get() = authRepository.isUserAuthenticatedInFirebase

    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(Response.Success(null))
        private set

    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(Response.Success(false))
        private set

    fun oneTapSignIn() = viewModelScope.launch {
        oneTapSignInResponse = Response.Loading
        oneTapSignInResponse = authRepository.oneTapSignInWithGoogle()
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        oneTapSignInResponse = Response.Loading
        signInWithGoogleResponse = authRepository.firebaseSignInWithGoogle(googleCredential)
    }

}