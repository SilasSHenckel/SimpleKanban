package com.sg.simplekanban.ui.screens.home


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
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.ui.components.EditKanbanNameDialog
import com.sg.simplekanban.ui.components.HomeOptionsDialog
import com.sg.simplekanban.ui.components.MoveCardDialog
import com.sg.simplekanban.ui.components.MyProgressBar
import com.sg.simplekanban.ui.components.ShareKanbanDialog
import com.sg.simplekanban.ui.routes.AppScreen
import com.sg.simplekanban.ui.theme.MenuBackgroundDark
import com.sg.simplekanban.ui.theme.MenuBackgroundGrey
import com.sg.simplekanban.ui.theme.PriorityHigh1
import com.sg.simplekanban.ui.theme.PriorityHigh2
import com.sg.simplekanban.ui.theme.PriorityLow1
import com.sg.simplekanban.ui.theme.PriorityLow2
import com.sg.simplekanban.ui.theme.PriorityMedium1
import com.sg.simplekanban.ui.theme.PriorityMedium2
import com.sg.simplekanban.ui.theme.PrioritySelect1
import com.sg.simplekanban.ui.theme.PrioritySelect2
import com.sg.simplekanban.ui.theme.SelectedBlue
import com.sg.simplekanban.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    SetStatusBarColor()

    val selectedColumnId = ColumnsInMemory.selectedColumnId
    val isShowingDialog = homeViewModel.showMoveCardDialog

    val kanbanTitle = KanbanInMemory.currentKanban?.name ?: "Kanban 1"

    val columns = ColumnsInMemory.currentKanbanColumns

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
                columns = columns,
                isShowingDialog = isShowingDialog
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
                            if(it != ColumnsInMemory.selectedColumnId){
                                ColumnsInMemory.selectedColumnId = it
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

            val kanban = KanbanInMemory.currentKanban

            if(kanban != null){
                EditKanbanNameDialog(
                    kanban = kanban,
                    homeViewModel = homeViewModel,
                    setShowDialog = { homeViewModel.showEditNameDialog = it },
                )
            }
        }

        val isLoading = homeViewModel.isLoading
        if(isLoading) MyProgressBar()
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MyToolBar(
    title: String,
    homeViewModel: HomeViewModel
){

    val kanbanMembers = KanbanInMemory.kanbanMembers

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

@Composable
fun MyBody(
    columnId: String,
    homeViewModel: HomeViewModel,
    nav: NavHostController,
    columns: List<Column>,
    isShowingDialog: Boolean
){

    val cardList = CardInMemory.cards

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {
        
        val kanbanMembers = KanbanInMemory.kanbanMembers

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
                MyListItem(card = card, nav, homeViewModel, columns, isShowingDialog, kanbanMembers)
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
            CardInMemory.card = null
            UserInMemory.userId = homeViewModel.userId
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
    columns: List<Column>,
    isShowingDialog: Boolean,
    kanbanMembers: List<User>
){

    if (isShowingDialog){
        val moveColumns = mutableListOf<Column>()
        for(column in columns){
            if(column.documentId != card.columnId) moveColumns.add(column)
        }
        MoveCardDialog(
            card = card,
            columns = moveColumns,
            homeViewModel = homeViewModel,
            setShowDialog = { homeViewModel.showMoveCardDialog = it }
        )
    }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.card_background),
                shape = RoundedCornerShape(10.dp)
            )
            .combinedClickable(
                onClick = {
                    CardInMemory.card = card
                    UserInMemory.userId = homeViewModel.userId
                    nav.navigate(AppScreen.Card.name + "/" + card.columnId)
                },
                onLongClick = {
                    homeViewModel.showMoveCardDialog = true
                },
            )
    ) {

        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 20.dp, start = 20.dp, bottom = 25.dp, end = if(kanbanMembers.isNotEmpty() && card.responsibleId != null) 60.dp else 20.dp),
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
                    Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp)){
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
                Row (modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp)) {
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
fun MyTab(
    name: String,
    onTabClick: () -> Unit,
    selected: Boolean,
    widthDivisionNumber: Int = 3,
    listSize: Int
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var widthSpaceRequired = if(listSize > 3) 20.dp else 0.dp

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