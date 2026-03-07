package com.sg.simplekanban.data.singleton

import com.sg.simplekanban.data.model.Column
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class CurrentColumnsManager @Inject constructor(){

    private val _currentKanbanColumns = MutableStateFlow<List<Column>>(listOf())
    val currentKanbanColumns = _currentKanbanColumns.asStateFlow()

    private val _selectedColumnId = MutableStateFlow("0")
    val selectedColumnId = _selectedColumnId.asStateFlow()

    fun setCurrentKanbanColumns(columns: List<Column>) {
        _currentKanbanColumns.value = columns
    }

    fun setSelectedColumnId(columnId: String) {
        _selectedColumnId.value = columnId
    }

}