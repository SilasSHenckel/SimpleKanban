package com.sg.simplekanban.presentation.screens.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R

@Composable
fun GoogleButton(
    oneTapSignIn: () -> Unit
){
    Button(
        modifier = Modifier
            .size(width = 300.dp, height = 60.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(R.color.menu_background)
        ),
        onClick = {
            oneTapSignIn()
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