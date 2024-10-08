package com.sg.simplekanban.data.model

data class Column (
    val documentId   : String? = null,
    val name : String? = null,
    val priority : Int = 0,
)