package com.sg.simplekanban.domain.usecase

import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.domain.repository.SharedRepository
import javax.inject.Inject

class SharedUseCase @Inject constructor(
    private val sharedRepository: SharedRepository
) {

    fun save(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        sharedRepository.save(userId, kanban, onError, onSuccess)
    }

    fun getUserKanbans(userId: String, onError: (Throwable) -> Unit, onSuccess: (List<Kanban>) -> Unit) {
        sharedRepository.getUserKanbans(userId, onError, onSuccess)
    }

    fun delete(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        sharedRepository.delete(userId, kanbanId, onError, onSuccess)
    }

    fun update(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        sharedRepository.update(userId, kanban, onError, onSuccess)
    }

}