package com.sg.simplekanban.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sg.simplekanban.R
import com.sg.simplekanban.presentation.navigation.AppScreen
import com.sg.simplekanban.presentation.screens.auth.components.GoogleAuth
import com.sg.simplekanban.presentation.screens.auth.components.GoogleButton
import com.sg.simplekanban.presentation.screens.auth.components.LoginAnimation
import com.sg.simplekanban.presentation.screens.auth.components.LoginImage

@Composable
fun AuthScreen (
    nav: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
){

    AuthScreenContent(
        oneTapSignIn = { authViewModel.oneTapSignIn() }
    )

    GoogleAuth(navigateToHome = { nav.navigate(AppScreen.Home.name)}, authViewModel)

}

@Composable
fun AuthScreenContent(
    oneTapSignIn: () -> Unit
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

        GoogleButton { oneTapSignIn() }
    }
}

@Preview
@Composable
fun AuthScreenContentPreview(){
    Surface {
        AuthScreenContent(
           oneTapSignIn = {}
        )
    }
}