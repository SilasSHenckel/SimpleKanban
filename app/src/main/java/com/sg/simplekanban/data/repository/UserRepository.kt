package com.sg.simplekanban.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.EMAIL
import com.sg.simplekanban.data.constants.Constants.Companion.SHARED_WITH_ME
import com.sg.simplekanban.data.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(){

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
        Firebase.firestore
            .collection(Constants.TABLE_USER).document(userId).get()
            .addOnFailureListener { error ->
                onError(error)
            }
            .addOnSuccessListener { result ->
                val user = result.toObject<User>()
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