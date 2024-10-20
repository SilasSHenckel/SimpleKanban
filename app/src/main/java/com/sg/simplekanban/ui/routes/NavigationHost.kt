package com.sg.simplekanban.ui.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.ui.screens.card.CardScreen
import com.sg.simplekanban.ui.screens.home.HomeScreen
import com.sg.simplekanban.ui.screens.auth.AuthScreen
import com.sg.simplekanban.ui.screens.columns.ColumnsScreen

@Composable
fun NavigationHost(nav: NavHostController = rememberNavController()){

    NavHost(navController = nav, startDestination = AppScreen.Auth.name){
        composable(route = AppScreen.Auth.name) {
            AuthScreen(nav)
        }
        composable(route = AppScreen.Home.name){ navBackResult ->
            HomeScreen(nav = nav, navBackStackEntry = navBackResult)
        }
        composable(route = AppScreen.Card.name + "/{columnId}" ){ params ->
            CardScreen(nav, params.arguments?.getString("columnId") ?: "0")
        }
        composable(route = AppScreen.Columns.name){
            ColumnsScreen(nav = nav)
        }
    }

}

enum class AppScreen{
    Auth,
    Home,
    Card,
    Columns
}