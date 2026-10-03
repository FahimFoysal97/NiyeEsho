package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveContact
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Backs up the private contacts cache to Drive. */
class SaveContactsToDriveUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String, contacts: List<DriveContact>): Result<Unit> =
        repository.saveContacts(accessToken, contacts)
}
