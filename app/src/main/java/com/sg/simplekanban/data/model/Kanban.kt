package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class Kanban (
    @DocumentId
    val documentId : String? = null,

    var name: String? = null,
    var isShared: Boolean = false,
    var creationDate: String? = null
)