package com.sg.simplekanban.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.domain.usecases.CardUseCase
import com.sg.simplekanban.domain.usecases.ColumnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase
): ViewModel() {

    var showMoveCardDialog by mutableStateOf(false)

    var selectedColumnId by mutableLongStateOf(0)

    fun moveCardToColumn(columnId: Long, card: Card) = viewModelScope.launch {
        card.columnId = columnId
        cardUseCase.update(card)
    }

    fun getColumns() : List<Column>{
        return listOf(
            Column(id = 0, name = "TO DO"),
            Column(id = 1, name = "DOING"),
            Column(id = 2, name = "DONE"),
        )
    }

    fun getCardsByColumnId(columnId: Long): List<Card>{
        return cardUseCase.getCardsByColumnId(columnId)
        /*return listOf(
            Card(id = 0, title = "Criar tela de login", columnId = 0),
            Card(id = 1, title = "Fazer rabanada com doce de leite", columnId = 1),
            Card(id = 2, title = "Fazer rabanada com doce de leite", columnId = 2),
            Card(id = 3, title = "Fazer rabanada com doce de leite", columnId = 3),
            Card(id = 4, title = "Fazer rabanada com doce de leite", columnId = 4),
            Card(id = 5, title = "Fazer rabanada com doce de leite", columnId = 5),
            Card(id = 6, title = "Criar tela de login", columnId = 6),
            Card(id = 7, title = "Fazer rabanada com doce de leite", columnId = 7),
            Card(id = 8, title = "Fazer rabanada com doce de leite", columnId = 8),
            Card(id = 9, title = "Fazer rabanada com doce de leite", columnId = 9),
            Card(id = 10, title = "Fazer rabanada com doce de leite", columnId = 10),
            Card(id = 11, title = "Fazer rabanada com doce de leite", columnId = 11),
        )*/
    }

}




