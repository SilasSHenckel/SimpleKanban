package com.sg.simplekanban.presentation.screens.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.response.Response
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.navigation.AppScreen
import com.sg.simplekanban.presentation.screens.auth.components.GoogleButton
import com.sg.simplekanban.presentation.screens.auth.components.LoginAnimation
import com.sg.simplekanban.presentation.screens.auth.components.LoginImage

@Composable
fun AuthScreen (
    nav: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
){

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.padding(bottom = 80.dp),
            text = stringResource(id = R.string.app_name).uppercase(),
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp
        )

        Box(
            contentAlignment = Alignment.Center
        ){
            LoginAnimation()
            LoginImage()
        }

        Text(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 80.dp, bottom = 40.dp),
            text = stringResource(id = R.string.select_account),
            color = colorResource(id = R.color.text),
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        GoogleButton { authViewModel.oneTapSignIn() }
    }

    GoogleAuth(navigateToHome = { nav.navigate(AppScreen.Home.name)}, authViewModel)

}

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