package com.sg.simplekanban.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.presentation.components.EditKanbanNameDialog
import com.sg.simplekanban.presentation.components.HomeOptionsDialog
import com.sg.simplekanban.presentation.components.MoveCardDialog
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.components.ShareKanbanDialog
import com.sg.simplekanban.presentation.screens.home.components.HomeBody
import com.sg.simplekanban.presentation.screens.home.components.HomeTab
import com.sg.simplekanban.presentation.screens.home.components.HomeToolBar
import com.sg.simplekanban.presentation.screens.home.components.StatusBarColor
import com.sg.simplekanban.presentation.theme.PriorityHigh1
import com.sg.simplekanban.presentation.theme.PriorityHigh2
import com.sg.simplekanban.presentation.theme.PriorityLow1
import com.sg.simplekanban.presentation.theme.PriorityLow2
import com.sg.simplekanban.presentation.theme.PriorityMedium1
import com.sg.simplekanban.presentation.theme.PriorityMedium2
import com.sg.simplekanban.presentation.theme.PrioritySelect1
import com.sg.simplekanban.presentation.theme.PrioritySelect2
import kotlinx.coroutines.launch

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
fun HomeScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    StatusBarColor()

    val isShowingDialog = homeViewModel.showMoveCardDialog.collectAsStateWithLifecycle().value
    val currentKanban = homeViewModel.currentKanban.collectAsStateWithLifecycle().value
    val selectedColumnId = homeViewModel.selectedColumnId.collectAsStateWithLifecycle().value
    val columns = homeViewModel.currentKanbanColumns.collectAsStateWithLifecycle().value
    val kanbanMembers = homeViewModel.kanbanMembers.collectAsStateWithLifecycle().value

    val kanbanTitle = currentKanban?.name ?: "Kanban 1"
    val card = homeViewModel.card

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
        ) {

            HomeToolBar(
                title = kanbanTitle,
                kanbanMembers = kanbanMembers,
                setShowOptionsDialog = { value -> homeViewModel.setShowOptionsDialog(value)}
            )

            HomeBody(
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
                HomeTab(
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
                if(column.documentId != card.columnId) moveColumns.add(column)
            }
            MoveCardDialog(
                card = card,
                columns = moveColumns,
                homeViewModel = homeViewModel,
                setShowDialog = { homeViewModel.setShowMoveCardDialog(it) }
            )
        }

        if(homeViewModel.showOptionsDialog.collectAsStateWithLifecycle().value){
            HomeOptionsDialog(
                nav = nav,
                homeViewModel = homeViewModel,
                setShowDialog = { homeViewModel.setShowOptionsDialog(it) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            )
        }

        if(homeViewModel.showShareDialog.collectAsStateWithLifecycle().value){
            ShareKanbanDialog(
                homeViewModel = homeViewModel,
                setShowDialog = { homeViewModel.setShowShareDialog(it) },
            )
        }

        if(homeViewModel.showEditNameDialog.collectAsStateWithLifecycle().value){
            if(currentKanban != null){
                EditKanbanNameDialog(
                    kanban = currentKanban,
                    homeViewModel = homeViewModel,
                    setShowDialog = { homeViewModel.setShowEditNameDialog(it) },
                )
            }
        }

        if(homeViewModel.isLoading.collectAsStateWithLifecycle().value){
            MyProgressBar()
        }
    }
}