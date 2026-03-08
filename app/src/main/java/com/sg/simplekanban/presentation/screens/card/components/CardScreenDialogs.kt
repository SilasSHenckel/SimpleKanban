package com.sg.simplekanban.presentation.screens.card.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.presentation.components.ChecklistDialog
import com.sg.simplekanban.presentation.components.CommentOptionsDialog
import com.sg.simplekanban.presentation.components.DateAndTimePickerDialog
import com.sg.simplekanban.presentation.components.DeleteCardDialog
import com.sg.simplekanban.presentation.components.EditCommentDialog
import com.sg.simplekanban.presentation.components.MyProgressBar
import com.sg.simplekanban.presentation.components.SelectPriorityDialog
import com.sg.simplekanban.presentation.components.SelectUserDialog
import com.sg.simplekanban.presentation.screens.card.CardViewModel

@Composable
fun CardScreenDialogs(
    isCreatingCard: Boolean,
    card: Card?,
    popBackStack : () -> Unit,
    cardViewModel: CardViewModel,
    showButton: Boolean,
    setShowButton : (Boolean) -> Unit,
){

    val isLoading = cardViewModel.isLoading.collectAsStateWithLifecycle().value
    val showDeleteCardDialog = cardViewModel.showDeleteCardDialog.collectAsStateWithLifecycle().value
    val showSelectResponsibleDialog = cardViewModel.showSelectResponsibleDialog.collectAsStateWithLifecycle().value
    val showSelectPriorityDialog = cardViewModel.showSelectPriorityDialog.collectAsStateWithLifecycle().value
    val showChecklistDialog = cardViewModel.showChecklistDialog.collectAsStateWithLifecycle().value
    val showSelectStartDateDialog = cardViewModel.showSelectStartDateDialog.collectAsStateWithLifecycle().value
    val showSelectFinalDateDialog = cardViewModel.showSelectFinalDateDialog.collectAsStateWithLifecycle().value
    val showCommentOptionsDialog = cardViewModel.showCommentOptionsDialog.collectAsStateWithLifecycle().value
    val showEditCommentDialog = cardViewModel.showEditCommentDialog.collectAsStateWithLifecycle().value

    if(!isCreatingCard){
        if(showDeleteCardDialog && card != null){
            DeleteCardDialog(
                setShowDialog = { cardViewModel.setShowDeleteCardDialog(it) },
                deleteCard = { cardViewModel.deleteCard(
                    card,
                    setShowDialog = { cardViewModel.setShowDeleteCardDialog(it) },
                    requestCloseScreen = {
                        cardViewModel.removeCardFromList(card)
                        popBackStack()
                    })}
            )
        }
    }

    if(showSelectResponsibleDialog){
        SelectUserDialog(
            users = cardViewModel.getCurrentKanbanMembers(),
            setShowDialog = { cardViewModel.setShowSelectResponsibleDialog(false) },
            title = stringResource(id = R.string.responsible),
            onSelectUser = { user ->
                cardViewModel.setResponsible(user)
                if(!showButton) setShowButton(true)
            }
        )
    }

    if(showSelectPriorityDialog){
        SelectPriorityDialog(
            priorities = cardViewModel.priorities.filter { it.id != 0 },
            setShowDialog = { cardViewModel.setShowSelectPriorityDialog(false) },
            title = stringResource(id = R.string.select_priority),
            onSelect = { priority ->
                cardViewModel.setPriority(priority)
                if(!showButton) setShowButton(true)
            }
        )
    }

    if(showChecklistDialog){
        ChecklistDialog(
            cardViewModel = cardViewModel,
            setShowDialog = { cardViewModel.setShowChecklistDialog(false) },
            card = card
        )
    }

    if(showSelectStartDateDialog){
        DateAndTimePickerDialog(
            onConfirm = { date ->
                cardViewModel.setStartDate(DateUtil.getDateFormated(date))
                if(!showButton) setShowButton(true)
            },
            onDismiss = { cardViewModel.setShowSelectStartDateDialog(false) },
        )
    }

    if(showSelectFinalDateDialog){
        DateAndTimePickerDialog(
            onConfirm = { date ->
                cardViewModel.setFinalDate(DateUtil.getDateFormated(date))
                if(!showButton) setShowButton(true)
            },
            onDismiss = { cardViewModel.setShowSelectFinalDateDialog(false)},
        )
    }

    if(showCommentOptionsDialog != null){
        CommentOptionsDialog(
            setShowDialog = { cardViewModel.setShowCommentOptionsDialog(null) },
            setShowEditCommentDialog = { comment -> cardViewModel.setShowEditCommentDialog(comment)},
            deleteComment = { comment -> cardViewModel.deleteComment(comment, onFinish = { cardViewModel.setShowCommentOptionsDialog(null) })},
            comment = showCommentOptionsDialog
        )
    }

    if(showEditCommentDialog != null){
        EditCommentDialog(
            setShowDialog = { cardViewModel.setShowEditCommentDialog(null) },
            commentToEdit = showEditCommentDialog,
            updateComment = { commentToEdit -> cardViewModel.updateComment(commentToEdit, onFinish = { cardViewModel.setShowEditCommentDialog(null)}) }
        )
    }

    if(isLoading) MyProgressBar()

}