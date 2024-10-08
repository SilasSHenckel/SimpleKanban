package com.sg.simplekanban.data.model

data class User (
    val documentId : String? = null,
    var name: String? = null,
    var email: String? = null,
    var photoUrl : Long = 0,
) {
    companion object{
        const val TABLE_NAME = "User"
    }
}