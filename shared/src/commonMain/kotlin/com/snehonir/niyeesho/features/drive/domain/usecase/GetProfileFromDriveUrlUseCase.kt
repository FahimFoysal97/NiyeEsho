package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveProfile
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Fetches a contact's public profile using its permanent Drive URL. */
class GetProfileFromDriveUrlUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(url: String): Result<DriveProfile> =
        repository.getProfileFromDriveUrl(url)
}
