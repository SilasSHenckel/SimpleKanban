package com.sg.simplekanban.data.repository

import android.content.Context
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.Firebase
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.domain.repository.CommentRepository
import com.sg.simplekanban.domain.usecase.TableHistoryUseCase
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val tableHistoryUseCase: TableHistoryUseCase,
    private val context: Context
) : CommentRepository {

    override fun save(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
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

    override fun getCommentsByCard(userId: String, kanbanId: String, cardId: String, onError: (Throwable) -> Unit, onSuccess: (List<Comment>) -> Unit){

        val path = Constants.TABLE_USER + "/" + userId + "/" + Constants.TABLE_KANBAN + "/" + kanbanId + "/" + Constants.TABLE_CARD + "/" + cardId + "/" + Constants.TABLE_COMMENT

        val source = DateUtil.getSourceOnlineOrCache(path, context,"yyyy/MM/dd-HH:mm", tableHistoryUseCase)

        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId)
            .collection(Constants.TABLE_CARD).document(cardId)
            .collection(Constants.TABLE_COMMENT).get(source)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val cards = result.toObjects<Comment>()
                tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                onSuccess(cards)
            }
    }

    override fun delete(userId: String, kanbanId: String, cardId: String, commentId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
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

    override fun update(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
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