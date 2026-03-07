package com.sg.simplekanban.presentation.screens.profile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sg.simplekanban.R
import com.sg.simplekanban.presentation.theme.Red

@Composable
fun DeleteAccountButton(
    onClick: () -> Unit
){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        onClick = {
            onClick()
        },
        colors = ButtonColors(Red, Color.White, Red, Color.White),

        ) {
        Text(
            text = stringResource(id = R.string.delete_account).uppercase(),
            fontWeight = FontWeight.Bold,
        )
    }
}