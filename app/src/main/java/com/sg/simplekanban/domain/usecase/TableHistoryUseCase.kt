package com.sg.simplekanban.domain.usecase

import com.sg.simplekanban.data.model.TableHistory
import com.sg.simplekanban.domain.repository.TableHistoryRepository
import javax.inject.Inject

class TableHistoryUseCase @Inject constructor(
    private val tableHistoryRepository: TableHistoryRepository
){

    fun save(tableHistory: TableHistory){
        tableHistoryRepository.save(tableHistory)
    }

    fun getByPath(path: String): List<TableHistory>{
        return tableHistoryRepository.getByPath(path)
    }

    fun delete(tableHistory: TableHistory){
        tableHistoryRepository.delete(tableHistory)
    }

    fun update(tableHistory: TableHistory){
        tableHistoryRepository.update(tableHistory)
    }

}