package com.snehonir.niyeesho.di

import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
}
