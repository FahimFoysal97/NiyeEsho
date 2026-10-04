package com.snehonir.niyeesho.app.di

import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import com.snehonir.niyeesho.core.di.coreModule
import com.snehonir.niyeesho.core.di.databaseModule
import com.snehonir.niyeesho.core.di.networkModule
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.auth.di.authModule
import com.snehonir.niyeesho.features.contacts.di.contactsModule
import com.snehonir.niyeesho.features.dashboard.di.dashboardModule
import com.snehonir.niyeesho.features.drive.di.driveModule
import com.snehonir.niyeesho.features.messaging.di.messagingModule
import com.snehonir.niyeesho.features.shopping_list.di.shoppingListModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initializeNiyeEsho(
    databaseDriverFactory: DatabaseDriverFactory,
    extraModules: List<Module> = emptyList(),
) {
    logD("NiyeEshoApp", "initializeNiyeEsho() starting Koin startKoin block")
    startKoin {
        modules(
            listOf(
                coreModule,
                databaseModule(databaseDriverFactory),
                networkModule,
                authModule,
                driveModule,
                messagingModule,
                contactsModule,
                shoppingListModule,
                dashboardModule,
            ) + extraModules,
        )
    }
    logD("NiyeEshoApp", "initializeNiyeEsho() Koin startKoin completed successfully")
}
