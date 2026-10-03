package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

/** Removes a shopping list backup from Drive. */
class DeleteListFromDriveUseCase(private val repository: DriveRepository) {
    suspend operator fun invoke(accessToken: String, listId: String): Result<Unit> =
        repository.deleteList(accessToken, listId)
}
