package com.snehonir.niyeesho.features.contacts.domain.usecase

import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository

/** Synchronizes the private contacts cache with the user's Drive. */
class SyncContactsWithDriveUseCase(private val repository: ContactsRepository) {
    suspend operator fun invoke(accessToken: String): Result<Unit> =
        repository.syncWithDrive(accessToken)
}
