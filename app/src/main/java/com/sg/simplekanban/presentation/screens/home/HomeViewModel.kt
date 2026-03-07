package com.sg.simplekanban.presentation.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.singleton.CurrentKanbanManager
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.data.singleton.CurrentCardManager
import com.sg.simplekanban.data.singleton.CurrentColumnsManager
import com.sg.simplekanban.data.singleton.CurrentUserManager
import com.sg.simplekanban.domain.usecase.CardUseCase
import com.sg.simplekanban.domain.usecase.ColumnUseCase
import com.sg.simplekanban.domain.usecase.KanbanUseCase
import com.sg.simplekanban.domain.usecase.UserUseCase
import com.sg.simplekanban.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase,
    private val kanbanUseCase: KanbanUseCase,
    private val appPreferences: AppPreferences,
    private val userUseCase: UserUseCase,
    private val currentKanbanManager: CurrentKanbanManager,
    private val currentUserManager: CurrentUserManager,
    private val currentCardManager: CurrentCardManager,
    private val currentColumnsManager: CurrentColumnsManager
): BaseViewModel() {

    var showOptionsDialog by mutableStateOf(false)

    var showShareDialog by mutableStateOf(false)

    var showEditNameDialog by mutableStateOf(false)

    var showMoveCardDialog by mutableStateOf(false)

    var userFounded by mutableStateOf<User?>(null)

    var firebaseUserId : String? = FirebaseAuth.getInstance().currentUser?.uid

    val currentKanban = currentKanbanManager.currentKanban
    val kanbanMembers = currentKanbanManager.kanbanMembers
    val selectedColumnId = currentColumnsManager.selectedColumnId
    val currentKanbanColumns = currentColumnsManager.currentKanbanColumns
    val cards = currentCardManager.cards

    val currentKanbanUserId get() = currentUserManager.currentKanbanUserId
    val userId get() = currentUserManager.userId
    val card get() = currentCardManager.card

    fun getCurrentKanban() = currentKanban.value
    fun getCurrentKanbanColumns() = currentKanbanColumns.value
    fun getCards() = cards.value

    fun setCurrentKanban(kanban: Kanban?) = currentKanbanManager.setCurrentKanban(kanban)
    fun setKanbanMembers(kanbanMembers: List<User>) = currentKanbanManager.setKanbanMembers(kanbanMembers)
    fun setCurrentKanbanColumns(columns: List<Column>)= currentColumnsManager.setCurrentKanbanColumns(columns)
    fun setSelectedColumnId(columnId: String) = currentColumnsManager.setSelectedColumnId(columnId)
    fun setCurrentKanbanUserId(kanbanUserId: String?) = currentUserManager.setCurrentKanbanUserId(kanbanUserId)
    fun setUserId(userId: String?) = currentUserManager.setUserId(userId)
    fun setCard(newCard: Card?) = currentCardManager.setCard(newCard)
    fun setCards(cards: List<Card>) = currentCardManager.setCards(cards)

    init {
        loadKanban()
    }

    fun loadKanban() {
        launchWithLoading {
            val lastKanbanId = appPreferences.getLastKanbanId()
            val lastKanbanUserId = appPreferences.getLastKanbanUserId()
            
            if(lastKanbanId == null || lastKanbanUserId == null){
                kanbanUseCase.getCurrentUserKanbans(
                    onError = { exception ->
                        stopLoading()
                    },
                    onSuccess = { list ->
                        stopLoading()
                        if(list.isNotEmpty()) {
                            setCurrentKanban(list[0])

                            setCurrentKanbanUserId(firebaseUserId)

                            appPreferences.setLastKanbanId(list[0].documentId)
                            appPreferences.setLastKanbanUserId(firebaseUserId)

                            verifyUsers(getCurrentKanban())
                            getColumns()
                        }
                    }
                )
            } else {
                kanbanUseCase.getKanbanById(lastKanbanUserId, lastKanbanId,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { kanban ->
                        setCurrentKanban(kanban)
                        setCurrentKanbanUserId(lastKanbanUserId)

                        stopLoading()
                        if (kanban != null) verifyUsers(kanban)
                        getColumns()
                    }
                )
            }
        }
        
    }

    private fun verifyUsers(currentKanban: Kanban?) = viewModelScope.launch {
        if(currentKanban == null) return@launch
        
        val currentKanbanUserId = currentKanbanUserId
        if(currentKanban.shared && currentKanbanUserId != null && !currentKanban.sharedWithUsers.isNullOrEmpty() && currentKanban.documentId != null){
            userUseCase.getKanbanMembers(currentKanbanUserId, currentKanban.documentId!!, currentKanban.sharedWithUsers!!,
                onError = {},
                onSuccess = { setKanbanMembers(it) }
            )
        }
    }

    fun moveCardToColumn(columnId: String, card: Card) {
        card.columnId = columnId

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        if(getCurrentKanban()?.documentId != null && lastKanbanUserId != null){
            launchWithLoading {
                cardUseCase.updateCardColumnId(
                    lastKanbanUserId,
                    getCurrentKanban()?.documentId!!,
                    card,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        val newList = mutableListOf<Card>()
                        newList.addAll(getCards())
                        newList.remove(card)
                        setCards(newList)
                    }
                )
            }
        }
        
    }

    fun getColumns() {

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()
        val currentKanban = getCurrentKanban()

        if(currentKanban?.documentId != null && lastKanbanUserId != null){
            launchWithLoading {
                columnUseCase.getColumnsByKanban(
                    lastKanbanUserId,
                    currentKanban.documentId!!,
                    currentKanban.shared,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { list ->
                        stopLoading()

                        if (list.isNotEmpty()){
                            list[0].documentId?.let {
                                setSelectedColumnId(it)
                                getCardsByColumnId(it)
                            }
                        }

                        setCurrentKanbanColumns(list)
                    }
                )
            }
        }
    }

    fun getCardsByColumnId(columnId: String) {

        val lastKanbanUserId = appPreferences.getLastKanbanUserId()

        val currentKanban = getCurrentKanban()

        if(currentKanban?.documentId != null && lastKanbanUserId != null){
            launchWithLoading {
                cardUseCase.getCardsByColumnId(
                    lastKanbanUserId,
                    currentKanban.documentId!!,
                    currentKanban.shared,
                    columnId,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { list ->
                        stopLoading()
                        setCards(list)
                    }
                )
            }
        }
    }

    fun getUserByEmail(email: String, onSuccess: () -> Unit, context: Context) {
        launchWithLoading {
            userUseCase.getUserByEmail(
                email,
                onSuccess = {
                    stopLoading()
                    userFounded = it
                    if(userFounded == null){
                        Toast.makeText(context, ContextCompat.getString(context, R.string.user_not_found), Toast.LENGTH_LONG).show()
                    }
                    onSuccess()
                },
                onError = {
                    stopLoading()
                }
            )
        }
    }

    fun shareKanbanWithUser(user: User, context: Context) {
        val kanban = getCurrentKanban()
        val kanbanUserId = currentKanbanUserId

        if(kanban != null && firebaseUserId != null){
            val sharedList = kanban.sharedWithUsers ?: hashMapOf()
            if(!sharedList.containsKey(user.documentId) && user.documentId != null && user.email != null){
                sharedList[user.documentId] = user.email!!
            }
            kanban.sharedWithUsers = sharedList
            kanban.shared = true

            launchWithLoading {
                kanbanUseCase.updateKanbanShared(
                    firebaseUserId!!,
                    kanban,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        if(kanbanUserId != null){
                            val sharedWithMe = user.sharedWithMe ?: hashMapOf()
                            sharedWithMe[kanban.documentId!!] = kanbanUserId
                            user.sharedWithMe = sharedWithMe

                            userUseCase.updateUserSharedKanbans(
                                user,
                                onError = {
                                    stopLoading()
                                },
                                onSuccess = {
                                    stopLoading()
                                    userFounded = null
                                    Toast.makeText(context, ContextCompat.getString(context, R.string.user_added), Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    fun updateKanbanName(kanban: Kanban, onSuccess: () -> Unit){
        val kanbanUserId = currentKanbanUserId
        if(kanbanUserId != null){
            launchWithLoading {
                kanbanUseCase.updateKanbanName(
                    kanbanUserId,
                    kanban,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        onSuccess()
                    }
                )
            }
        }
    }

    fun getCardMember(memberId: String?, members: List<User>) : User? {
        if(memberId == null) return null

        for(member in members){
            if(member.documentId == memberId) return member
        }

        return null
    }

    fun updateUserIdWithFirebaseUser(){
        setUserId(firebaseUserId)
    }

}




