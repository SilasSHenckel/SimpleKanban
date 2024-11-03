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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.preferences.AppPreferences
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_IN_REQUEST
import com.sg.simplekanban.data.constants.Constants.Companion.SIGN_UP_REQUEST
import com.sg.simplekanban.data.repository.AuthRepository
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
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
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
    ): AuthRepository = AuthRepository(
        auth = auth,
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        db = db,
        signInClient = signInClient
    )

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