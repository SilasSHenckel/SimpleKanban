package com.sg.simplekanban.data.singleton

import com.sg.simplekanban.data.model.Card
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentCardManager @Inject constructor(){

    private val _cards = MutableStateFlow<List<Card>>(listOf())
    val cards = _cards.asStateFlow()

    var card: Card? = null

    fun setCards(cards: List<Card>) {
        _cards.value = cards
    }

}