package com.snehonir.niyeesho.core.di

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.core.utils.TokenManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<Settings> { Settings() }
    single<Json> { Json { ignoreUnknownKeys = true } }
    singleOf(::TokenManagerImpl).bind<TokenManager>()
}
