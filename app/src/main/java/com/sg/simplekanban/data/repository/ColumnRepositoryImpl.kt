package com.sg.simplekanban.data.repository

import android.content.Context
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.Firebase
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.TABLE_USER
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.domain.repository.ColumnRepository
import com.sg.simplekanban.domain.usecase.TableHistoryUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ColumnRepositoryImpl @Inject constructor(
    private val tableHistoryUseCase: TableHistoryUseCase,
    private val context: Context
) : ColumnRepository {

    override fun save(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_COLUMN).add(column)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                onSuccess(result.id)
            }
    }

    override fun getColumnsByKanban(userId: String, kanbanId: String, isKanbanShared: Boolean, onError: (Throwable) -> Unit, onSuccess: (List<Column>) -> Unit) {

        val path = Constants.TABLE_USER + "/" + userId + "/" + Constants.TABLE_KANBAN + "/" + kanbanId + "/" + Constants.TABLE_COLUMN

        val source = DateUtil.getSourceOnlineOrCache(path, context,  if(isKanbanShared) "yyyy/MM/dd-HH:mm" else "yyyy/MM/dd-HH" , tableHistoryUseCase)

        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_COLUMN)
            .orderBy(Constants.PRIORITY, Query.Direction.ASCENDING)
            .get(source)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val cards = result.toObjects<Column>()
                tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                onSuccess(cards)
            }
    }

    override fun delete(userId: String, kanbanId: String, columnId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_COLUMN).document(columnId).delete()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override fun update(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_COLUMN).document(column.documentId!!).set(column)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override suspend fun createKanbanDefaultColumns(userId: String, kanbanId: String) : Exception? {
        try {
            Firebase.firestore.collection(TABLE_USER).document(userId).collection(Constants.TABLE_KANBAN).document(kanbanId).collection(Constants.TABLE_COLUMN).add(Column(name = "TO DO", priority = 0)).await()
            Firebase.firestore.collection(TABLE_USER).document(userId).collection(Constants.TABLE_KANBAN).document(kanbanId).collection(Constants.TABLE_COLUMN).add(Column(name = "DOING", priority = 1)).await()
            Firebase.firestore.collection(TABLE_USER).document(userId).collection(Constants.TABLE_KANBAN).document(kanbanId).collection(Constants.TABLE_COLUMN).add(Column(name = "DONE", priority = 2)).await()

            return null
        } catch (e: Exception){
            return e
        }
    }

}