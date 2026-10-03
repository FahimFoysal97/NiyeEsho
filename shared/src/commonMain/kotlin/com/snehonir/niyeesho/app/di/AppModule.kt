package com.snehonir.niyeesho.app.di

import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import com.snehonir.niyeesho.core.di.coreModule
import com.snehonir.niyeesho.core.di.databaseModule
import com.snehonir.niyeesho.core.di.networkModule
import com.snehonir.niyeesho.features.auth.di.authModule
import com.snehonir.niyeesho.features.contacts.di.contactsModule
import com.snehonir.niyeesho.features.drive.di.driveModule
import com.snehonir.niyeesho.features.messaging.di.messagingModule
import com.snehonir.niyeesho.features.shopping_list.di.shoppingListModule
import org.koin.core.context.startKoin

fun initializeNiyeEsho(databaseDriverFactory: DatabaseDriverFactory) {
    startKoin {
        modules(
            coreModule,
            databaseModule(databaseDriverFactory),
            networkModule,
            authModule,
            driveModule,
            messagingModule,
            contactsModule,
            shoppingListModule,
        )
    }
}
