package com.snehonir.niyeesho.core.di

import com.snehonir.niyeesho.core.network.DriveApiClient
import com.snehonir.niyeesho.core.network.KtorClientFactory
import org.koin.dsl.module

val networkModule = module {
    single { KtorClientFactory().create() }
    single { DriveApiClient(get()) }
}
