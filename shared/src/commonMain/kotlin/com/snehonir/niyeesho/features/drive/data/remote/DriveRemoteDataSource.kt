package com.snehonir.niyeesho.features.drive.data.remote

import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveContactDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveMetadataDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveProfileDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveShoppingListDto

interface DriveRemoteDataSource {
    suspend fun initializeFolder(accessToken: String, profile: DriveProfileDto): Result<DriveMetadataDto>
    suspend fun readAllLists(accessToken: String): Result<List<DriveShoppingListDto>>
    suspend fun updateProfile(accessToken: String, profile: DriveProfileDto): Result<Unit>
    suspend fun saveList(accessToken: String, list: DriveShoppingListDto): Result<Unit>
    suspend fun deleteList(accessToken: String, listId: String): Result<Unit>
    suspend fun saveContacts(accessToken: String, contacts: List<DriveContactDto>): Result<Unit>
    suspend fun readContacts(accessToken: String): Result<List<DriveContactDto>>
    suspend fun getProfileFromDriveUrl(url: String): Result<DriveProfileDto>
}
