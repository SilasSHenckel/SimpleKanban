package com.sg.simplekanban.kanban

import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.singleton.CurrentCardManager
import com.sg.simplekanban.data.singleton.CurrentColumnsManager
import com.sg.simplekanban.data.singleton.CurrentKanbanManager
import com.sg.simplekanban.data.singleton.CurrentUserManager
import com.sg.simplekanban.domain.usecase.CardUseCase
import com.sg.simplekanban.domain.usecase.ColumnUseCase
import com.sg.simplekanban.domain.usecase.KanbanUseCase
import com.sg.simplekanban.domain.usecase.UserUseCase
import com.sg.simplekanban.presentation.screens.kanban.KanbanViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.mockito.kotlin.whenever

@RunWith(RobolectricTestRunner::class)
class KanbanViewModelTest {

    private val kanbanUseCase: KanbanUseCase = mock()
    private val appPreferences: AppPreferences = mock()
    private val columnUseCase: ColumnUseCase = mock()
    private val cardUseCase: CardUseCase = mock()
    private val userUseCase: UserUseCase = mock()
    private val currentKanbanManager: CurrentKanbanManager = mock()
    private val currentUserManager: CurrentUserManager = mock()
    private val currentColumnsManager: CurrentColumnsManager = mock()
    private val currentCardManager: CurrentCardManager = mock()

    @Test
    fun selectKanbanTest() = runTest {

        val testKanbanFlow = MutableStateFlow<Kanban?>(null)
        val mockKanbanManager: CurrentKanbanManager = mock()
        whenever(mockKanbanManager.currentKanban).thenReturn(testKanbanFlow)

        val viewModel = KanbanViewModel(
            kanbanUseCase = kanbanUseCase,
            appPreferences = appPreferences,
            columnUseCase = columnUseCase,
            cardUseCase = cardUseCase,
            userUseCase = userUseCase,
            currentKanbanManager = mockKanbanManager,
            currentUserManager = currentUserManager,
            currentColumnsManager = currentColumnsManager,
            currentCardManager = currentCardManager,
            firebaseAuth = mock()
        )

        val kanban = Kanban(documentId = "1", name = "Test")

        testKanbanFlow.value = kanban
        advanceUntilIdle()

        Assert.assertEquals("1", viewModel.currentKanban.value?.documentId)

    }
}