package com.sg.simplekanban.presentation.screens.card

import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.singleton.CurrentKanbanManager
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.data.provider.ResourceProvider
import com.sg.simplekanban.data.singleton.CurrentCardManager
import com.sg.simplekanban.data.singleton.CurrentColumnsManager
import com.sg.simplekanban.data.singleton.CurrentUserManager
import com.sg.simplekanban.domain.usecase.CardUseCase
import com.sg.simplekanban.domain.usecase.CommentUseCase
import com.sg.simplekanban.presentation.base.BaseViewModel
import com.sg.simplekanban.data.model.CardPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardUseCase: CardUseCase,
    private val commentUseCase: CommentUseCase,
    private val currentKanbanManager: CurrentKanbanManager,
    private val currentUserManager: CurrentUserManager,
    private val currentColumnsManager: CurrentColumnsManager,
    private val currentCardManager: CurrentCardManager,
    private val resourceProvider: ResourceProvider
): BaseViewModel() {

    private val _showDeleteCardDialog = MutableStateFlow(false)
    val showDeleteCardDialog: StateFlow<Boolean> = _showDeleteCardDialog

    private val _showSelectResponsibleDialog = MutableStateFlow(false)
    val showSelectResponsibleDialog: StateFlow<Boolean> = _showSelectResponsibleDialog

    private val _showSelectPriorityDialog = MutableStateFlow(false)
    val showSelectPriorityDialog: StateFlow<Boolean> = _showSelectPriorityDialog

    private val _showChecklistDialog = MutableStateFlow(false)
    val showChecklistDialog: StateFlow<Boolean> = _showChecklistDialog

    private val _showCommentOptionsDialog = MutableStateFlow<Comment?>(null)
    val showCommentOptionsDialog: StateFlow<Comment?> = _showCommentOptionsDialog

    private val _showEditCommentDialog = MutableStateFlow<Comment?>(null)
    val showEditCommentDialog: StateFlow<Comment?> = _showEditCommentDialog

    private val _showSelectStartDateDialog = MutableStateFlow(false)
    val showSelectStartDateDialog: StateFlow<Boolean> = _showSelectStartDateDialog

    private val _showSelectFinalDateDialog = MutableStateFlow(false)
    val showSelectFinalDateDialog: StateFlow<Boolean> = _showSelectFinalDateDialog

    private val _startDate = MutableStateFlow<String?>(null)
    val startDate: StateFlow<String?> = _startDate

    private val _finalDate = MutableStateFlow<String?>(null)
    val finalDate: StateFlow<String?> = _finalDate

    private val _responsible = MutableStateFlow<User?>(null)
    val responsible: StateFlow<User?> = _responsible

    private val _author = MutableStateFlow<User?>(null)
    val author: StateFlow<User?> = _author

    private val _priority = MutableStateFlow<CardPriority?>(null)
    val priority: StateFlow<CardPriority?> = _priority

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    var checklistTemp : HashMap<String, HashMap<String, Boolean>>? = null

    var isChecklistItemChanged = false

    val currentKanbanUserId get() = currentUserManager.currentKanbanUserId
    val userId get() = currentUserManager.userId
    val card get() = currentCardManager.card
    var priorities = cardUseCase.getCardPriorities()

    val currentKanban = currentKanbanManager.currentKanban
    val currentKanbanMembers = currentKanbanManager.kanbanMembers
    val selectedColumnId = currentColumnsManager.selectedColumnId
    val cards = currentCardManager.cards

    fun getCurrentKanban() = currentKanban.value
    fun getCurrentKanbanMembers() = currentKanbanMembers.value
    fun getSelectedColumnId() = selectedColumnId.value
    fun getCards() = cards.value

    fun setCards(cards: List<Card>) = currentCardManager.setCards(cards)

    fun setShowDeleteCardDialog(value: Boolean) {
        _showDeleteCardDialog.value = value
    }

    fun setShowSelectResponsibleDialog(value: Boolean) {
        _showSelectResponsibleDialog.value = value
    }

    fun setShowSelectPriorityDialog(value: Boolean) {
        _showSelectPriorityDialog.value = value
    }

    fun setShowChecklistDialog(value: Boolean) {
        _showChecklistDialog.value = value
    }

    fun setShowCommentOptionsDialog(comment: Comment?) {
        _showCommentOptionsDialog.value = comment
    }

    fun setShowEditCommentDialog(comment: Comment?) {
        _showEditCommentDialog.value = comment
    }

    fun setShowSelectStartDateDialog(value: Boolean) {
        _showSelectStartDateDialog.value = value
    }

    fun setShowSelectFinalDateDialog(value: Boolean) {
        _showSelectFinalDateDialog.value = value
    }

    fun setResponsible(user: User?) {
        _responsible.value = user
    }

    fun setPriority(priority: CardPriority?) {
        _priority.value = priority
    }

    fun setStartDate(date: String?) {
        _startDate.value = date
    }

    fun setFinalDate(date: String?) {
        _finalDate.value = date
    }

    init {
        _responsible.value = getKanbanMember(card?.responsibleId)
        _author.value = getKanbanMember(card?.ownerId)
        loadCardPriority(card?.priority)
        loadCardComments()
    }

    private fun loadCardPriority(cardPriority: Int?){
        if(cardPriority == null ) _priority.value = CardPriority(0, resourceProvider.getString(R.string.select_priority), "#9E9E9E", "#3E3E3E")
        else {
            for (p in priorities){
                if(p.id == cardPriority){
                    _priority.value = p
                    return
                }
            }
        }
    }

    fun saveCard(
        title: String,
        description: String,
        columnId: String,
        priority: Int = 3,
        ownerId: String?,
        popBackStack : () -> Unit
    ) {
        launchWithLoading {
            val currentKanbanUserId = currentKanbanUserId
            val currentKanbanId = getCurrentKanban()?.documentId

            if(currentKanbanUserId != null && currentKanbanId != null){
                val card = Card(
                    title = title,
                    description = description,
                    columnId = columnId,
                    creationDate = DateUtil.getCurrentDateFormated(),
                    endDate = null,
                    priority = priority,
                    ownerId = ownerId,
                    responsibleId = null
                )

                if(checklistTemp != null) card.checklist = checklistTemp

                cardUseCase.save(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanbanId,
                    card = card,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = { generatedId ->
                        stopLoading()

                        card.documentId = generatedId

                        addCardInList(card)
                        popBackStack()
                    }
                )
            }
        }
    }

    fun deleteCard(
        card: Card,
        setShowDialog: (Boolean) -> Unit,
        requestCloseScreen: () -> Unit
    ) {

        launchWithLoading {
            val currentKanbanUserId = currentKanbanUserId
            val currentKanbanId = getCurrentKanban()?.documentId

            if(currentKanbanUserId != null && currentKanbanId != null){
                cardUseCase.delete(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanbanId,
                    card = card,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        setShowDialog(false)
                        requestCloseScreen()
                    }
                )
            }
        }

    }

    fun updateCard(card: Card, popBackStack : () -> Unit) {
        launchWithLoading {
            val currentKanbanUserId = currentKanbanUserId
            val currentKanbanId = getCurrentKanban()?.documentId

            if(currentKanbanUserId != null && currentKanbanId != null){
                cardUseCase.update(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanbanId,
                    card = card,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        addCardInList(card)
                        popBackStack()
                    }
                )
            }
        }
    }

    fun addCardInList(newCard: Card?){
        if (newCard != null && getSelectedColumnId() == newCard.columnId){
            val newList = mutableListOf<Card>()
            newList.addAll(getCards())

            val index = hasCardInList(newCard)

            if(index != null) newList[index] = newCard
            else newList.add(newCard)

            newList.sortByDescending { it.priority }

            setCards(newList)
        }
    }

    fun hasCardInList(newCard: Card): Int?{
        for((index, card) in getCards().withIndex()){
            if(card.documentId == newCard.documentId){
                return index
            }
        }
        return null
    }

    fun removeCardFromList(cardToRemove: Card?){
        if (cardToRemove != null && getSelectedColumnId() == cardToRemove.columnId){
            val newList = mutableListOf<Card>()
            newList.addAll(getCards())
            newList.remove(cardToRemove)

            setCards(newList)
        }
    }

    fun getKanbanMember(memberId: String?) : User? {
        if(memberId == null) return null

        val members = getCurrentKanbanMembers()

        for(member in members){
            if(member.documentId == memberId) return member
        }

        return null
    }

    fun saveComment(commentText: String, onCommentSaved: () -> Unit) {

        val currentKanbanUserId = currentKanbanUserId
        val currentKanbanId = getCurrentKanban()?.documentId
        val cardId = card?.documentId

        if(currentKanbanUserId != null && currentKanbanId != null && cardId != null){

            val comment = Comment(
                documentId = null,
                text = commentText,
                authorId = FirebaseAuth.getInstance().currentUser?.uid,
                creationDate = DateUtil.getCurrentDateFormated()
            )

            launchWithLoading {
                commentUseCase.save(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanbanId,
                    cardId = cardId,
                    comment = comment,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        onCommentSaved()
                        comment.documentId = it
                        addNewCommentInList(comment)
                    }
                )
            }
        }
    }

    fun updateComment(comment: Comment, onFinish: () -> Unit) {
        val currentKanbanUserId = currentKanbanUserId
        val currentKanban = getCurrentKanban()
        val cardId = card?.documentId

        if(currentKanbanUserId != null
            && currentKanban != null
            && currentKanban.documentId != null
            && currentKanban.shared
            && cardId != null){

            launchWithLoading {
                commentUseCase.update(
                    currentKanbanUserId,
                    kanbanId = currentKanban.documentId!!,
                    cardId = cardId,
                    comment = comment,
                    onError = {
                        stopLoading()
                        onFinish()
                    },
                    onSuccess = {
                        stopLoading()
                        onFinish()
                        addNewCommentInList(comment)
                    }
                )
            }

        } else {
            onFinish()
        }
    }

    fun deleteComment(comment: Comment, onFinish: () -> Unit) {
        val currentKanbanUserId = currentKanbanUserId
        val currentKanban = getCurrentKanban()
        val cardId = card?.documentId

        if(currentKanbanUserId != null
            && currentKanban != null
            && currentKanban.documentId != null
            && currentKanban.shared
            && cardId != null
            && comment.documentId != null){

            launchWithLoading {
                commentUseCase.delete(
                    currentKanbanUserId,
                    kanbanId = currentKanban.documentId!!,
                    cardId = cardId,
                    commentId = comment.documentId!!,
                    onError = {
                        stopLoading()
                        onFinish()
                    },
                    onSuccess = {
                        stopLoading()
                        onFinish()
                        removeCommentFromList(comment)
                    }
                )
            }

        } else {
            onFinish()
        }
    }

    private fun loadCardComments() {
        val currentKanbanUserId = currentKanbanUserId
        val currentKanban = getCurrentKanban()
        val cardId = card?.documentId

        if(currentKanbanUserId != null
            && currentKanban != null
            && currentKanban.documentId != null
            && currentKanban.shared
            && cardId != null){

            launchWithLoading {
                commentUseCase.getCommentsByCard(
                    userId = currentKanbanUserId,
                    kanbanId = currentKanban.documentId!!,
                    cardId = cardId,
                    onError = {
                        stopLoading()
                    },
                    onSuccess = {
                        stopLoading()
                        _comments.value = it
                    }
                )
            }

        }
    }

    fun addNewCommentInList(comment: Comment){
        val newList = mutableListOf<Comment>()
        newList.addAll(comments.value)

        val index = hasCommentInList(comment)

        if(index != null) newList[index] = comment
        else newList.add(comment)

        newList.sortByDescending { it.creationDate }

        _comments.value = newList
    }

    private fun hasCommentInList(newComment: Comment): Int?{
        for((index, comment) in comments.value.withIndex()){
            if(newComment.documentId == comment.documentId){
                return index
            }
        }
        return null
    }

    fun removeCommentFromList(commentToRemove: Comment?){
        if (commentToRemove != null){
            val newList = mutableListOf<Comment>()
            newList.addAll(comments.value)
            newList.remove(commentToRemove)

            _comments.value = newList
        }
    }

    fun updateChecklist(card: Card, onFinish: () -> Unit) {
        val currentKanbanUserId = currentKanbanUserId
        val currentKanban = getCurrentKanban()
        val cardId = card.documentId

        if(currentKanbanUserId != null
            && currentKanban != null
            && currentKanban.documentId != null
            && currentKanban.shared
            && cardId != null){

            launchWithLoading {
                cardUseCase.updateCardChecklist(
                    currentKanbanUserId,
                    kanbanId = currentKanban.documentId!!,
                    card = card,
                    onError = {
                        stopLoading()
                        onFinish()
                    },
                    onSuccess = {
                        stopLoading()
                        onFinish()
                    }
                )
            }

        } else {
            onFinish()
        }
    }

    fun deleteChecklistItem(card: Card?, key: String) {
        launchWithLoading {
            if(card != null) {
                card.checklist?.remove(key)
            } else {
                checklistTemp?.remove(key)
            }
            isChecklistItemChanged = true
            stopLoading()
        }
    }

}