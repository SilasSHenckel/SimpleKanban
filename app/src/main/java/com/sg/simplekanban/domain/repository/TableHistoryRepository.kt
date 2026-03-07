package com.sg.simplekanban.domain.repository

import com.sg.simplekanban.data.model.TableHistory

interface TableHistoryRepository {

    fun save(tableHistory: TableHistory)

    fun getByPath(path: String): List<TableHistory>

    fun delete(tableHistory: TableHistory)

    fun update(tableHistory: TableHistory)

}