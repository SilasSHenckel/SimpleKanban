package com.sg.simplekanban.presentation.screens.home.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.sg.simplekanban.presentation.theme.MenuBackgroundDark
import com.sg.simplekanban.presentation.theme.MenuBackgroundGrey

@SuppressLint("ContextCastToActivity")
@Composable
fun StatusBarColor(){

    val statusBarLight = MenuBackgroundGrey
    val statusBarDark = MenuBackgroundDark
    val navigationBarLight = android.graphics.Color.BLACK
    val navigationBarDark = android.graphics.Color.BLACK
    val isDarkMode = isSystemInDarkTheme()
    val context = LocalContext.current as ComponentActivity

    DisposableEffect(isDarkMode) {
        context.enableEdgeToEdge(
            statusBarStyle = if (!isDarkMode) {
                SystemBarStyle.light(
                    statusBarLight.hashCode(),
                    statusBarDark.hashCode()
                )
            } else {
                SystemBarStyle.dark(
                    statusBarDark.hashCode()
                )
            },
            navigationBarStyle = if(!isDarkMode){
                SystemBarStyle.light(
                    navigationBarLight,
                    navigationBarDark
                )
            } else {
                SystemBarStyle.dark(navigationBarDark)
            }
        )

        onDispose { }
    }
}