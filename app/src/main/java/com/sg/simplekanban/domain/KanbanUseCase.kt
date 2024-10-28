package com.sg.simplekanban.domain

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.IS_SHARED
import com.sg.simplekanban.data.constants.Constants.Companion.NAME
import com.sg.simplekanban.data.constants.Constants.Companion.SHARED_WITH_USERS
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.repository.KanbanRepository
import javax.inject.Inject

class KanbanUseCase @Inject constructor(
    private val kanbanRepository: KanbanRepository
) {

    fun save(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        kanbanRepository.save(userId, kanban, onError, onSuccess)
    }

    fun getKanbanById(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: (Kanban?) -> Unit){
        kanbanRepository.getKanbanById(userId, kanbanId, onError, onSuccess)
    }

    fun getCurrentUserKanbans(onError: (Throwable) -> Unit, onSuccess: (List<Kanban>) -> Unit) {
        kanbanRepository.getCurrentUserKanbans(onError, onSuccess)
    }

    fun delete(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        kanbanRepository.delete(userId, kanbanId, onError, onSuccess)
    }

    fun update(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        kanbanRepository.update(userId, kanban, onError, onSuccess)
    }

    fun updateKanbanName(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        kanbanRepository.updateKanbanName(userId, kanban, onError, onSuccess)
    }

    fun updateKanbanShared(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit ){
        kanbanRepository.updateKanbanShared(userId, kanban, onError, onSuccess)
    }

}