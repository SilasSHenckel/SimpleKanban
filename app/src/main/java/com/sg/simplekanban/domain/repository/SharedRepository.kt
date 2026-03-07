package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.Kanban

interface SharedRepository {

    fun save(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit)

    fun getUserKanbans(userId: String, onError: (Throwable) -> Unit, onSuccess: (List<Kanban>) -> Unit)

    fun delete(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun update(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

}