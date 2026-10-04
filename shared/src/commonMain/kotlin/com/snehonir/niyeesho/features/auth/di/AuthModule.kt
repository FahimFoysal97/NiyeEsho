package com.snehonir.niyeesho.features.auth.di

import com.snehonir.niyeesho.BuildKonfig
import com.snehonir.niyeesho.features.auth.data.local.AuthLocalDataSource
import com.snehonir.niyeesho.features.auth.data.local.AuthLocalDataSourceImpl
import com.snehonir.niyeesho.features.auth.data.remote.AuthRemoteDataSource
import com.snehonir.niyeesho.features.auth.data.remote.AuthRemoteDataSourceImpl
import com.snehonir.niyeesho.features.auth.data.remote.FakeAuthRemoteDataSource
import com.snehonir.niyeesho.features.auth.data.repository.AuthRepositoryImpl
import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.auth.domain.usecase.IsSignedInUseCase
import com.snehonir.niyeesho.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.snehonir.niyeesho.features.auth.domain.usecase.SignOutUseCase
import com.snehonir.niyeesho.features.auth.presentation.profile.ProfileViewModel
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    singleOf(::AuthLocalDataSourceImpl).bind<AuthLocalDataSource>()
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(Firebase.auth) }
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::SignInWithGoogleUseCase)
    singleOf(::SignOutUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::IsSignedInUseCase)
    viewModelOf(::SignInViewModel)
    viewModelOf(::ProfileViewModel)
}
