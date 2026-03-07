package com.sg.simplekanban.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.sg.simplekanban.data.model.response.Response
import com.sg.simplekanban.data.repository.OneTapSignInResponse
import com.sg.simplekanban.data.repository.SignInWithGoogleResponse
import com.sg.simplekanban.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    val oneTapClient: SignInClient
): ViewModel() {

    val isUserAuthenticated get() = authUseCase.isUserAuthenticatedInFirebase()

    private val _oneTapSignInResponse = MutableStateFlow<OneTapSignInResponse>(Response.Success(null))
    val oneTapSignInResponse = _oneTapSignInResponse.asStateFlow()

    private val _signInWithGoogleResponse = MutableStateFlow<SignInWithGoogleResponse>(Response.Success(false))
    val signInWithGoogleResponse = _signInWithGoogleResponse.asStateFlow()

    fun oneTapSignIn() = viewModelScope.launch {
        _oneTapSignInResponse.value = Response.Loading
        _oneTapSignInResponse.value = authUseCase.oneTapSignInWithGoogle()
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        _signInWithGoogleResponse.value = Response.Loading
        _signInWithGoogleResponse.value = authUseCase.firebaseSignInWithGoogle(googleCredential)
    }

}