package com.sg.simplekanban.data.inMemory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sg.simplekanban.data.model.Column

object ColumnsInMemory{
    var currentKanbanColumns by mutableStateOf<List<Column>>(listOf())
}