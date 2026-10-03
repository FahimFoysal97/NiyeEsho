package com.snehonir.niyeesho.features.drive.domain.repository

import com.snehonir.niyeesho.features.drive.domain.entity.DriveContact
import com.snehonir.niyeesho.features.drive.domain.entity.DriveMetadata
import com.snehonir.niyeesho.features.drive.domain.entity.DriveProfile
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList

/** Coordinates a user's private Google Drive backup and synchronization state. */
interface DriveRepository {
    suspend fun initializeFolder(accessToken: String, profile: DriveProfile): Result<DriveMetadata>
    suspend fun syncFromDrive(accessToken: String): Result<List<DriveShoppingList>>
    suspend fun updateProfile(accessToken: String, profile: DriveProfile): Result<Unit>
    suspend fun saveList(accessToken: String, list: DriveShoppingList): Result<Unit>
    suspend fun deleteList(accessToken: String, listId: String): Result<Unit>
    suspend fun saveContacts(accessToken: String, contacts: List<DriveContact>): Result<Unit>
    suspend fun readContacts(accessToken: String): Result<List<DriveContact>>
    suspend fun getProfileFromDriveUrl(url: String): Result<DriveProfile>
}
