package com.snehonir.niyeesho.features.invite.domain.usecase

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository

/** Persists a validated invite contact locally. */
class AddContactFromInviteUseCase(private val repository: ContactsRepository) {
    suspend operator fun invoke(contact: Contact): Result<Unit> = repository.addContact(contact)
}
