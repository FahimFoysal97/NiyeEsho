package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveContact
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Reads the private contacts cache from Drive. */
class ReadContactsFromDriveUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String): Result<List<DriveContact>> =
        repository.readContacts(accessToken)
}
