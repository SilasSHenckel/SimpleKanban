package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.Column

interface ColumnRepository {

    fun save(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit)

    fun getColumnsByKanban(userId: String, kanbanId: String, isKanbanShared: Boolean, onError: (Throwable) -> Unit, onSuccess: (List<Column>) -> Unit)

    fun delete(userId: String, kanbanId: String, columnId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun update(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    suspend fun createKanbanDefaultColumns(userId: String, kanbanId: String) : Exception?

}