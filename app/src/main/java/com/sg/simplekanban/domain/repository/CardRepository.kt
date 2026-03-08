package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.CardPriority

interface CardRepository {

    fun save(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit)

    fun getCardsByColumnId(userId: String, kanbanId: String, isKanbanShared: Boolean, columnId: String, onError: (Throwable) -> Unit, onSuccess: (List<Card>) -> Unit)

    fun delete(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun update(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun updateCardColumnId(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun updateCardChecklist(userId: String, kanbanId: String, card: Card, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun getCardPriorities() : List<CardPriority>
}