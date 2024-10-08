package com.sg.simplekanban.domain

import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.data.repository.CommentRepository
import javax.inject.Inject

class CommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    fun save(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        commentRepository.save(userId, kanbanId, cardId, comment, onError, onSuccess)
    }

    fun getCommentsByCard(userId: String, kanbanId: String, cardId: String, onError: (Throwable) -> Unit, onSuccess: (List<Comment>) -> Unit){
        commentRepository.getCommentsByCard(userId, kanbanId, cardId, onError, onSuccess)
    }

    fun delete(userId: String, kanbanId: String, cardId: String, commentId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        commentRepository.delete(userId, kanbanId, cardId, commentId, onError, onSuccess)
    }

    fun update(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        commentRepository.update(userId, kanbanId, cardId, comment, onError, onSuccess)
    }

}