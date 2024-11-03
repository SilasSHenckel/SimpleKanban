package com.sg.simplekanban.data.repository

import android.content.Context
import com.google.android.gms.auth.api.identity.SignInClient
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
import com.sg.simplekanban.domain.TableHistoryUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val tableHistoryUseCase: TableHistoryUseCase,
    private val context: Context,
){

    fun save(user: User, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).add(user)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                onSuccess(result.id)
            }
    }

    fun getUser(userId: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit){

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

    fun getUserByEmail(email: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit){
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

    fun delete(userId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId).delete()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun update(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(user.documentId!!).set(user)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun updateUserSharedKanbans(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(user.documentId!!).update(SHARED_WITH_ME, user.sharedWithMe)
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }



}