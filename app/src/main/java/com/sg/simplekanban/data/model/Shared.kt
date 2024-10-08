package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class Shared (
    @DocumentId
    val documentId : String? = null,

    var userId: String? = null,
    var kanbanId: String? = null,
)