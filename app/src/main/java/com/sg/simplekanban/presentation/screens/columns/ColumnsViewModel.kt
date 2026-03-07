package com.sg.simplekanban.presentation.screens.columns

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.singleton.CurrentColumnsManager
import com.sg.simplekanban.data.singleton.CurrentKanbanManager
import com.sg.simplekanban.data.singleton.CurrentUserManager
import com.sg.simplekanban.domain.usecase.ColumnUseCase
import com.sg.simplekanban.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColumnsViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
    private val currentKanbanManager: CurrentKanbanManager,
    private val currentUserManager: CurrentUserManager,
    private val currentColumnsManager: CurrentColumnsManager
): BaseViewModel() {

    var showNewColumnDialog by mutableStateOf(false)

    var columnToEdit : Column? = null

    val currentKanban = currentKanbanManager.currentKanban
    val currentKanbanColumns = currentColumnsManager.currentKanbanColumns
    val currentKanbanUserId get() = currentUserManager.currentKanbanUserId
    val userId get() = currentUserManager.userId

    fun getCurrentKanbanColumns() = currentKanbanColumns.value
    fun getCurrentKanban() = currentKanban.value

    fun setCurrentKanbanColumns(columns: List<Column>) = currentColumnsManager.setCurrentKanbanColumns(columns)


    fun saveColumn(
        name: String,
        priority: Int = 3,
        onSaveColumn: (Column) -> Unit
    ) {

        val currentKanbanUserId = currentKanbanUserId
        val currentKanbanId = getCurrentKanban()?.documentId

        if(currentKanbanUserId != null && currentKanbanId != null){

            val column = Column(
                name = name,
                priority = priority,
            )
            
            launchWithLoading {
                columnUseCase.save(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanbanId,
                    column = column,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { generatedId ->
                        stopLoading()
                        column.documentId = generatedId

                        val list = mutableListOf<Column>()
                        list.addAll(getCurrentKanbanColumns())
                        list.add(column)

                        setCurrentKanbanColumns(list)

                        onSaveColumn(column)
                    }
                )
            }
        }
    }

    fun updateColum(column: Column, name: String, onSuccess: (Column) -> Unit) {

        val currentKanbanUserId = currentKanbanUserId
        val currentKanbanId = getCurrentKanban()?.documentId

        if(currentKanbanUserId != null && currentKanbanId != null){
            launchWithLoading {
                column.name = name

                columnUseCase.update(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanbanId,
                    column = column,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()

                        val list = mutableListOf<Column>()
                        list.addAll(getCurrentKanbanColumns())
                        list.remove(column)
                        list.add(column)

                        list.sortBy { it.priority }

                        setCurrentKanbanColumns(list)

                        onSuccess(column)
                    }
                )
            }

        }
    }

}




