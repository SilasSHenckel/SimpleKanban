package com.sg.simplekanban.ui.screens.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.response.Response
import com.sg.simplekanban.ui.components.MyProgressBar
import com.sg.simplekanban.ui.routes.AppScreen

@Composable
fun AuthScreen (
    nav: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
){

    val isDarkTheme = isSystemInDarkTheme()

    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if(isDarkTheme) R.raw.login_dark else R.raw.login
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name).uppercase(),
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp
        )
        Spacer(modifier = Modifier.height(80.dp))

        Box(
            contentAlignment = Alignment.Center
        ){
            LottieAnimation(
                composition = preloaderLottieComposition,
                progress = preloaderProgress,
                modifier = Modifier.size(280.dp)
            )
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    modifier = Modifier
                        .clip(RoundedCornerShape(34.dp))
                        .height(130.dp)
                        .width(130.dp),
                    painter = painterResource(id = R.drawable.ic_launcher_playstore),
                    contentDescription = "icon",
                )
            }
          
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(id = R.string.select_account),
            color = colorResource(id = R.color.text),
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            modifier = Modifier
                .size(width = 300.dp, height = 60.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.menu_background)
            ),
            onClick = {
                authViewModel.oneTapSignIn()
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = stringResource(id = R.string.sign_in_google),
                modifier = Modifier.padding(start= 15.dp),
                color = colorResource(id = R.color.title),
                fontSize = 18.sp
            )
        }
    }

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

    when(val oneTapSignInResponse = authViewModel.oneTapSignInResponse) {
        is Response.Loading -> MyProgressBar()
        is Response.Success -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) { launch(it) }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(oneTapSignInResponse.e)
        }
    }

    when(val signInWithGoogleResponse = authViewModel.signInWithGoogleResponse) {
        is Response.Loading -> MyProgressBar()
        is Response.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                if (signedIn) { nav.navigate(AppScreen.Home.name) }
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(signInWithGoogleResponse.e)
        }
    }
}