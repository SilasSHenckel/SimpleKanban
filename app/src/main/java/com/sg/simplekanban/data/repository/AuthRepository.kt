package com.sg.simplekanban.data.repository

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.simplekanban.data.constants.Constants.Companion.CREATED_AT
import com.sg.simplekanban.data.constants.Constants.Companion.EMAIL
import com.sg.simplekanban.data.constants.Constants.Companion.NAME
import com.sg.simplekanban.data.constants.Constants.Companion.PHOTO_URL
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_IN_REQUEST
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_UP_REQUEST
import com.sg.simplekanban.data.constants.Constants.Companion.TABLE_USER
import com.sg.simplekanban.data.model.response.Response
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

typealias OneTapSignInResponse = Response<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<Boolean>

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseFirestore
) {

     val isUserAuthenticatedInFirebase = auth.currentUser != null

     suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Response.Success(signUpResult)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                addUserToFirestore()
            }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    private suspend fun addUserToFirestore() {
        auth.currentUser?.apply {
            val user = toUser()
            db.collection(TABLE_USER).document(uid).set(user).await()
        }
    }

    fun FirebaseUser.toUser() = mapOf(
        NAME to displayName,
        EMAIL to email,
        PHOTO_URL to photoUrl?.toString(),
        CREATED_AT to serverTimestamp()
    )

}