package com.snehonir.niyeesho.features.contacts.domain.usecase

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository

/** Searches cached contacts by name or email. */
class SearchContactsUseCase(private val repository: ContactsRepository) {
    suspend operator fun invoke(query: String): Result<List<Contact>> =
        repository.searchContacts(query)
}
