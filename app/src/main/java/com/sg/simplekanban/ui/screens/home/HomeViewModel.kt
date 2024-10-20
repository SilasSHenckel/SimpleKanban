package com.sg.simplekanban.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.domain.CardUseCase
import com.sg.simplekanban.domain.ColumnUseCase
import com.sg.simplekanban.domain.KanbanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase,
    private val kanbanUseCase: KanbanUseCase,
    private val appPreferences: AppPreferences
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var currentKanban by mutableStateOf<Kanban?>(null)

    var columns by mutableStateOf<List<Column>>(listOf())

    var cards by mutableStateOf<List<Card>>(listOf())

    var showMoveCardDialog by mutableStateOf(false)

    var selectedColumnId by mutableStateOf("0")

    var userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    var lastKanbanUserId : String? = null

    init {
        loadKanban()
    }

    fun loadKanban() = viewModelScope.launch {
        val lastKanbanId = appPreferences.getLastKanbanId()
        lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(lastKanbanId == null || lastKanbanUserId == null){
            isLoading = true
            kanbanUseCase.getCurrentUserKanbans(
                onError = { exception ->
                    isLoading = false

                },
                onSuccess = { list ->
                    isLoading = false
                    if(list.isNotEmpty()) {
                        currentKanban = list[0]
                        appPreferences.setLastKanbanId(list[0].documentId)
                        appPreferences.setLastKanbanId(userId)
                        lastKanbanUserId = userId
                        getColumns()
                    }
                }
            )
        } else {
            isLoading = true
            kanbanUseCase.getKanbanById(lastKanbanUserId!!, lastKanbanId,
                onError = {
                    isLoading = false
                },
                onSuccess = { kanban ->
                    currentKanban = kanban
                    isLoading = false
                    getColumns()
                }
            )
        }
    }

    fun moveCardToColumn(columnId: String, card: Card) = viewModelScope.launch {
        card.columnId = columnId
//        cardUseCase.update(card) //TODO
    }

    fun getColumns() = viewModelScope.launch {
        if(currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            columnUseCase.getColumnsByKanban(
                lastKanbanUserId!!,
                currentKanban!!.documentId!!,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false

                    if (list.isNotEmpty()){
                        list[0].documentId?.let {
                            selectedColumnId = it
                            getCardsByColumnId(it)
                        }
                    }

                    columns = list

                }
            )
        }
    }

    fun getCardsByColumnId(columnId: String) = viewModelScope.launch{
        if(currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            cardUseCase.getCardsByColumnId(
                lastKanbanUserId!!,
                currentKanban!!.documentId!!,
                columnId,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false
                    cards = list
                }
            )
        }
    }

    fun addCardInList(newCard: Card?){
        if (newCard != null && selectedColumnId == newCard.columnId){
            val newList = mutableListOf<Card>()
            newList.addAll(cards)
            newList.add(newCard)
            cards = newList
        }
    }

}




