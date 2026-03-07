package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.User

interface UserRepository {

    fun save(user: User, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit)

    fun getUser(userId: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit)

    fun getUserByEmail(email: String, onError: (Throwable) -> Unit, onSuccess: (User?) -> Unit)

    fun delete(userId: String, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun update(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    fun updateUserSharedKanbans(user: User, onError: (Throwable) -> Unit, onSuccess: () -> Unit)

    suspend fun getKanbanMembers(kanbanUserId: String, kanbanId: String, sharedWithUsers: HashMap<String, String>, onError: (Throwable) -> Unit, onSuccess: (List<User>) -> Unit)

}