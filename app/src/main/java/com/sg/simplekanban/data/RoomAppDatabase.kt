package com.sg.simplekanban.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sg.simplekanban.data.dao.TableHistoryDao
import com.sg.simplekanban.data.model.TableHistory

@Database(
    entities = [
        TableHistory::class,
    ],
    version = 1
)
abstract class RoomAppDatabase : RoomDatabase (){
    companion object {
        const val DATABASE_NAME = "simple_kanban.db"
    }
    abstract fun tableHistoryDao() : TableHistoryDao
}