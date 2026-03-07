package com.sg.simplekanban.data.singleton

import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.model.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class CurrentKanbanManager @Inject constructor(){

    private val _currentKanban = MutableStateFlow<Kanban?>(null)
    val currentKanban = _currentKanban.asStateFlow()

    private val _kanbanMembers = MutableStateFlow<List<User>>(listOf())
    val kanbanMembers = _kanbanMembers.asStateFlow()

    fun setCurrentKanban(kanban: Kanban?) {
        _currentKanban.value = kanban
    }

    fun setKanbanMembers(kanbanMembers: List<User>) {
        _kanbanMembers.value = kanbanMembers
    }

}