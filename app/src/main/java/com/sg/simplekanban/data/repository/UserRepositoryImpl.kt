package com.sg.simplekanban.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.EMAIL
import com.sg.simplekanban.data.constants.Constants.Companion.SHARED_WITH_ME
import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.domain.repository.UserRepository
import com.sg.simplekanban.domain.usecase.TableHistoryUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val tableHistoryUseCase: TableHistoryUseCase,
    private val context: Context,
) : UserRepository{

    override fun save(user: User, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).add(user)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                onSuccess(result.id)
            }
    }

    override fun getUser(userId: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit){

        val path = Constants.TABLE_USER + "/" + userId
        val source = DateUtil.getSourceOnlineOrCache(path, context,"yyyy/MM/dd-HH:mm", tableHistoryUseCase)

        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId).get(source)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val user = result.toObject<User>()
                tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                onSuccess(user)
            }
    }

    override fun getUserByEmail(email: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).whereEqualTo(EMAIL, email).limit(1).get()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val user = result.toObjects<User>()
                onSuccess(if(user.isNotEmpty()) user[0] else null)
            }
    }

    override fun delete(userId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId).delete()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override fun update(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(user.documentId!!).set(user)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override fun updateUserSharedKanbans(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(user.documentId!!).update(SHARED_WITH_ME, user.sharedWithMe)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    override suspend fun getKanbanMembers(kanbanUserId: String, kanbanId: String, sharedWithUsers: HashMap<String, String>, onError: (Throwable) -> Unit, onSuccess: (List<User>) -> Unit){

        try {

            val list = mutableListOf<User>()
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            val containsCurrentUser = currentUserId != null && sharedWithUsers.contains(currentUserId)

            //se nao contem o usuario quer dizer que ele e o dono do kanban, buscar ele tambem para ter na lista
            if(!containsCurrentUser && currentUserId != null){
                val user = getKanbanMember(kanbanUserId, kanbanId, currentUserId)
                if(user != null) list.add(user)
            }

            if(containsCurrentUser){
                val user = getKanbanMember(kanbanUserId, kanbanId, kanbanUserId)
                if(user != null) list.add(user)
            }

            for ((id, email) in sharedWithUsers){
                val user = getKanbanMember(kanbanUserId, kanbanId, id)
                if(user != null) list.add(user)
            }

            onSuccess(list)

        } catch (e: Exception){
            onError(e)
        }

    }

    private suspend fun getKanbanMember(kanbanUserId: String, kanbanId: String, userId: String) : User?{
        val path = Constants.TABLE_USER + "/" + kanbanUserId + "/" + Constants.TABLE_KANBAN + "/Member/"  + userId

        val source = DateUtil.getSourceOnlineOrCache(path, context,"yyyy/MM/dd", tableHistoryUseCase)

        val result = Firebase.firestore.collection(Constants.TABLE_USER).document(userId).get(source).await()
        if(result != null){
            val user = result.toObject<User>()
            if(user != null){
                tableHistoryUseCase.save(TableHistory(path, DateUtil.getCurrentDateFormated("yyyy/MM/dd-HH:mm")))
                return user
            }
        }

        return null
    }

}