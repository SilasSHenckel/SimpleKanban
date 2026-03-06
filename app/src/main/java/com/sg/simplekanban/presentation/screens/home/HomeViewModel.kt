package com.sg.simplekanban.presentation.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Card
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
class HomeViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase,
    private val kanbanUseCase: KanbanUseCase,
    private val appPreferences: AppPreferences,
    private val userUseCase: UserUseCase
): ViewModel() {

    var isLoading by mutableStateOf(false)

    var showOptionsDialog by mutableStateOf(false)

    var showShareDialog by mutableStateOf(false)

    var showEditNameDialog by mutableStateOf(false)

    var showMoveCardDialog by mutableStateOf(false)

    var userFounded by mutableStateOf<User?>(null)

    var userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadKanban()
    }

    fun loadKanban() = viewModelScope.launch {
        val lastKanbanId = appPreferences.getLastKanbanId()
        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(lastKanbanId == null || lastKanbanUserId == null){
            isLoading = true
            kanbanUseCase.getCurrentUserKanbans(
                onError = { exception ->
                    isLoading = false

                },
                onSuccess = { list ->
                    isLoading = false
                    if(list.isNotEmpty()) {
                        KanbanInMemory.currentKanban = list[0]
                        UserInMemory.currentKanbanUserId = userId

                        appPreferences.setLastKanbanId(list[0].documentId)
                        appPreferences.setLastKanbanUserId(userId)

                        if (KanbanInMemory.currentKanban != null) verifyUsers(KanbanInMemory.currentKanban!!)
                        getColumns()
                    }
                }
            )
        } else {
            isLoading = true
            kanbanUseCase.getKanbanById(lastKanbanUserId, lastKanbanId,
                onError = {
                    isLoading = false
                },
                onSuccess = { kanban ->
                    KanbanInMemory.currentKanban = kanban
                    UserInMemory.currentKanbanUserId = lastKanbanUserId

                    isLoading = false
                    if (kanban != null) verifyUsers(kanban)
                    getColumns()
                }
            )
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

    fun moveCardToColumn(columnId: String, card: Card) = viewModelScope.launch {
        card.columnId = columnId

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(KanbanInMemory.currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            cardUseCase.updateCardColumnId(
                lastKanbanUserId,
                KanbanInMemory.currentKanban!!.documentId!!,
                card,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false
                    val newList = mutableListOf<Card>()
                    newList.addAll(CardInMemory.cards)
                    newList.remove(card)
                    CardInMemory.cards = newList
                }
            )
        }

    }

    fun getColumns() = viewModelScope.launch {

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()
        val currentKanban = KanbanInMemory.currentKanban

        if(currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            columnUseCase.getColumnsByKanban(
                lastKanbanUserId,
                currentKanban.documentId!!,
                currentKanban.shared,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false

                    if (list.isNotEmpty()){
                        list[0].documentId?.let {
                            ColumnsInMemory.selectedColumnId = it
                            getCardsByColumnId(it)
                        }
                    }

                    ColumnsInMemory.currentKanbanColumns = list
                }
            )
        }
    }

    fun getCardsByColumnId(columnId: String) = viewModelScope.launch{

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        val currentKanban = KanbanInMemory.currentKanban

        if(currentKanban?.documentId != null && lastKanbanUserId != null){
            isLoading = true
            cardUseCase.getCardsByColumnId(
                lastKanbanUserId,
                currentKanban.documentId!!,
                currentKanban.shared,
                columnId,
                onError = {
                    isLoading = false
                },
                onSuccess = { list ->
                    isLoading = false
                    CardInMemory.cards = list
                }
            )
        }
    }

    fun getUserByEmail(email: String, onSuccess: () -> Unit, context: Context) = viewModelScope.launch {
        isLoading = true
        userUseCase.getUserByEmail(
            email,
            onSuccess = {
                isLoading = false
                userFounded = it
                if(userFounded == null){
                    Toast.makeText(context, ContextCompat.getString(context, R.string.user_not_found), Toast.LENGTH_LONG).show()
                }
                onSuccess()
            },
            onError = {
                isLoading = false
            }
        )
    }

    fun shareKanbanWithUser(user: User, context: Context) = viewModelScope.launch{
        val kanban = KanbanInMemory.currentKanban
        val kanbanUserId = UserInMemory.currentKanbanUserId

        if(kanban != null && userId != null){
            val sharedList = kanban.sharedWithUsers ?: hashMapOf()
            if(!sharedList.containsKey(user.documentId) && user.documentId != null && user.email != null){
                sharedList[user.documentId] = user.email!!
            }
            kanban.sharedWithUsers = sharedList
            kanban.shared = true

            isLoading = true
            kanbanUseCase.updateKanbanShared(
                userId!!,
                kanban,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    if(kanbanUserId != null){
                        val sharedWithMe = user.sharedWithMe ?: hashMapOf()
                        sharedWithMe[kanban.documentId!!] = kanbanUserId
                        user.sharedWithMe = sharedWithMe

                        userUseCase.updateUserSharedKanbans(
                            user,
                            onError = {
                                isLoading = false
                            },
                            onSuccess = {
                                isLoading = false
                                userFounded = null
                                Toast.makeText(context, ContextCompat.getString(context, R.string.user_added), Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            )
        }
    }

    fun updateKanbanName(kanban: Kanban, onSuccess: () -> Unit) = viewModelScope.launch{
        val kanbanUserId = UserInMemory.currentKanbanUserId
        if(kanbanUserId != null){
            isLoading = true
            kanbanUseCase.updateKanbanName(
                kanbanUserId,
                kanban,
                onError = {
                    isLoading = false
                },
                onSuccess = {
                    isLoading = false
                    onSuccess()
                }
            )
        }

    }

    fun getCardMember(memberId: String?, members: List<User>) : User? {
        if(memberId == null) return null

        for(member in members){
            if(member.documentId == memberId) return member
        }

        return null
    }

}




