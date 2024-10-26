package com.sg.simplekanban.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
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

    var showOptionsDialog by mutableStateOf(false)

    var showMoveCardDialog by mutableStateOf(false)

    var userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadKanban()
    }

    fun loadKanban() = viewModelScope.launch {
        val lastKanbanId = appPreferences.getLastKanbanId()
        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(lastKanbanId == null || lastKanbanUserId == null){
            isLoading = true
            kanbanUseCase.getCurrentUserKanbans(
                onError = { exception ->
                    isLoading = false

                },
                onSuccess = { list ->
                    isLoading = false
                    if(list.isNotEmpty()) {
                        KanbanInMemory.currentKanban = list[0]
                        UserInMemory.currentKanbanUserId = userId

                        appPreferences.setLastKanbanId(list[0].documentId)
                        appPreferences.setLastKanbanUserId(userId)

                        getColumns()
                    }
                }
            )
        } else {
            isLoading = true
            kanbanUseCase.getKanbanById(lastKanbanUserId, lastKanbanId,
                onError = {
                    isLoading = false
                },
                onSuccess = { kanban ->
                    KanbanInMemory.currentKanban = kanban
                    UserInMemory.currentKanbanUserId = lastKanbanUserId

                    isLoading = false
                    getColumns()
                }
            )
        }
    }

    fun moveCardToColumn(columnId: String, card: Card) = viewModelScope.launch {
        card.columnId = columnId

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(KanbanInMemory.currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            cardUseCase.updateCardColumnId(
                lastKanbanUserId,
                KanbanInMemory.currentKanban!!.documentId!!,
                card,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false
                    val newList = mutableListOf<Card>()
                    newList.addAll(CardInMemory.cards)
                    newList.remove(card)
                    CardInMemory.cards = newList
                }
            )
        }

    }

    fun getColumns() = viewModelScope.launch {

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(KanbanInMemory.currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            columnUseCase.getColumnsByKanban(
                lastKanbanUserId,
                KanbanInMemory.currentKanban!!.documentId!!,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false

                    if (list.isNotEmpty()){
                        list[0].documentId?.let {
                            ColumnsInMemory.selectedColumnId = it
                            getCardsByColumnId(it)
                        }
                    }

                    ColumnsInMemory.currentKanbanColumns = list
                }
            )
        }
    }

    fun getCardsByColumnId(columnId: String) = viewModelScope.launch{

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(KanbanInMemory.currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            cardUseCase.getCardsByColumnId(
                lastKanbanUserId,
                KanbanInMemory.currentKanban!!.documentId!!,
                columnId,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false
                    CardInMemory.cards = list
                }
            )
        }
    }

}




