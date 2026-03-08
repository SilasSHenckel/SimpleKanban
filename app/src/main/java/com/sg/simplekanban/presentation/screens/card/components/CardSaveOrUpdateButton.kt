package com.sg.simplekanban.presentation.screens.card.components

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.presentation.screens.card.CardViewModel
import com.sg.simplekanban.presentation.theme.Purple40
import com.sg.simplekanban.presentation.theme.SelectedBlue

@Composable
fun SaveOrUpdateButton(
    isCreatingCard: Boolean,
    onSave: () -> Unit,
    onUpdate: () -> Unit
) {
    Button(
        onClick = {
            if (isCreatingCard) {
                onSave()
            } else {
                onUpdate()
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = SelectedBlue,
            contentColor = Color.White,
            disabledContainerColor = Purple40,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = if (isCreatingCard) stringResource(id = R.string.save) else stringResource(id = R.string.update))
    }
}

fun onSaveClick(
    title: String?,
    description: String,
    columnId: String,
    priority: Int,
    userId: String?,
    context: Context,
    cardViewModel: CardViewModel,
    popBackStack : () -> Unit,
){
    if(!title.isNullOrEmpty()){
        cardViewModel.saveCard(title, description, columnId, priority, userId, popBackStack)
    } else {
        Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
    }
}

fun onUpdateClick(
    card: Card?,
    title: String?,
    description: String,
    context: Context,
    cardViewModel: CardViewModel,
    popBackStack: () -> Unit
){
    if(card != null){
        if(!title.isNullOrEmpty()){
            if(verifyIfHasChangesInCard(card, title, description, cardViewModel.responsible.value?.documentId, cardViewModel.priority.value?.id ?: card.priority, cardViewModel)){
                card.title = title
                card.description = description
                card.responsibleId = cardViewModel.responsible.value?.documentId
                card.priority = cardViewModel.priority.value?.id ?: card.priority
                card.startDate = cardViewModel.startDate.value ?: card.startDate
                card.endDate = cardViewModel.finalDate.value ?: card.endDate
                cardViewModel.updateCard(card, popBackStack)
            } else {
                popBackStack()
            }
        } else {
            Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
        }
    }
}

fun verifyIfHasChangesInCard(
    card: Card,
    title: String?,
    description: String,
    responsibleId: String?,
    selectedPriority: Int,
    cardViewModel: CardViewModel
) : Boolean{
    return card.title != title ||
            card.description != description ||
            card.responsibleId != responsibleId ||
            card.priority != selectedPriority ||
            card.startDate != cardViewModel.startDate.value ||
            card.endDate != cardViewModel.finalDate.value
}