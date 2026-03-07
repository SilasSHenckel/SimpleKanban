package com.sg.simplekanban.domain.repository

import com.google.firebase.auth.AuthCredential
import com.sg.simplekanban.data.repository.OneTapSignInResponse
import com.sg.simplekanban.data.repository.SignInWithGoogleResponse

interface AuthRepository {

    fun isUserAuthenticatedInFirebase() : Boolean

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse

    suspend fun signOut(onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    suspend fun deleteAccount(onError: (Throwable) -> Unit, onSuccess: () -> Unit)

}