package com.sg.simplekanban.di

import android.content.Context
import androidx.room.Room
import com.sg.simplekanban.data.RoomAppDatabase
import com.sg.simplekanban.data.repository.TableHistoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideRoomDataBase(@ApplicationContext context: Context): RoomAppDatabase = Room.databaseBuilder(
        context.applicationContext,
        RoomAppDatabase::class.java,
        RoomAppDatabase.DATABASE_NAME
    )
        .allowMainThreadQueries()
        .build()

    @Provides
    fun provideTableHistoryRepository(database: RoomAppDatabase): TableHistoryRepositoryImpl = TableHistoryRepositoryImpl(
        database.tableHistoryDao()
    )

}