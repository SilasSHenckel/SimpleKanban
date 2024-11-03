package com.sg.simplekanban.ui.routes

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.ui.screens.card.CardScreen
import com.sg.simplekanban.ui.screens.home.HomeScreen
import com.sg.simplekanban.ui.screens.auth.AuthScreen
import com.sg.simplekanban.ui.screens.auth.AuthViewModel
import com.sg.simplekanban.ui.screens.columns.ColumnsScreen
import com.sg.simplekanban.ui.screens.kanban.KanbanScreen
import com.sg.simplekanban.ui.screens.profile.ProfileScreen

@Composable
fun NavigationHost(nav: NavHostController = rememberNavController(), authViewModel: AuthViewModel = hiltViewModel()){

    val startDestination = if(authViewModel.isUserAuthenticated) AppScreen.Home.name else AppScreen.Auth.name

    NavHost(navController = nav, startDestination = startDestination){
        composable(route = AppScreen.Auth.name) {
            AuthScreen(nav)
        }
        composable(route = AppScreen.Home.name){
            HomeScreen(nav = nav)
        }
        composable(route = AppScreen.Card.name + "/{columnId}" ){ params ->
            CardScreen(nav, params.arguments?.getString("columnId") ?: "0")
        }
        composable(route = AppScreen.Columns.name){
            ColumnsScreen(nav = nav)
        }
        composable(route = AppScreen.Kanbans.name) {
            KanbanScreen(nav = nav)
        }
        composable(route = AppScreen.Profile.name) {
            ProfileScreen(nav = nav)
        }
    }

}

enum class AppScreen{
    Auth,
    Home,
    Card,
    Columns,
    Kanbans,
    Profile
}