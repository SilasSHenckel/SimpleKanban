package com.sg.simplekanban.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.User

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SelectUserDialog (
    users: List<User>,
    setShowDialog: (Boolean) -> Unit,
    title: String,
    onSelectUser: (User) -> Unit,
) {

    Dialog(
        onDismissRequest = { setShowDialog(false) }
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            Column(
                modifier = Modifier.padding(40.dp)
            ) {

                Text(
                    text = title,
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                )

                Spacer(modifier = Modifier.height(30.dp))

                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(space = 20.dp),
                ){
                    itemsIndexed(users) { index: Int, user: User ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onSelectUser(user)
                                setShowDialog(false)
                            }
                        ){
                            if(!user.photoUrl.isNullOrEmpty()){
                                GlideImage(
                                    model = user.photoUrl,
                                    contentDescription = "user",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(15.dp)),
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar),
                                    contentDescription = "user",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                )
                            }
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = user.name ?: (user.email ?: ""),
                                color = colorResource(id = R.color.title),
                                fontSize = 18.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectUserDialogPreview(){

    SelectUserDialog(
        users =  listOf(User(name = "Messi", email = "messi@messi.com"), User(name = "Cr7", email = "cr7@messi.com")),
        setShowDialog = {},
        title = "teste",
        onSelectUser = {},
    )

}


