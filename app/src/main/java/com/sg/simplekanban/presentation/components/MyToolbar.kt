package com.sg.simplekanban.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R

@Composable
fun MyToolBar(
    title: String,
    popBackStack : () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = colorResource(id = R.color.menu_background))
            .padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        IconButton(
            onClick = popBackStack
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = colorResource(id = R.color.title)
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = title,
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )
    }
}