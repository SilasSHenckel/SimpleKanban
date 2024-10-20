package com.sg.simplekanban.ui.screens.columns

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.domain.ColumnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColumnsViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var columns by mutableStateOf<List<Column>>(listOf())

    var showNewColumnDialog by mutableStateOf(false)

    init {
        columns = ColumnsInMemory.currentKanbanColumns ?: listOf()
    }

}




