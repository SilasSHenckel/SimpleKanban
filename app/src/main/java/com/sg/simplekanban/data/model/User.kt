package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class User (
    @DocumentId
    val documentId : String? = null,

    var name: String? = null,
    var email: String? = null,
    var photoUrl : String? = null,

    //key -> kanbanId, value -> UserId
    var sharedWithMe: HashMap<String, String>? = null
)