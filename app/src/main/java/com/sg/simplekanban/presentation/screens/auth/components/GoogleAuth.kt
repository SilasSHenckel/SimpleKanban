package com.sg.simplekanban.presentation.screens.auth.components

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.sg.simplekanban.data.model.response.Response
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.screens.auth.AuthViewModel

@Composable
fun GoogleAuth(
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
){

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val credentials = authViewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken
                val googleCredentials = getCredential(googleIdToken, null)
                authViewModel.signInWithGoogle(googleCredentials)
            } catch (it: ApiException) {
                print(it)
            }
        }
    }

    fun launch(signInResult: BeginSignInResult) {
        launcher.launch(IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build())
    }

    val oneTapSignInResponse by authViewModel.oneTapSignInResponse.collectAsStateWithLifecycle()
    val signInWithGoogleResponse by authViewModel.signInWithGoogleResponse.collectAsStateWithLifecycle()

    when (val response = oneTapSignInResponse) {

        is Response.Loading -> MyProgressBar()

        is Response.Success -> response.data?.let {
            LaunchedEffect(it) { launch(it) }
        }

        is Response.Failure -> LaunchedEffect(Unit) {
            print(response.e)
        }
    }

    when (val response = signInWithGoogleResponse) {

        is Response.Loading -> MyProgressBar()

        is Response.Success -> response.data?.let { signedIn ->
            LaunchedEffect(signedIn) { if (signedIn) navigateToHome() }
        }

        is Response.Failure -> LaunchedEffect(Unit) {
            print(response.e)
        }
    }
}