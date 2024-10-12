package com.sg.simplekanban.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.sg.simplekanban.R

@Composable
fun MyProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.dark_transparent)),
        contentAlignment = Alignment.Center,
    ){
        CircularProgressIndicator()
    }
}