package com.snehonir.niyeesho.features.contacts.di

import com.snehonir.niyeesho.features.contacts.data.local.ContactsLocalDataSource
import com.snehonir.niyeesho.features.contacts.data.local.ContactsLocalDataSourceImpl
import com.snehonir.niyeesho.features.contacts.data.repository.ContactsRepositoryImpl
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import com.snehonir.niyeesho.features.contacts.domain.usecase.RefreshContactTokenUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.RemoveContactUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.SearchContactsUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.SyncContactsWithDriveUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.contacts.presentation.ContactsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val contactsModule = module {
    singleOf(::ContactsLocalDataSourceImpl).bind<ContactsLocalDataSource>()
    singleOf(::ContactsRepositoryImpl).bind<ContactsRepository>()
    singleOf(::WatchContactsUseCase)
    singleOf(::RemoveContactUseCase)
    singleOf(::SearchContactsUseCase)
    singleOf(::RefreshContactTokenUseCase)
    singleOf(::SyncContactsWithDriveUseCase)
    viewModelOf(::ContactsViewModel)
}
