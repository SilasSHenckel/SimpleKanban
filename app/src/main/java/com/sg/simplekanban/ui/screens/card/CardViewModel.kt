package com.sg.simplekanban.ui.screens.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.domain.CardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardUseCase: CardUseCase
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var showDeleteCardDialog by mutableStateOf(false)

    fun saveCard(
        title: String,
        description: String,
        columnId: String,
        priority: Int = 3,
        ownerId: String?,
        nav: NavHostController
    ) = viewModelScope.launch {

        isLoading = true
        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanbanId

        if(currentKanbanUserId != null && currentKanbanId != null){
            val card = Card(
                title = title,
                description = description,
                columnId = columnId,
                creationDate = DateUtil.getCurrentDateFormated(),
                endDate = null,
                priority = priority,
                ownerId = ownerId,
                responsibleId = null
            )
            cardUseCase.save(
                userId = currentKanbanUserId,
                kanbanId = currentKanbanId,
                card = card,
                onError = {
                    isLoading = false
                },
                onSuccess = { generatedId ->
                    isLoading = false

                    card.documentId = generatedId

                    nav.popBackStack()
                    nav.currentBackStackEntry?.savedStateHandle?.set("card", card)
                }
            )
        }
    }

    fun deleteCard(
        card: Card,
        setShowDialog: (Boolean) -> Unit,
        requestCloseScreen: () -> Unit
    ) = viewModelScope.launch {

        isLoading = true
        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanbanId

        if(currentKanbanUserId != null && currentKanbanId != null){
            cardUseCase.delete(
                userId = currentKanbanUserId,
                kanbanId = currentKanbanId,
                card = card,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false
                    setShowDialog(false)
                    requestCloseScreen()
                }
            )
        }
    }

    fun updateCard(card: Card, nav: NavHostController) = viewModelScope.launch {

        isLoading = true
        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanbanId

        if(currentKanbanUserId != null && currentKanbanId != null){
            cardUseCase.update(
                userId = currentKanbanUserId,
                kanbanId = currentKanbanId,
                card = card,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false
                    nav.popBackStack()
                }
            )
        }
    }

}