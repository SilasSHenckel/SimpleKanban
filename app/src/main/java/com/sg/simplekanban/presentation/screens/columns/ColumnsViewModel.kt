package com.sg.simplekanban.presentation.screens.columns

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
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

    var showNewColumnDialog by mutableStateOf(false)

    var columnToEdit : Column? = null

    fun saveColumn(
        name: String,
        priority: Int = 3,
        onSaveColumn: (Column) -> Unit
    ) = viewModelScope.launch {

        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanban?.documentId

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
                    list.addAll(ColumnsInMemory.currentKanbanColumns)
                    list.add(column)

                    ColumnsInMemory.currentKanbanColumns = list

                    onSaveColumn(column)
                }
            )
        }
    }

    fun updateColum(column: Column, name: String, onSuccess: (Column) -> Unit) = viewModelScope.launch {

        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        val currentKanbanId = KanbanInMemory.currentKanban?.documentId

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
                    list.addAll(ColumnsInMemory.currentKanbanColumns)
                    list.remove(column)
                    list.add(column)

                    list.sortBy { it.priority }

                    ColumnsInMemory.currentKanbanColumns = list

                    onSuccess(column)
                }
            )
        }
    }

}




