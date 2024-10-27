package com.sg.simplekanban.domain

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.data.constants.Constants
import com.sg.simplekanban.data.constants.Constants.Companion.EMAIL
import com.sg.simplekanban.data.constants.Constants.Companion.SHARED_WITH_ME
import com.sg.simplekanban.data.model.User
import com.sg.simplekanban.data.repository.UserRepository
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    fun save(user: User, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit){
        userRepository.save(user, onError, onSuccess)
    }

    fun getUser(userId: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit){
        userRepository.getUser(userId, onError, onSuccess)
    }

    fun getUserByEmail(email: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit){
        userRepository.getUserByEmail(email, onError, onSuccess)
    }

    fun delete(userId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        userRepository.delete(userId, onError, onSuccess)
    }

    fun update(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        userRepository.update(user, onError, onSuccess)
    }

    fun updateUserSharedKanbans(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit){
        userRepository.updateUserSharedKanbans(user, onError, onSuccess)
    }

}