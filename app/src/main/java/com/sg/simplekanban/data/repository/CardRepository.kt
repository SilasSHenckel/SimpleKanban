package com.sg.simplekanban.data.repository

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.COLUMN_ID
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.domain.TableHistoryUseCase
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val tableHistoryUseCase: TableHistoryUseCase,
    private val context: Context
){

    fun save(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).add(card)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                onSuccess(result.id)
            }
    }

    fun getCardsByColumnId(userId: String, kanbanId: String, isKanbanShared: Boolean, columnId: String, onError: (Throwable) -> Unit, onSuccess: (List<Card>) -> Unit){

        val path = Constants.TABLE_USER + "/" + userId + "/" + Constants.TABLE_KANBAN + "/" + kanbanId + "/" + Constants.TABLE_CARD

        val source = DateUtil.getSourceOnlineOrCache(path, context,  if(isKanbanShared) "yyyy/MM/dd-HH:mm" else "yyyy/MM/dd-HH" , tableHistoryUseCase)

        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).whereEqualTo("columnId", columnId).get(source)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val cards = result.toObjects<Card>()
                tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                onSuccess(cards)
            }
    }

    fun delete(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(card.documentId!!).delete()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun update(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(card.documentId!!).set(card)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun updateCardColumnId(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(card.documentId!!).update(COLUMN_ID, card.columnId)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

}