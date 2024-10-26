package com.sg.simplekanban.ui.screens.kanban

import androidx.lifecycle.ViewModel
import com.sg.simplekanban.domain.KanbanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val kanbanUseCase: KanbanUseCase,
): ViewModel() {

}