package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class Kanban (
    @DocumentId
    var documentId : String? = null,

    var name: String? = null,
    var shared: Boolean = false,
    var creationDate: String? = null,

    //key -> userId, value -> userEmail
    var sharedWithUsers: HashMap<String, String>? = null

)