package com.sg.simplekanban.ui.screens.columns

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.domain.ColumnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColumnsViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var columns by mutableStateOf<List<Column>>(listOf())

    var showNewColumnDialog by mutableStateOf(false)

    var columnToEdit : Column? = null

    init {
        columns = ColumnsInMemory.currentKanbanColumns ?: listOf()
    }



    fun saveColumn(
        name: String,
        priority: Int = 3,
        onSaveColumn: (Column) -> Unit
    ) = viewModelScope.launch {

        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanbanId

        if(currentKanbanUserId != null && currentKanbanId != null){
            isLoading = true

            val column = Column(
                name = name,
                priority = priority,
            )

            columnUseCase.save(
                userId = currentKanbanUserId,
                kanbanId = currentKanbanId,
                column = column,
                onError = {
                    isLoading = false
                },
                onSuccess = { generatedId ->
                    isLoading = false
                    column.documentId = generatedId

                    val list = mutableListOf<Column>()
                    list.addAll(columns)
                    list.add(column)

                    columns = list

                    onSaveColumn(column)
                }
            )
        }
    }

    fun updateColum(column: Column, name: String, onSuccess: (Column) -> Unit) = viewModelScope.launch {

        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanbanId

        if(currentKanbanUserId != null && currentKanbanId != null){
            isLoading = true

            column.name = name

            columnUseCase.update(
                userId = currentKanbanUserId,
                kanbanId = currentKanbanId,
                column = column,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false

                    val list = mutableListOf<Column>()
                    list.addAll(columns)
                    list.remove(column)
                    list.add(column)

                    list.sortBy { it.priority }

                    columns = list

                    onSuccess(column)
                }
            )
        }
    }

}




