package com.sg.simplekanban.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.domain.CardUseCase
import com.sg.simplekanban.domain.ColumnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase
): ViewModel() {

    var showMoveCardDialog by mutableStateOf(false)

    var selectedColumnId by mutableStateOf("0")

    fun moveCardToColumn(columnId: String, card: Card) = viewModelScope.launch {
        card.columnId = columnId
//        cardUseCase.update(card) //TODO
    }

    fun getColumns() : List<Column>{
        return listOf(
            Column(documentId = "0", name = "TO DO", priority = 0),
            Column(documentId = "1", name = "DOING", priority = 1),
            Column(documentId = "2", name = "DONE", priority = 2),
        )
    }

    fun getCardsByColumnId(columnId: String): List<Card>{
//        return cardUseCase.getCardsByColumnId(columnId) //TODO
        return listOf(
            Card(documentId = "0", title = "Criar tela de login", columnId = "0"),
            Card(documentId = "1", title = "Fazer rabanada com doce de leite", columnId = "1"),
            Card(documentId = "2", title = "Fazer rabanada com doce de leite", columnId = "2"),
            Card(documentId = "3", title = "Fazer rabanada com doce de leite", columnId = "3"),
            Card(documentId = "4", title = "Fazer rabanada com doce de leite", columnId = "4"),
            Card(documentId = "5", title = "Fazer rabanada com doce de leite", columnId = "5"),
            Card(documentId = "6", title = "Criar tela de login", columnId = "6"),
            Card(documentId = "7", title = "Fazer rabanada com doce de leite", columnId = "7"),
            Card(documentId = "8", title = "Fazer rabanada com doce de leite", columnId = "8"),
            Card(documentId = "9", title = "Fazer rabanada com doce de leite", columnId = "9"),
            Card(documentId = "10", title = "Fazer rabanada com doce de leite", columnId = "10"),
            Card(documentId = "11", title = "Fazer rabanada com doce de leite", columnId = "11"),
        )
    }

}




