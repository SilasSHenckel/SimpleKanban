package com.sg.simplekanban.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R
import com.sg.simplekanban.ui.components.DeleteAccountDialog
import com.sg.simplekanban.ui.components.DeleteCardDialog
import com.sg.simplekanban.ui.components.MyProgressBar
import com.sg.simplekanban.ui.theme.Red

@Composable
fun ProfileScreen(
    nav: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    Box (
        modifier = Modifier.fillMaxSize()
    ){

        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileToolbar(nav = nav, profileViewModel = profileViewModel)

            Spacer(modifier = Modifier.height(30.dp))

            DeleteAccountButton(profileViewModel = profileViewModel)
        }

        if(profileViewModel.isShowingDeleteAccountDialog){
            DeleteAccountDialog(
                profileViewModel = profileViewModel,
                setShowDialog = { profileViewModel.isShowingDeleteAccountDialog = it },
                nav = nav
            )
        }

        val isLoading = profileViewModel.isLoading
        if(isLoading) MyProgressBar()

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileToolbar(
    nav: NavHostController,
    profileViewModel: ProfileViewModel
){

    val user = profileViewModel.currentUser

    Column(
        modifier = Modifier
            .background(color = colorResource(id = R.color.menu_background))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){

            IconButton(
                onClick = {
                    nav.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = colorResource(id = R.color.title)
                )
            }

            Column (
                modifier = Modifier.align(Alignment.TopCenter)
            ){

                Spacer(modifier = Modifier.height(20.dp))

                if(user?.photoUrl != null){
                    GlideImage(
                        model = user.photoUrl,
                        contentDescription = "user",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(45.dp)),
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "user",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(45.dp))
                    )
                }
            }

            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(end = 5.dp),
                onClick = {
                    profileViewModel.signOut(nav)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ExitToApp,
                    contentDescription = null,
                    tint = colorResource(id = R.color.title)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = profileViewModel.getUserName().uppercase(),
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = profileViewModel.getUserEmail(),
            color = colorResource(id = R.color.text),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(30.dp))
    }

}

@Composable
fun DeleteAccountButton(
    profileViewModel: ProfileViewModel
){
    Button(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
        onClick = {
            profileViewModel.isShowingDeleteAccountDialog = true
        },
        colors = ButtonColors(Red, Color.White, Red, Color.White),

        ) {
        Text(
            text = stringResource(id = R.string.delete_account).uppercase(),
            fontWeight = FontWeight.Bold,
        )
    }
}