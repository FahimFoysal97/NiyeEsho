package com.snehonir.niyeesho.di

import android.content.Context
import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import com.snehonir.niyeesho.core.network.AndroidConnectivityObserver
import com.snehonir.niyeesho.core.network.ConnectivityObserver
import com.snehonir.niyeesho.core.network.KtorClientFactory
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    single { DatabaseDriverFactory(context) }
    single { KtorClientFactory() }
    single<ConnectivityObserver> { AndroidConnectivityObserver(context) }
}
