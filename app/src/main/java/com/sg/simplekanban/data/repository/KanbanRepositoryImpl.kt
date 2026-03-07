package com.sg.simplekanban.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.CREATION_DATE
import com.sg.simplekanban.data.constants.Constants.Companion.IS_SHARED
import com.sg.simplekanban.data.constants.Constants.Companion.NAME
import com.sg.simplekanban.data.constants.Constants.Companion.SHARED_WITH_USERS
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.domain.repository.KanbanRepository
import com.sg.simplekanban.domain.usecase.TableHistoryUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class KanbanRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val tableHistoryUseCase: TableHistoryUseCase,
    private val context: Context
) : KanbanRepository {

    override fun save(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).add(kanban)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                onSuccess(result.id)
            }
    }

    override fun getKanbanById(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: (Kanban?) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId).get()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val card = result.toObject<Kanban>()
                onSuccess(card)
            }
    }

    override suspend fun getKanbanSharedWithMeById(sharedWithMe: HashMap<String, String>) : List<Kanban>{

        val kanbansSharedWithMe = mutableListOf<Kanban>()

        try {
            for ((kanbanId, userId) in sharedWithMe){
                if(kanbanId.isNotEmpty() && userId.isNotEmpty()){
                    val path = Constants.TABLE_USER + "/" + userId + "/" + Constants.TABLE_KANBAN + "/" + kanbanId
                    val source = DateUtil.getSourceOnlineOrCache(path, context,  "yyyy/MM/dd", tableHistoryUseCase)
                    val result = Firebase.firestore.collection(Constants.TABLE_USER).document(userId).collection(Constants.TABLE_KANBAN).document(kanbanId).get(source).await()
                    val kanban = result?.toObject<Kanban>()
                    tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                    if (kanban != null) kanbansSharedWithMe.add(kanban)
                }
            }
        } catch (e: Exception){

        }

        return kanbansSharedWithMe
    }

    override fun getCurrentUserKanbans(onError: (Throwable) -> Unit, onSuccess: (List<Kanban>) -> Unit) {
        val userId = auth.currentUser?.uid

        if(userId != null){

            val path = Constants.TABLE_USER + "/" + userId + "/" + Constants.TABLE_KANBAN

            val source = DateUtil.getSourceOnlineOrCache(path, context,  "yyyy/MM/dd-HH:mm", tableHistoryUseCase)

            Firebase.firestore
                .collection(Constants.TABLE_USER).document(userId)
                .collection(Constants.TABLE_KANBAN)
                .orderBy(CREATION_DATE, Query.Direction.DESCENDING).get(source)
                .addOnFailureListener { error ->
                    onError(error)
                }
                .addOnSuccessListener { result ->
                    val cards = result.toObjects<Kanban>()
                    tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                    onSuccess(cards)
                }
        } else {
            onError(Exception("auth user is null"))
        }
    }

    override fun delete(userId: String, kanbanId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanbanId).delete()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override fun update(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanban.documentId!!).set(kanban)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override fun updateKanbanName(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanban.documentId!!).update(NAME, kanban.name)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override fun updateKanbanShared(userId: String, kanban: Kanban, onError: (Throwable) -> Unit, onSuccess: () -> Unit ){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId)
            .collection(Constants.TABLE_KANBAN).document(kanban.documentId!!).update(
                IS_SHARED, kanban.shared,
                SHARED_WITH_USERS, kanban.sharedWithUsers
                )
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

}