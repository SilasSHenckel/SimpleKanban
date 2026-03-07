package com.sg.simplekanban.data.singleton

import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CurrentUserManager @Inject constructor(){

    var currentKanbanUserId: String? = null
    var userId :String? = null

    fun setCurrentKanbanUserId(newCurrentKanbanUserId: String?) {
        currentKanbanUserId = newCurrentKanbanUserId
    }

    fun setUserId(newUserId: String?) {
        userId = newUserId
    }
}