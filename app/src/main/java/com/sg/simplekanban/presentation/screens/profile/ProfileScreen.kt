package com.sg.simplekanban.presentation.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sg.simplekanban.presentation.components.DeleteAccountDialog
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.screens.profile.components.DeleteAccountButton
import com.sg.simplekanban.presentation.screens.profile.components.ProfileToolbar

@Composable
fun ProfileScreen(
    nav: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    Box (
        modifier = Modifier.fillMaxSize()
    ){

        ProfileScreenContent (
            photoUrl = profileViewModel.currentUser?.photoUrl,
            userName = profileViewModel.getUserName(),
            userEmail = profileViewModel.getUserEmail(),
            onBackClick = { nav.popBackStack() },
            onLogoutClick = { profileViewModel.signOut(nav) },
            onClickDeleteAccount = { profileViewModel.setShowDeleteAccountDialog(true) }
        )

        if(profileViewModel.isShowingDeleteAccountDialog.collectAsStateWithLifecycle().value){
            DeleteAccountDialog(
                profileViewModel = profileViewModel,
                setShowDialog = { profileViewModel.setShowDeleteAccountDialog(it) },
                nav = nav
            )
        }

        if(profileViewModel.isLoading.collectAsStateWithLifecycle().value){
            MyProgressBar()
        }
    }
}

@Composable
fun ProfileScreenContent(
    userName: String,
    userEmail: String,
    photoUrl: String?,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClickDeleteAccount: () -> Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ProfileToolbar(
            photoUrl = photoUrl,
            userName = userName,
            userEmail = userEmail,
            onBackClick = onBackClick,
            onLogoutClick = onLogoutClick
        )

        Spacer(modifier = Modifier.height(30.dp))

        DeleteAccountButton(onClick = onClickDeleteAccount)
    }
}

@Preview()
@Composable
fun ProfileScreenContentPreview(){
    Surface {
        ProfileScreenContent(
            photoUrl = null,
            userName = "Lucas",
            userEmail = "lucas@email.com",
            onBackClick = {},
            onLogoutClick = {},
            onClickDeleteAccount = {}
        )
    }
}