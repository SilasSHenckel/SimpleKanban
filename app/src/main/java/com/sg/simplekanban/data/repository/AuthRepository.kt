package com.sg.simplekanban.data.repository

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.CREATED_AT
import com.sg.simplekanban.data.constants.Constants.Companion.EMAIL
import com.sg.simplekanban.data.constants.Constants.Companion.NAME
import com.sg.simplekanban.data.constants.Constants.Companion.PHOTO_URL
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_IN_REQUEST
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_UP_REQUEST
import com.sg.simplekanban.data.constants.Constants.Companion.TABLE_USER
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.Kanban
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
    private var signInClient: GoogleSignInClient,
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
            val response = db.collection(TABLE_USER).document(uid).collection(Constants.TABLE_KANBAN).add(Kanban(name = "Kanban 1", shared = false, creationDate = DateUtil.getCurrentDateFormated())).await()
            db.collection(TABLE_USER).document(uid).collection(Constants.TABLE_KANBAN).document(response.id).collection(Constants.TABLE_COLUMN).add(Column(name = "TO DO", priority = 0)).await()
            db.collection(TABLE_USER).document(uid).collection(Constants.TABLE_KANBAN).document(response.id).collection(Constants.TABLE_COLUMN).add(Column(name = "DOING", priority = 1)).await()
            db.collection(TABLE_USER).document(uid).collection(Constants.TABLE_KANBAN).document(response.id).collection(Constants.TABLE_COLUMN).add(Column(name = "DONE", priority = 2)).await()
        }
    }

    suspend fun signOut(onError: (Throwable) -> Unit, onSuccess: () -> Unit) {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun FirebaseUser.toUser() = mapOf(
        NAME to displayName,
        EMAIL to email,
        PHOTO_URL to photoUrl?.toString(),
        CREATED_AT to serverTimestamp()
    )

    suspend fun deleteAccount(onError: (Throwable) -> Unit, onSuccess: () -> Unit) {
        return try {
            auth.currentUser?.apply {
                db.collection(Constants.TABLE_USER).document(uid).delete().await()
                signInClient.revokeAccess().await()
                oneTapClient.signOut().await()
                delete().await()
            }
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

}