package com.sg.simplekanban.domain

import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.repository.CardRepository
import javax.inject.Inject

class CardUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {

    fun save(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        cardRepository.save(userId, kanbanId, card, onError, onSuccess)
    }

    fun getCardsByColumnId(userId: String, kanbanId: String, columnId: String, onError: (Throwable) -> Unit, onSuccess: (List<Card>) -> Unit){
        cardRepository.getCardsByColumnId(userId, kanbanId, columnId, onError, onSuccess)
    }

    fun delete(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        cardRepository.delete(userId, kanbanId, card, onError, onSuccess)
    }

    fun update(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        cardRepository.update(userId, kanbanId, card, onError, onSuccess)
    }

    fun updateCardColumnId(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        cardRepository.updateCardColumnId(userId, kanbanId, card, onError, onSuccess)
    }

}