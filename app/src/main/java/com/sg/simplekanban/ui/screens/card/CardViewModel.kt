package com.sg.simplekanban.ui.screens.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.domain.CardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardUseCase: CardUseCase
): ViewModel() {

    var showDeleteCardDialog by mutableStateOf(false)

    fun saveCard(title: String, description: String, columnId: String, priority: Int = 3, ownerId: String) = viewModelScope.launch {
//        cardUseCase.save(
//            Card(
//                title = title,
//                description = description,
//                columnId = columnId,
//                creationDate = DateUtil.getCurrentDateFormated(),
//                endDate = null,
//                priority = priority,
//                ownerId = ownerId,
//                responsibleId = null
//            )
//        ) //TODO
    }

    fun deleteCard(card: Card) = viewModelScope.launch {
//        cardUseCase.delete(card) //TODO
    }

    fun updateCard(card: Card) = viewModelScope.launch {
//        cardUseCase.update(card) //TODO
    }

}