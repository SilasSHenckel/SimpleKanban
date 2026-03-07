package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.Kanban

interface KanbanRepository {

    fun save(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit)

    fun getKanbanById(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: (Kanban?) -> Unit)

    suspend fun getKanbanSharedWithMeById(sharedWithMe: HashMap<String, String>) : List<Kanban>

    fun getCurrentUserKanbans(onError: (Throwable) -> Unit, onSuccess: (List<Kanban>) -> Unit)

    fun delete(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun update(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun updateKanbanName(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun updateKanbanShared(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit )

}