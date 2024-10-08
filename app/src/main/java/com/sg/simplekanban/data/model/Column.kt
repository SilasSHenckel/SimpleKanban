package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class Column (
    @DocumentId
    val documentId   : String? = null,

    val name : String? = null,
    val priority : Int = 0,
)