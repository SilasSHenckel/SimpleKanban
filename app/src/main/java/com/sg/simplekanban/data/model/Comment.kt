package com.sg.simplekanban.data.model

data class Comment (
    val documentId : String? = null,
    var text: String? = null,
    var authorId: String? = null,
    val creationDate : String? = null,
)