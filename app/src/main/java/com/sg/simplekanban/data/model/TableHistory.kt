package com.sg.simplekanban.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_history")
data class TableHistory (
    @PrimaryKey (autoGenerate = false)
    val id  : String = "",
    val lastGetOnlineDate : String = ""
)