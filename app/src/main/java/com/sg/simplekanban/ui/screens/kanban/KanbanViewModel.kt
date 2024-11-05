package com.sg.simplekanban.ui.screens.kanban

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.domain.CardUseCase
import com.sg.simplekanban.domain.ColumnUseCase
import com.sg.simplekanban.domain.KanbanUseCase
import com.sg.simplekanban.domain.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val kanbanUseCase: KanbanUseCase,
    private val appPreferences: AppPreferences,
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var kanbans by mutableStateOf<List<Kanban>>(listOf())

    var showNewKanbanDialog by mutableStateOf(false)

    var currentUser : User? = null

    var sharedWithMeKanbans by mutableStateOf<List<Kanban>>(listOf())

    var userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadKanbans()
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        isLoading = true
        if(userId != null){
            userUseCase.getUser(
                userId!!,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false
                    currentUser = it
                    loadSharedWithMeKanbans(currentUser)
                }
            )
        }
    }

    private fun loadSharedWithMeKanbans(currentUser: User?) = viewModelScope.launch {
        if(currentUser != null && !currentUser.sharedWithMe.isNullOrEmpty()){
            sharedWithMeKanbans = kanbanUseCase.getKanbanSharedWithMeById(currentUser.sharedWithMe!!)
        }
    }

    fun getUserIdBySharedWithMeKanban(kanbanId: String?) : String?{
        if(kanbanId != null && currentUser != null && !currentUser?.sharedWithMe.isNullOrEmpty()){
            return currentUser!!.sharedWithMe?.get(kanbanId)
        }
        return null
    }

    fun loadKanbans() = viewModelScope.launch{
        isLoading = true
        kanbanUseCase.getCurrentUserKanbans(
            onError = {
                isLoading = false
            },
            onSuccess = {
                isLoading = false
                kanbans = it
            }
        )
    }

    fun selectKanban(kanbanUserId: String?, kanban: Kanban, nav: NavHostController){
        if(kanbanUserId != null && KanbanInMemory.currentKanban?.documentId != kanban.documentId){

            appPreferences.setLastKanbanId(kanban.documentId)
            appPreferences.setLastKanbanUserId(kanbanUserId)

            KanbanInMemory.currentKanban = kanban
            UserInMemory.currentKanbanUserId = kanbanUserId

            verifyUsers(kanban)

            getColumns(kanbanUserId, kanban, nav)
        }
    }

    private fun verifyUsers(currentKanban: Kanban) = viewModelScope.launch {
        val currentKanbanUserId = UserInMemory.currentKanbanUserId
        if(currentKanban.shared && currentKanbanUserId != null && !currentKanban.sharedWithUsers.isNullOrEmpty() && currentKanban.documentId != null){
            userUseCase.getKanbanMembers(currentKanbanUserId, currentKanban.documentId!!, currentKanban.sharedWithUsers!!,
                onError = {

                },
                onSuccess = {
                    KanbanInMemory.kanbanMembers = it
                }
            )
        }
    }

    fun getColumns(kanbanUserId: String?, kanban: Kanban, nav: NavHostController) = viewModelScope.launch {
        if(kanban.documentId != null && kanbanUserId != null){
            isLoading = true
            columnUseCase.getColumnsByKanban(
                kanbanUserId,
                kanban.documentId!!,
                kanban.shared,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false

                    ColumnsInMemory.currentKanbanColumns = list

                    if (list.isNotEmpty()){
                        list[0].documentId?.let {
                            ColumnsInMemory.selectedColumnId = it
                            getCardsByColumnId(kanbanUserId, kanban, it, nav)
                        }
                    } else {
                        nav.popBackStack()
                    }
                }
            )
        }
    }

    fun getCardsByColumnId(kanbanUserId: String?, kanban: Kanban, columnId: String, nav: NavHostController) = viewModelScope.launch{
        if(kanban.documentId != null && kanbanUserId != null){
            isLoading = true
            cardUseCase.getCardsByColumnId(
                kanbanUserId,
                kanban.documentId!!,
                kanban.shared,
                columnId,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false
                    CardInMemory.cards = list
                    nav.popBackStack()
                }
            )
        }
    }

    fun saveKanban(
        name: String,
        onSave: (Kanban) -> Unit
    ) = viewModelScope.launch {

        isLoading = true

        val kanban = Kanban(
            name = name,
            shared = false,
            creationDate = DateUtil.getCurrentDateFormated()
        )

        if(userId != null){
            kanbanUseCase.save(
                userId = userId!!,
                kanban = kanban,
                onError = {
                    isLoading = false
                },
                onSuccess = { generatedId ->
                    kanban.documentId = generatedId

                    createKanbanDefaultColumns(userId!!, kanban, onSave)
                }
            )
        }
    }

    fun createKanbanDefaultColumns(userId: String, kanban: Kanban, onSave: (Kanban) -> Unit) = viewModelScope.launch{
        if(kanban.documentId != null){
            val errorResult = columnUseCase.createKanbanDefaultColumns(userId, kanban.documentId!!)
            if(errorResult == null){
                isLoading = false

                //success
                val newList = mutableListOf<Kanban>()
                newList.addAll(kanbans)
                newList.add(kanban)
                kanbans = newList

                onSave(kanban)
            } else {
                isLoading = false
                //error
            }
        }
    }
}