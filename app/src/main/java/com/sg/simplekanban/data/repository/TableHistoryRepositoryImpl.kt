package com.sg.simplekanban.data.repository

import com.sg.simplekanban.data.dao.TableHistoryDao
import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.domain.repository.TableHistoryRepository
import javax.inject.Inject

class TableHistoryRepositoryImpl @Inject constructor(
    private val tableHistoryDao: TableHistoryDao
) : TableHistoryRepository {

    override fun save(tableHistory: TableHistory){
        tableHistoryDao.save(tableHistory)
    }

    override fun getByPath(path: String): List<TableHistory>{
        return tableHistoryDao.getByPath(path)
    }

    override fun delete(tableHistory: TableHistory){
        tableHistoryDao.delete(tableHistory)
    }

    override fun update(tableHistory: TableHistory){
        tableHistoryDao.update(tableHistory)
    }

}