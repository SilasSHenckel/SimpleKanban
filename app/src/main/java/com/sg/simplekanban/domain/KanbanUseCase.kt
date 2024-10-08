package com.sg.simplekanban.domain

import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.repository.KanbanRepository
import javax.inject.Inject

class KanbanUseCase @Inject constructor(
    private val kanbanRepository: KanbanRepository
) {

    fun save(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        kanbanRepository.save(userId, kanban, onError, onSuccess)
    }

    fun getUserKanbans(userId: String, onError: (Throwable) -> Unit, onSuccess: (List<Kanban>) -> Unit) {
        kanbanRepository.getUserKanbans(userId, onError, onSuccess)
    }

    fun delete(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        kanbanRepository.delete(userId, kanbanId, onError, onSuccess)
    }

    fun update(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        kanbanRepository.update(userId, kanban, onError, onSuccess)
    }

}