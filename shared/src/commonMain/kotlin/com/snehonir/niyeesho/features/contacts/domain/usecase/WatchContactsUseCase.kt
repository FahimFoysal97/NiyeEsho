package com.snehonir.niyeesho.features.contacts.domain.usecase

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow

/** Observes the local-first contacts list. */
class WatchContactsUseCase(private val repository: ContactsRepository) {
    operator fun invoke(): Flow<Result<List<Contact>>> = repository.watchContacts()
}
