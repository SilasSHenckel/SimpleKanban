package com.sg.simplekanban.presentation.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.presentation.navigation.AppScreen
import com.sg.simplekanban.presentation.screens.home.HomeViewModel
import com.sg.simplekanban.presentation.screens.home.priorityColors
import com.sg.simplekanban.presentation.screens.home.priorityWidth
import com.sg.simplekanban.presentation.theme.PrioritySelect1
import com.sg.simplekanban.presentation.theme.PrioritySelect2
import com.sg.simplekanban.presentation.theme.SelectedBlue
import kotlin.text.first

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun HomeListItem(
    card: Card,
    nav: NavHostController,
    homeViewModel: HomeViewModel,
    kanbanMembers: List<User>
){
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.card_background),
                shape = RoundedCornerShape(10.dp)
            )
            .combinedClickable(
                onClick = {
                    homeViewModel.setCard(card)
                    homeViewModel.updateUserIdWithFirebaseUser()
                    nav.navigate(AppScreen.Card.name + "/" + card.columnId)
                },
                onLongClick = {
                    homeViewModel.setCard(card)
                    homeViewModel.setShowMoveCardDialog(true)
                },
            )
    ) {

        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    top = 20.dp,
                    start = 20.dp,
                    bottom = 25.dp,
                    end = if (kanbanMembers.isNotEmpty() && card.responsibleId != null) 60.dp else 20.dp
                ),
            text = card.title ?: "",
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            priorityColors[card.priority]?.first ?: PrioritySelect1,
                            priorityColors[card.priority]?.second ?: PrioritySelect2
                        )
                    ),
                    shape = RoundedCornerShape(5.dp)
                )
                .height(4.dp)
                .width(priorityWidth[card.priority] ?: 15.dp)
        ) {

        }

        if(kanbanMembers.isNotEmpty() && card.responsibleId != null){
            val responsible = homeViewModel.getCardMember(card.responsibleId!!, kanbanMembers)
            if(responsible?.photoUrl == null){
                val name = responsible?.name
                if(name != null){
                    Box(modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)){
                        Icon(
                            imageVector = Icons.Rounded.Circle,
                            contentDescription = "circle",
                            tint = SelectedBlue,
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center)
                        )
                        Text(
                            text = name.first().toString(),
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Row (modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)) {
                    GlideImage(
                        model = responsible.photoUrl,
                        contentDescription = "user",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(15.dp))
                        ,
                    )
                }
            }
        }
    }
}