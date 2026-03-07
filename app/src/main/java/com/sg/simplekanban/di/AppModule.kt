package com.sg.simplekanban.di

import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_IN_REQUEST
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_UP_REQUEST
import com.sg.simplekanban.data.repository.AuthRepositoryImpl
import com.sg.simplekanban.data.repository.CardRepositoryImpl
import com.sg.simplekanban.data.repository.ColumnRepositoryImpl
import com.sg.simplekanban.data.repository.CommentRepositoryImpl
import com.sg.simplekanban.data.repository.KanbanRepositoryImpl
import com.sg.simplekanban.data.repository.SharedRepositoryImpl
import com.sg.simplekanban.data.repository.TableHistoryRepositoryImpl
import com.sg.simplekanban.data.repository.UserRepositoryImpl
import com.sg.simplekanban.domain.repository.AuthRepository
import com.sg.simplekanban.domain.repository.CardRepository
import com.sg.simplekanban.domain.repository.ColumnRepository
import com.sg.simplekanban.domain.repository.CommentRepository
import com.sg.simplekanban.domain.repository.KanbanRepository
import com.sg.simplekanban.domain.repository.SharedRepository
import com.sg.simplekanban.domain.repository.TableHistoryRepository
import com.sg.simplekanban.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(
        app: Application
    ): Context = app.applicationContext

    @Provides
    fun provideAppPreferences(
        context: Context
    ): AppPreferences = AppPreferences(context)

    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun provideFirestore() = Firebase.firestore

    @Provides
    fun provideOneTapClient(
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Named(SIGN_IN_REQUEST)
    fun provideSignInRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build())
        .setAutoSelectEnabled(true)
        .build()

    @Provides
    @Named(SIGN_UP_REQUEST)
    fun provideSignUpRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
        .build()

    @Provides
    fun provideGoogleSignInOptions(
        app: Application
    ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.web_client_id))
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        @Named(SIGN_IN_REQUEST)
        signInRequest: BeginSignInRequest,
        @Named(SIGN_UP_REQUEST)
        signUpRequest: BeginSignInRequest,
        db: FirebaseFirestore,
        signInClient: GoogleSignInClient,
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        db = db,
        signInClient = signInClient
    )

}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleRepositories {

    @Binds
    abstract fun provideCardRepository(cardRepository: CardRepositoryImpl): CardRepository

    @Binds
    abstract fun provideColumnRepository(columnRepository: ColumnRepositoryImpl): ColumnRepository

    @Binds
    abstract fun provideCommentRepository(commentRepository: CommentRepositoryImpl): CommentRepository

    @Binds
    abstract fun provideKanbanRepository(kanbanRepository: KanbanRepositoryImpl): KanbanRepository

    @Binds
    abstract fun provideSharedRepository(sharedRepository: SharedRepositoryImpl): SharedRepository

    @Binds
    abstract fun provideUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun provideTableHistoryRepository(tableHistoryRepository: TableHistoryRepositoryImpl): TableHistoryRepository

}