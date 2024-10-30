package com.sg.simplekanban.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sg.simplekanban.data.model.TableHistory

@Dao
interface TableHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(tableHistory: TableHistory)

    @Query("SELECT * FROM table_history WHERE id = :pathId")
    fun getByPath(pathId: String): List<TableHistory>

    @Delete
    fun delete(tableHistory: TableHistory)

    @Update
    fun update(tableHistory: TableHistory)

}