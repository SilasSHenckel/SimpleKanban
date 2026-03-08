package com.sg.simplekanban.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.sg.simplekanban.data.model.User

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeToolBar(
    title: String,
    kanbanMembers: List<User>,
    setShowOptionsDialog: (Boolean) -> Unit
){

    val hasKanbanMembers = kanbanMembers.isNotEmpty()

    Column(
        modifier = Modifier
            .background(color = colorResource(id = R.color.menu_background))
            .padding(start = 20.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ){
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = title,
                color = colorResource(id = R.color.title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    setShowOptionsDialog(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = colorResource(id = R.color.title)
                )
            }
        }

        if(hasKanbanMembers){
            LazyRow (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                contentPadding = PaddingValues(all = 0.dp),
                modifier = Modifier.height(30.dp)
            ) {
                itemsIndexed(kanbanMembers){ index: Int, user: User ->
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

                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}