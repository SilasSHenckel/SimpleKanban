package com.sg.simplekanban.data.repository

import com.sg.simplekanban.data.dao.TableHistoryDao
import com.sg.simplekanban.data.model.TableHistory
import javax.inject.Inject

class TableHistoryRepository @Inject constructor(
    private val tableHistoryDao: TableHistoryDao
){

    fun save(tableHistory: TableHistory){
        tableHistoryDao.save(tableHistory)
    }

    fun getByPath(path: String): List<TableHistory>{
        return tableHistoryDao.getByPath(path)
    }

    fun delete(tableHistory: TableHistory){
        tableHistoryDao.delete(tableHistory)
    }

    fun update(tableHistory: TableHistory){
        tableHistoryDao.update(tableHistory)
    }

}