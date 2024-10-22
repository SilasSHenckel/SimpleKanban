package com.sg.simplekanban.data.model

import com.google.firebase.firestore.DocumentId

data class Column (
    @DocumentId
    var documentId   : String? = null,

    var name : String? = null,
    val priority : Int = 0,
)