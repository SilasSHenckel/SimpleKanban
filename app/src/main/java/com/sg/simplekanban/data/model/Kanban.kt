package com.sg.simplekanban.data.model

data class Kanban (
    val documentId : String? = null,
    var name: String? = null,
    var isShared: Boolean = false,
)