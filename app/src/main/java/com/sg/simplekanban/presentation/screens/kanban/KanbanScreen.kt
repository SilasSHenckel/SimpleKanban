package com.sg.simplekanban.presentation.screens.kanban

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.presentation.components.CreateKanbanDialog
import com.sg.simplekanban.presentation.components.DeleteKanbanDialog
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.components.MyToolBar
import com.sg.simplekanban.presentation.screens.kanban.components.KanbanBody

@Composable
fun KanbanScreen(
    nav: NavHostController,
    kanbanViewModel: KanbanViewModel? = hiltViewModel()
) {

    val kanbans = kanbanViewModel?.kanbans?.collectAsStateWithLifecycle()?.value ?: listOf()
    val currentKanban = kanbanViewModel?.currentKanban?.collectAsStateWithLifecycle()?.value
    val sharedWithMeKanbans = kanbanViewModel?.sharedWithMeKanbans?.collectAsStateWithLifecycle()?.value
    val showNewKanbanDialog = kanbanViewModel?.showNewKanbanDialog?.collectAsStateWithLifecycle()?.value
    val showDeleteKanbanDialog = kanbanViewModel?.showDeleteKanbanDialog?.collectAsStateWithLifecycle()?.value
    val isLoading = kanbanViewModel?.isLoading?.collectAsStateWithLifecycle()?.value ?: false

    Box (
        modifier = Modifier.fillMaxSize()
    ){

        KanbanScreenContent(
            kanbans = kanbans,
            sharedWithMeKanbans = sharedWithMeKanbans ?: listOf(),
            currentKanban = currentKanban,
            onAddKanbanClick = { kanbanViewModel?.setShowNewKanbanDialog(true)},
            onKanbanClick = { kanbanSelected, isMyKanban ->
                val userId = if(isMyKanban) kanbanViewModel?.firebaseUserId
                else kanbanViewModel?.getUserIdBySharedWithMeKanban(kanbanSelected.documentId)

                kanbanViewModel?.selectKanban(userId, kanbanSelected,{ nav.popBackStack() })
            },
            onMenuClick = { kanbanSelected -> kanbanViewModel?.setShowDeleteKanbanDialog(kanbanSelected) },
            nav = nav
        )

        if(showNewKanbanDialog == true){
            CreateKanbanDialog(
                kanbanViewModel = kanbanViewModel,
                setShowDialog = { kanbanViewModel.setShowNewKanbanDialog(it) }
            )
        }

        if(showDeleteKanbanDialog != null){
            DeleteKanbanDialog(
                kanban = showDeleteKanbanDialog,
                kanbanViewModel = kanbanViewModel,
                setShowDialog = { kanbanViewModel.setShowDeleteKanbanDialog(it) }
            )
        }

        if(isLoading) MyProgressBar()
    }
}

@Composable
fun KanbanScreenContent(
    kanbans: List<Kanban>,
    sharedWithMeKanbans: List<Kanban>,
    currentKanban: Kanban?,
    onAddKanbanClick: () -> Unit,
    onKanbanClick: (Kanban, Boolean) -> Unit,
    onMenuClick: (Kanban) -> Unit,
    nav: NavHostController,
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        MyToolBar(title = stringResource(id = R.string.alternate_kanban), popBackStack = {nav.popBackStack()})

        KanbanBody(
            kanbans = kanbans,
            sharedWithMeKanbans = sharedWithMeKanbans,
            currentKanban = currentKanban,
            onAddKanbanClick = onAddKanbanClick,
            onKanbanClick = onKanbanClick,
            onMenuClick = onMenuClick
        )
    }
}

@Preview
@Composable
fun KanbanScreenPreview(){
    Surface {
        KanbanScreenContent(
            kanbans = listOf(Kanban(name = "Test"), Kanban(name = "Test2", documentId = "2"), Kanban(name = "Test3", documentId = "3")),
            sharedWithMeKanbans = listOf(Kanban(name = "Test4", documentId = "1")),
            currentKanban = null,
            onKanbanClick = { a, b -> },
            onAddKanbanClick = {},
            onMenuClick = {},
            nav = rememberNavController(),
        )
    }
}