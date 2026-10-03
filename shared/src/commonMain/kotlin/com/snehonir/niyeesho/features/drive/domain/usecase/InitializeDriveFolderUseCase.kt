package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveMetadata
import com.snehonir.niyeesho.features.drive.domain.entity.DriveProfile
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Creates or restores the private NiyeEsho folder structure. */
class InitializeDriveFolderUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String, profile: DriveProfile): Result<DriveMetadata> =
        repository.initializeFolder(accessToken, profile)
}
