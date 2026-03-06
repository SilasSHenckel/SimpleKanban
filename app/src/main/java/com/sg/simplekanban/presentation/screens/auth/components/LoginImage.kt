package com.sg.simplekanban.presentation.screens.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sg.simplekanban.R

@Composable
fun LoginImage(){
    Image(
        modifier = Modifier
            .clip(RoundedCornerShape(34.dp))
            .height(130.dp)
            .width(130.dp)
            .padding(top = 20.dp),
        painter = painterResource(id = R.drawable.ic_launcher_playstore),
        contentDescription = "icon",
    )
}