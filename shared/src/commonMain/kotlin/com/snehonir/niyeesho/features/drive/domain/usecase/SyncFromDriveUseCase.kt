package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Imports the user's Drive-backed shopping lists. */
class SyncFromDriveUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String): Result<List<DriveShoppingList>> =
        repository.syncFromDrive(accessToken)
}
