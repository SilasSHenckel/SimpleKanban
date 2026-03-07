package com.sg.simplekanban.presentation.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileToolbar(
    userName: String,
    userEmail: String,
    photoUrl: String?,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.menu_background))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = colorResource(id = R.color.title)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                if (photoUrl != null) {
                    GlideImage(
                        model = photoUrl,
                        contentDescription = "user",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(45.dp))
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
                    .align(Alignment.TopEnd)
                    .padding(end = 5.dp),
                onClick = onLogoutClick
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
            text = userName.uppercase(),
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userEmail,
            color = colorResource(id = R.color.text),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}