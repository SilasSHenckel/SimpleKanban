package com.sg.simplekanban.presentation.screens.kanban

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.CardPriority
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.data.singleton.CurrentCardManager
import com.sg.simplekanban.data.singleton.CurrentColumnsManager
import com.sg.simplekanban.data.singleton.CurrentKanbanManager
import com.sg.simplekanban.data.singleton.CurrentUserManager
import com.sg.simplekanban.domain.usecase.CardUseCase
import com.sg.simplekanban.domain.usecase.ColumnUseCase
import com.sg.simplekanban.domain.usecase.KanbanUseCase
import com.sg.simplekanban.domain.usecase.UserUseCase
import com.sg.simplekanban.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val kanbanUseCase: KanbanUseCase,
    private val appPreferences: AppPreferences,
    private val columnUseCase: ColumnUseCase,
    private val cardUseCase: CardUseCase,
    private val userUseCase: UserUseCase,
    private val currentKanbanManager: CurrentKanbanManager,
    private val currentUserManager: CurrentUserManager,
    private val currentColumnsManager: CurrentColumnsManager,
    private val currentCardManager: CurrentCardManager,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
): BaseViewModel() {

    private val _kanbans = MutableStateFlow<List<Kanban>>(emptyList())
    val kanbans: StateFlow<List<Kanban>> = _kanbans

    private val _showNewKanbanDialog = MutableStateFlow(false)
    val showNewKanbanDialog: StateFlow<Boolean> = _showNewKanbanDialog

    private val _showDeleteKanbanDialog = MutableStateFlow<Kanban?>(null)
    val showDeleteKanbanDialog: StateFlow<Kanban?> = _showDeleteKanbanDialog

    private val _sharedWithMeKanbans = MutableStateFlow<List<Kanban>>(emptyList())
    val sharedWithMeKanbans: StateFlow<List<Kanban>> = _sharedWithMeKanbans

    var currentUser : User? = null

    var firebaseUserId : String? = firebaseAuth.currentUser?.uid

    val currentKanban: StateFlow<Kanban?> = currentKanbanManager.currentKanban
    val currentKanbanUserId get() = currentUserManager.currentKanbanUserId
    val userId get() = currentUserManager.userId

    fun getCurrentKanban() = currentKanban.value

    fun setShowNewKanbanDialog(show : Boolean){ _showNewKanbanDialog.value = show }
    fun setShowDeleteKanbanDialog(kanban : Kanban?){ _showDeleteKanbanDialog.value = kanban }
    fun setCurrentKanban(kanban: Kanban?) = currentKanbanManager.setCurrentKanban(kanban)
    fun setKanbanMembers(kanbanMembers: List<User>) = currentKanbanManager.setKanbanMembers(kanbanMembers)
    fun setSelectedColumnId(columnId: String) = currentColumnsManager.setSelectedColumnId(columnId)
    fun setCurrentKanbanColumns(columns: List<Column>)= currentColumnsManager.setCurrentKanbanColumns(columns)
    fun setCurrentKanbanUserId(kanbanUserId: String?) { currentUserManager.currentKanbanUserId = (kanbanUserId) }
    fun setCards(cards: List<Card>) = currentCardManager.setCards(cards)

    init {
        getCurrentUser()
        loadKanbans()
    }

    private fun getCurrentUser() {
        launchWithLoading {
            if(firebaseUserId != null){
                userUseCase.getUser(
                    firebaseUserId!!,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        currentUser = it
                        loadSharedWithMeKanbans(currentUser)
                    }
                )
            }
        }
    }

    private fun loadSharedWithMeKanbans(currentUser: User?) = viewModelScope.launch {
        if(currentUser != null && !currentUser.sharedWithMe.isNullOrEmpty()){
            _sharedWithMeKanbans.value = kanbanUseCase.getKanbanSharedWithMeById(currentUser.sharedWithMe!!)
        }
    }

    fun getUserIdBySharedWithMeKanban(kanbanId: String?) : String?{
        if(kanbanId != null && currentUser != null && !currentUser?.sharedWithMe.isNullOrEmpty()){
            return currentUser!!.sharedWithMe?.get(kanbanId)
        }
        return null
    }

    fun loadKanbans() {
        launchWithLoading {
            kanbanUseCase.getCurrentUserKanbans(
                onError = {
                    stopLoading()
                },
                onSuccess = {
                    stopLoading()
                    _kanbans.value = it
                }
            )
        }
    }

    fun selectKanban(kanbanUserId: String?, kanban: Kanban, popBackStack: () -> Unit){
        if(kanbanUserId != null && getCurrentKanban()?.documentId != kanban.documentId){

            appPreferences.setLastKanbanId(kanban.documentId)
            appPreferences.setLastKanbanUserId(kanbanUserId)

            setCurrentKanban(kanban)
            setCurrentKanbanUserId(kanbanUserId)

            verifyUsers(kanban)

            getColumns(kanbanUserId, kanban, popBackStack)
        } else {
            popBackStack()
        }
    }

    private fun verifyUsers(currentKanban: Kanban) = viewModelScope.launch {
        val currentKanbanUserId = currentKanbanUserId
        if(currentKanban.shared && currentKanbanUserId != null && !currentKanban.sharedWithUsers.isNullOrEmpty() && currentKanban.documentId != null){
            userUseCase.getKanbanMembers(currentKanbanUserId, currentKanban.documentId!!, currentKanban.sharedWithUsers!!,
                onError = { setKanbanMembers(listOf()) },
                onSuccess = { setKanbanMembers(it) }
            )
        } else {
            setKanbanMembers(listOf())
        }
    }

    fun getColumns(kanbanUserId: String?, kanban: Kanban, popBackStack: () -> Unit) {
        if(kanban.documentId != null && kanbanUserId != null){
            launchWithLoading {
                columnUseCase.getColumnsByKanban(
                    kanbanUserId,
                    kanban.documentId!!,
                    kanban.shared,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { list ->
                        stopLoading()

                        setCurrentKanbanColumns(list)

                        if (list.isNotEmpty()){
                            list[0].documentId?.let {
                                setSelectedColumnId(it)
                                getCardsByColumnId(kanbanUserId, kanban, it, popBackStack)
                            }
                        } else {
                            popBackStack()
                        }
                    }
                )
            }
        }
    }

    fun getCardsByColumnId(kanbanUserId: String?, kanban: Kanban, columnId: String, popBackStack: () -> Unit) {
        if(kanban.documentId != null && kanbanUserId != null){
            launchWithLoading {
                cardUseCase.getCardsByColumnId(
                    kanbanUserId,
                    kanban.documentId!!,
                    kanban.shared,
                    columnId,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { list ->
                        stopLoading()
                        setCards(list)
                        popBackStack()
                    }
                )
            }
        }
    }

    fun saveKanban(
        name: String,
        onSave: (Kanban) -> Unit
    ) {

        launchWithLoading {
            val kanban = Kanban(
                name = name,
                shared = false,
                creationDate = DateUtil.getCurrentDateFormated()
            )

            if(firebaseUserId != null){
                kanbanUseCase.save(
                    userId = firebaseUserId!!,
                    kanban = kanban,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { generatedId ->
                        kanban.documentId = generatedId

                        createKanbanDefaultColumns(firebaseUserId!!, kanban, onSave)
                    }
                )
            }
        }

    }

    fun createKanbanDefaultColumns(userId: String, kanban: Kanban, onSave: (Kanban) -> Unit) = viewModelScope.launch{
        if(kanban.documentId != null){
            val errorResult = columnUseCase.createKanbanDefaultColumns(userId, kanban.documentId!!)
            if(errorResult == null){
                stopLoading()

                //success
                val newList = mutableListOf<Kanban>()
                newList.addAll(_kanbans.value)
                newList.add(kanban)
                _kanbans.value = newList

                onSave(kanban)
            } else {
                stopLoading()
                //error
            }
        }
    }

    fun deleteKanban( kanban: Kanban, setShowDialog: (Kanban?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(kanban.documentId != null && userId != null){
            launchWithLoading {
                kanbanUseCase.delete(
                    userId,
                    kanban.documentId!!,
                    onError = {
                        stopLoading()

                        setShowDialog(null)
                    },
                    onSuccess = {
                        stopLoading()
                        val newList : MutableList<Kanban> = mutableListOf()
                        newList.addAll(_kanbans.value)
                        newList.remove(kanban)
                        _kanbans.value = newList

                        setShowDialog(null)
                    }
                )
            }
        }
    }
}