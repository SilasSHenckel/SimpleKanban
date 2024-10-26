package com.sg.simplekanban.data.inMemory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sg.simplekanban.data.model.Card

object CardInMemory{
    var card: Card? = null

    var cards by mutableStateOf<List<Card>>(listOf())
}