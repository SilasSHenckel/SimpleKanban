package com.sg.simplekanban.di

import com.sg.simplekanban.data.repository.CardRepository
import com.sg.simplekanban.data.repository.ColumnRepository
import com.sg.simplekanban.data.repository.CommentRepository
import com.sg.simplekanban.data.repository.KanbanRepository
import com.sg.simplekanban.data.repository.SharedRepository
import com.sg.simplekanban.data.repository.UserRepository
import com.sg.simplekanban.domain.CardUseCase
import com.sg.simplekanban.domain.ColumnUseCase
import com.sg.simplekanban.domain.CommentUseCase
import com.sg.simplekanban.domain.KanbanUseCase
import com.sg.simplekanban.domain.SharedUseCase
import com.sg.simplekanban.domain.UserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

    @Provides
    fun provideCardUseCase(cardRepository: CardRepository): CardUseCase = CardUseCase(cardRepository)

    @Provides
    fun provideColumnUseCase(columnRepository: ColumnRepository): ColumnUseCase = ColumnUseCase(columnRepository)

    @Provides
    fun provideCommentUseCase(commentRepository: CommentRepository): CommentUseCase = CommentUseCase(commentRepository)

    @Provides
    fun provideKanbanUseCase(kanbanRepository: KanbanRepository): KanbanUseCase = KanbanUseCase(kanbanRepository)

    @Provides
    fun provideSharedUseCase(sharedRepository: SharedRepository): SharedUseCase = SharedUseCase(sharedRepository)

    @Provides
    fun provideUserUseCase(userRepository: UserRepository): UserUseCase = UserUseCase(userRepository)

}