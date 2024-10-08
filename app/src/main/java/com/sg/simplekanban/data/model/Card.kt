package com.sg.simplekanban.data.model

data class Card (
    val documentId : String? = null,
    var title: String? = null,
    var description: String? = null,
    var columnId : String? = null,
    val creationDate : String? = null,
    val endDate : String? = null,
    val priority : Int = 0,
    val ownerId : String? = null,
    val responsibleId : String? = null,
)