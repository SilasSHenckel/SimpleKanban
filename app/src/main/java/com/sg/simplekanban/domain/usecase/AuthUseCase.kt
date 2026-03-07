package com.sg.simplekanban.domain.usecase

import com.google.firebase.auth.AuthCredential
import com.sg.simplekanban.data.repository.OneTapSignInResponse
import com.sg.simplekanban.data.repository.SignInWithGoogleResponse
import com.sg.simplekanban.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    fun isUserAuthenticatedInFirebase() : Boolean {
        return authRepository.isUserAuthenticatedInFirebase()
    }

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return authRepository.oneTapSignInWithGoogle()
    }

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse {
        return authRepository.firebaseSignInWithGoogle(googleCredential)
    }

    suspend fun signOut(onError: (Throwable) -> Unit, onSuccess: () -> Unit) {
        return authRepository.signOut(onError, onSuccess)
    }

    suspend fun deleteAccount(onError: (Throwable) -> Unit, onSuccess: () -> Unit) {
        return authRepository.deleteAccount(onError, onSuccess)
    }
}