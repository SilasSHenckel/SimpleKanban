package com.sg.simplekanban.ui.screens.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
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
        val currentKanbanId = KanbanInMemory.currentKanban?.documentId

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

                    addCardInList(card)
                    nav.popBackStack()
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
        val currentKanbanId = KanbanInMemory.currentKanban?.documentId

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
        val currentKanbanId = KanbanInMemory.currentKanban?.documentId

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

                    addCardInList(card)
                    nav.popBackStack()
                }
            )
        }
    }

    fun addCardInList(newCard: Card?){
        if (newCard != null && ColumnsInMemory.selectedColumnId == newCard.columnId){
            val newList = mutableListOf<Card>()
            newList.addAll(CardInMemory.cards)

            val index = hasCardInList(newCard)

            if(index != null) newList[index] = newCard
            else newList.add(newCard)

            CardInMemory.cards = newList
        }
    }

    fun hasCardInList(newCard: Card): Int?{
        for((index, card) in CardInMemory.cards.withIndex()){
            if(card.documentId == newCard.documentId){
                return index
            }
        }
        return null
    }

    fun removeCardFromList(cardToRemove: Card?){
        if (cardToRemove != null && ColumnsInMemory.selectedColumnId == cardToRemove.columnId){
            val newList = mutableListOf<Card>()
            newList.addAll(CardInMemory.cards)
            newList.remove(cardToRemove)

            CardInMemory.cards = newList
        }
    }

}