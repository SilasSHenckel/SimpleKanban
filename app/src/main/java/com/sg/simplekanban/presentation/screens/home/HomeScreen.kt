package com.sg.simplekanban.presentation.screens.home

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.presentation.components.EditKanbanNameDialog
import com.sg.simplekanban.presentation.components.HomeOptionsDialog
import com.sg.simplekanban.presentation.components.MoveCardDialog
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.components.ShareKanbanDialog
import com.sg.simplekanban.presentation.navigation.AppScreen
import com.sg.simplekanban.presentation.theme.MenuBackgroundDark
import com.sg.simplekanban.presentation.theme.MenuBackgroundGrey
import com.sg.simplekanban.presentation.theme.PriorityHigh1
import com.sg.simplekanban.presentation.theme.PriorityHigh2
import com.sg.simplekanban.presentation.theme.PriorityLow1
import com.sg.simplekanban.presentation.theme.PriorityLow2
import com.sg.simplekanban.presentation.theme.PriorityMedium1
import com.sg.simplekanban.presentation.theme.PriorityMedium2
import com.sg.simplekanban.presentation.theme.PrioritySelect1
import com.sg.simplekanban.presentation.theme.PrioritySelect2
import com.sg.simplekanban.presentation.theme.SelectedBlue
import com.sg.simplekanban.presentation.theme.White
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    SetStatusBarColor()

    val isShowingDialog = homeViewModel.showMoveCardDialog
    val currentKanban = homeViewModel.currentKanban.collectAsStateWithLifecycle().value
    val selectedColumnId = homeViewModel.selectedColumnId.collectAsStateWithLifecycle().value
    val columns = homeViewModel.currentKanbanColumns.collectAsStateWithLifecycle().value

    val kanbanTitle = currentKanban?.name ?: "Kanban 1"
    val card = homeViewModel.card

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
        ) {

            MyToolBar(
                title = kanbanTitle,
                homeViewModel = homeViewModel
            )

            MyBody(
                nav = nav,
                columnId = selectedColumnId,
                homeViewModel = homeViewModel,
            )
        }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyRow (
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = colorResource(id = R.color.menu_background))
                .align(Alignment.BottomCenter)) {
            itemsIndexed(columns) { index: Int, column: Column ->
                MyTab(
                    name = column.name ?: "",
                    onTabClick = {
                        column.documentId?.let {
                            if(it != selectedColumnId){
                                homeViewModel.setSelectedColumnId(it)
                                homeViewModel.getCardsByColumnId(it)

                                val scrollIndex = if(index > 0) index - 1 else index
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index = scrollIndex)
                                }
                            }
                        }
                    },
                    selected = selectedColumnId == column.documentId,
                    listSize = columns.size
                )
            }
        }

        if (isShowingDialog && card != null){
            val moveColumns = mutableListOf<Column>()
            for(column in columns){
                if(column.documentId != card!!.columnId) moveColumns.add(column)
            }
            MoveCardDialog(
                card = card,
                columns = moveColumns,
                homeViewModel = homeViewModel,
                setShowDialog = { homeViewModel.showMoveCardDialog = it }
            )
        }

        if(homeViewModel.showOptionsDialog){
            HomeOptionsDialog(
                nav = nav,
                homeViewModel = homeViewModel,
                setShowDialog = { homeViewModel.showOptionsDialog = it },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            )
        }

        if(homeViewModel.showShareDialog){
            ShareKanbanDialog(
                homeViewModel = homeViewModel,
                setShowDialog = { homeViewModel.showShareDialog = it },
            )
        }

        if(homeViewModel.showEditNameDialog){
            if(currentKanban != null){
                EditKanbanNameDialog(
                    kanban = currentKanban,
                    homeViewModel = homeViewModel,
                    setShowDialog = { homeViewModel.showEditNameDialog = it },
                )
            }
        }

        if(homeViewModel.isLoading.collectAsStateWithLifecycle().value){
            MyProgressBar()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MyToolBar(
    title: String,
    homeViewModel: HomeViewModel
){

    val kanbanMembers = homeViewModel.kanbanMembers.collectAsStateWithLifecycle()

    val hasKanbanMembers = kanbanMembers.value.isNotEmpty()

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
                    homeViewModel.showOptionsDialog = true
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
                itemsIndexed(kanbanMembers.value){ index: Int, user: User ->
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

@Composable
fun MyBody(
    columnId: String,
    homeViewModel: HomeViewModel,
    nav: NavHostController,
){

    val cardList = homeViewModel.cards.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {
        
        val kanbanMembers = homeViewModel.kanbanMembers.collectAsStateWithLifecycle()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(all = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            item {
                MyButtonAddCard(nav = nav, columnId, homeViewModel)
            }

            itemsIndexed(cardList) { index: Int, card: Card ->
                MyListItem(card = card, nav, homeViewModel, kanbanMembers.value)
            }
        }
    }
}

@Composable
fun MyButtonAddCard(
    nav: NavHostController,
    columnId: String,
    homeViewModel: HomeViewModel
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .background(color = SelectedBlue, shape = RoundedCornerShape(20.dp))
        .clickable {
            homeViewModel.setCard(null)
            homeViewModel.updateUserIdWithFirebaseUser()
            nav.navigate(AppScreen.Card.name + "/" + columnId)
        },
        Alignment.Center,

    ) {

        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            imageVector = Icons.Rounded.Add,
            tint = White,
            contentDescription = "add"
        )

        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp),
            text = stringResource(id = R.string.add_card),
            color = White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun MyListItem(
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
                    homeViewModel.showMoveCardDialog = true
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

val priorityColors = hashMapOf(
    Pair(0, Pair(PrioritySelect1, PrioritySelect2)),
    Pair(1, Pair(PriorityLow1, PriorityLow2)),
    Pair(2, Pair(PriorityMedium1, PriorityMedium2)),
    Pair(3, Pair(PriorityHigh1, PriorityHigh2)),
)


val priorityWidth = hashMapOf(
    Pair(0, 20.dp),
    Pair(1, 25.dp),
    Pair(2, 30.dp),
    Pair(3, 35.dp),
)

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        configuration.screenWidthDp > 840
    } else {
        configuration.screenWidthDp > 600
    }
}

@Composable
fun MyTab(
    name: String,
    onTabClick: () -> Unit,
    selected: Boolean,
    widthDivisionNumber: Int = 3,
    listSize: Int
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var widthSpaceRequired = if(listSize > 3) (if(isTablet()) 30.dp else 20.dp) else 0.dp

    Box(
        modifier = Modifier
            .clickable { onTabClick() }
            .width((screenWidth / widthDivisionNumber) - widthSpaceRequired)
            .height(60.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = name,
            color = if (selected) colorResource(id = R.color.blue_selected) else colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        if (selected) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        color = colorResource(id = R.color.blue_selected),
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
                    .height(3.dp)
                    .width((screenWidth / widthDivisionNumber) - 40.dp)
            ) {

            }
        }
    }
}
 @Composable
fun SetStatusBarColor(){

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