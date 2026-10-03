package com.snehonir.niyeesho.core.di

import com.snehonir.niyeesho.core.database.AppDatabase
import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import org.koin.dsl.module

fun databaseModule(driverFactory: DatabaseDriverFactory) = module {
    single { driverFactory.createDriver() }
    single<AppDatabase> { AppDatabase(get()) }
}
