package com.sg.simplekanban.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.model.Comment
import javax.inject.Inject

class CommentRepository @Inject constructor(){

    fun save(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(cardId)
            .collection(Constants.TABLE_COMMENT).add(comment)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                onSuccess(result.id)
            }
    }

    fun getCommentsByCard(userId: String, kanbanId: String, cardId: String, onError: (Throwable) -> Unit, onSuccess: (List<Comment>) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(cardId)
            .collection(Constants.TABLE_COMMENT).get()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val cards = result.toObjects<Comment>()
                onSuccess(cards)
            }
    }

    fun delete(userId: String, kanbanId: String, cardId: String, commentId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(cardId)
            .collection(Constants.TABLE_COMMENT).document(commentId).delete()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun update(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(cardId)
            .collection(Constants.TABLE_COMMENT).document(comment.documentId!!).set(comment)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

}