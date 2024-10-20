package com.sg.simplekanban.data.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.model.Column
import javax.inject.Inject

class ColumnRepository @Inject constructor(){

    fun save(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
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

    fun getColumnsByKanban(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: (List<Column>) -> Unit) {
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_COLUMN)
            .orderBy(Constants.PRIORITY, Query.Direction.ASCENDING)
            .get()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val cards = result.toObjects<Column>()
                onSuccess(cards)
            }
    }

    fun delete(userId: String, kanbanId: String, columnId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
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

    fun update(userId: String, kanbanId: String, column: Column, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
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

}