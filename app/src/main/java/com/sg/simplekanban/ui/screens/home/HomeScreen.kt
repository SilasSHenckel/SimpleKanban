package com.sg.simplekanban.ui.screens.home

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.ui.components.MoveCardDialog
import com.sg.simplekanban.ui.components.MyProgressBar
import com.sg.simplekanban.ui.routes.AppScreen
import com.sg.simplekanban.ui.theme.MenuBackgroundDark
import com.sg.simplekanban.ui.theme.MenuBackgroundGrey
import com.sg.simplekanban.ui.theme.SelectedBlue
import com.sg.simplekanban.ui.theme.White

@Composable
fun HomeScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navBackStackEntry: NavBackStackEntry? = null
) {

    LaunchedEffect(key1 = Unit) {
        navBackStackEntry?.let {
            homeViewModel.addCardInList(it.savedStateHandle.get<Card>("card"))
        }
    }

    SetStatusBarColor()

    val selectedColumnId = homeViewModel.selectedColumnId
    val isShowingDialog = homeViewModel.showMoveCardDialog

    val columns = homeViewModel.columns

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
        ) {
            MyToolBar(title = "TASKS")

            MyBody(
                nav = nav,
                columnId = selectedColumnId,
                homeViewModel = homeViewModel,
                columns = columns,
                isShowingDialog = isShowingDialog
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.menu_background))
                .align(Alignment.BottomCenter)
        ) {
            for (column in columns) {
                MyTab(
                    name = column.name ?: "",
                    onTabClick = {
                        column.documentId?.let {
                            if(it != homeViewModel.selectedColumnId){
                                homeViewModel.selectedColumnId = it
                                homeViewModel.getCardsByColumnId(it)
                            }
                        }
                    },
                    selected = selectedColumnId == column.documentId
                )
            }
        }

        val isLoading = homeViewModel.isLoading
        if(isLoading) MyProgressBar()
    }
}

@Composable
fun MyToolBar(
    title: String,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = colorResource(id = R.color.menu_background))
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = title,
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
            )
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

    val cardList = homeViewModel.cards

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {

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
                MyListItem(card = card, nav, homeViewModel, columns, isShowingDialog)
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
            UserInMemory.currentKanbanUserId = homeViewModel.lastKanbanUserId
            UserInMemory.userId = homeViewModel.userId
            KanbanInMemory.currentKanbanId = homeViewModel.currentKanban?.documentId
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyListItem(
    card: Card,
    nav: NavHostController,
    homeViewModel: HomeViewModel,
    columns: List<Column>,
    isShowingDialog: Boolean
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

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.card_background), shape = RoundedCornerShape(10.dp))
            .padding(20.dp)
            .combinedClickable(
                onClick = {
                    CardInMemory.card = card
                    nav.navigate(AppScreen.Card.name + "/" + card.columnId)
                },
                onLongClick = {
                    homeViewModel.showMoveCardDialog = true
                },
            )
    ) {
        Text(
            text = card.title ?: "",
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Composable
fun MyTab(
    name: String,
    onTabClick: () -> Unit,
    selected: Boolean,
    widthDivisionNumber: Int = 3
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = Modifier
            .clickable { onTabClick() }
            .width(screenWidth / widthDivisionNumber)
            .height(60.dp),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = name,
            color = if (selected) colorResource(id = R.color.blue_selected) else colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
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
     val navigationBarLight = Color.BLACK
     val navigationBarDark = Color.BLACK
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