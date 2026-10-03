package com.snehonir.niyeesho.features.contacts.domain.usecase

import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository

/** Removes a contact from the local cache. */
class RemoveContactUseCase(private val repository: ContactsRepository) {
    suspend operator fun invoke(uid: String): Result<Unit> =
        if (uid.isBlank()) Result.failure(IllegalArgumentException("Contact ID cannot be blank."))
        else repository.removeContact(uid)
}
