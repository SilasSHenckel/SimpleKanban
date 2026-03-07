package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.Comment

interface CommentRepository {

    fun save(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit)

    fun getCommentsByCard(userId: String, kanbanId: String, cardId: String, onError: (Throwable) -> Unit, onSuccess: (List<Comment>) -> Unit)

    fun delete(userId: String, kanbanId: String, cardId: String, commentId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun update(userId: String, kanbanId: String, cardId: String, comment: Comment, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

}