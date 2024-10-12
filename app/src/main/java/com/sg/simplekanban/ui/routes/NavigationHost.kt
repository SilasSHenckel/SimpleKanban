package com.sg.simplekanban.ui.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.ui.screens.card.CardScreen
import com.sg.simplekanban.ui.screens.home.HomeScreen
import com.sg.simplekanban.ui.screens.auth.AuthScreen

@Composable
fun NavigationHost(nav: NavHostController = rememberNavController()){

    NavHost(navController = nav, startDestination = AppScreen.Auth.name){
        composable(route = AppScreen.Auth.name) {
            AuthScreen(nav)
        }
        composable(route = AppScreen.Home.name){
            HomeScreen(nav)
        }
        composable(route = AppScreen.Card.name + "/{columnId}" ){ params ->
            CardScreen(nav, params.arguments?.getString("columnId") ?: "0")
        }
    }

}

enum class AppScreen{
    Auth,
    Home,
    Card
}