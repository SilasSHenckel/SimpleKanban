package com.sg.simplekanban.domain

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.TABLE_USER
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.repository.ColumnRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ColumnUseCase @Inject constructor(
    private val columnRepository: ColumnRepository
) {

    fun save(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        columnRepository.save(userId, kanbanId, column, onError, onSuccess)
    }

    fun getColumnsByKanban(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: (List<Column>) -> Unit) {
        columnRepository.getColumnsByKanban(userId, kanbanId, onError, onSuccess)
    }

    fun delete(userId: String, kanbanId: String, columnId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        columnRepository.delete(userId, kanbanId, columnId, onError, onSuccess)
    }

    fun update(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        columnRepository.update(userId, kanbanId, column, onError, onSuccess)
    }

    suspend fun createKanbanDefaultColumns(userId: String, kanbanId: String) : Exception? {
        return columnRepository.createKanbanDefaultColumns(userId, kanbanId)
    }

}