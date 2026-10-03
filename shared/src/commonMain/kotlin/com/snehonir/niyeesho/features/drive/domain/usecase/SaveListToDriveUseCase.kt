package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Backs up one complete shopping list to the owner's Drive. */
class SaveListToDriveUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String, list: DriveShoppingList): Result<Unit> =
        repository.saveList(accessToken, list)
}
