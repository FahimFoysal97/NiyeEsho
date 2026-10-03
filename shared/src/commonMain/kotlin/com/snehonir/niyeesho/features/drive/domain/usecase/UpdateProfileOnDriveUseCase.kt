package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveProfile
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Writes the latest public profile and FCM token to Drive. */
class UpdateProfileOnDriveUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String, profile: DriveProfile): Result<Unit> =
        repository.updateProfile(accessToken, profile)
}
