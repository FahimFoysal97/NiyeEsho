package com.snehonir.niyeesho.features.contacts.domain.usecase

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository

/** Refreshes a stale contact FCM token from the public Drive profile. */
class RefreshContactTokenUseCase(private val repository: ContactsRepository) {
    suspend operator fun invoke(uid: String): Result<Contact> =
        repository.refreshContactToken(uid)
}
