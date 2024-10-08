package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class Comment (
    @DocumentId
    val documentId : String? = null,

    var text: String? = null,
    var authorId: String? = null,
    val creationDate : String? = null,
)