package com.sg.simplekanban.presentation.screens.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.sg.simplekanban.presentation.model.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var showDeleteCardDialog by mutableStateOf(false)
    var showSelectResponsibleDialog by mutableStateOf(false)
    var showSelectPriorityDialog by mutableStateOf(false)
    var showChecklistDialog by mutableStateOf(false)
    var showCommentOptionsDialog by mutableStateOf<Comment?>(null)
    var showEditCommentDialog by mutableStateOf<Comment?>(null)

    var showSelectStartDateDialog by mutableStateOf(false)
    var showSelectFinalDateDialog by mutableStateOf(false)

    var startDate by mutableStateOf<String?>(null)
    var finalDate by mutableStateOf<String?>(null)

    var responsible by mutableStateOf<User?>(null)
    var author by mutableStateOf<User?>(null)

    var priority by mutableStateOf<Priority?>(null)

    var comments by mutableStateOf<List<Comment>>(listOf())

    var checklistTemp : HashMap<String, HashMap<String, Boolean>>? = null

    var isChecklistItemChanged = false

    var priorities = listOf(
        Priority(0, resourceProvider.getString(R.string.select_priority), "#9E9E9E", "#3E3E3E"),
        Priority(1, resourceProvider.getString(R.string.low_priority), "#73FF88", "#0BA923"),
        Priority(2, resourceProvider.getString(R.string.medium_priority), "#FFDA73", "#E49800"),
        Priority(3, resourceProvider.getString(R.string.high_priority), "#FFA3A3", "#E83411"),
    )

    val currentKanbanUserId get() = currentUserManager.currentKanbanUserId
    val userId get() = currentUserManager.userId
    val card get() = currentCardManager.card

    val currentKanban = currentKanbanManager.currentKanban
    val currentKanbanMembers = currentKanbanManager.kanbanMembers
    val selectedColumnId = currentColumnsManager.selectedColumnId
    val cards = currentCardManager.cards

    fun getCurrentKanban() = currentKanban.value
    fun getCurrentKanbanMembers() = currentKanbanMembers.value
    fun getSelectedColumnId() = selectedColumnId.value
    fun getCards() = cards.value
    fun setCards(cards: List<Card>) = currentCardManager.setCards(cards)

    init {
        responsible = getKanbanMember(card?.responsibleId)
        author = getKanbanMember(card?.ownerId)
        loadCardPriority(card?.priority)
        loadCardComments()
    }

    private fun loadCardPriority(cardPriority: Int?){
        if(cardPriority == null ) priority =
            Priority(0, resourceProvider.getString(R.string.select_priority), "#9E9E9E", "#3E3E3E")
        else {
            for (p in priorities){
                if(p.id == cardPriority){
                    priority = p
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
        nav: NavHostController
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

                if(checklistTemp != null){
                    card.checklist = checklistTemp
                }

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
                        nav.popBackStack()
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

    fun updateCard(card: Card, nav: NavHostController) {
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
                        nav.popBackStack()
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
                        comments = it
                    }
                )
            }

        }
    }

    fun addNewCommentInList(comment: Comment){
        val newList = mutableListOf<Comment>()
        newList.addAll(comments)

        val index = hasCommentInList(comment)

        if(index != null) newList[index] = comment
        else newList.add(comment)

        newList.sortByDescending { it.creationDate }

        comments = newList
    }

    private fun hasCommentInList(newComment: Comment): Int?{
        for((index, comment) in comments.withIndex()){
            if(newComment.documentId == comment.documentId){
                return index
            }
        }
        return null
    }

    fun removeCommentFromList(commentToRemove: Comment?){
        if (commentToRemove != null){
            val newList = mutableListOf<Comment>()
            newList.addAll(comments)
            newList.remove(commentToRemove)

            comments = newList
        }
    }

    fun updateChecklist(card: Card, onFinish: () -> Unit) {
        val currentKanbanUserId = currentKanbanUserId
        val currentKanban = getCurrentKanban()
        val cardId = card?.documentId

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