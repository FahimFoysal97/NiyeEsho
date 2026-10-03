package com.snehonir.niyeesho.features.drive.data.repository

import com.snehonir.niyeesho.features.drive.data.mapper.toDomain
import com.snehonir.niyeesho.features.drive.data.mapper.toDto
import com.snehonir.niyeesho.features.drive.data.remote.DriveRemoteDataSource
import com.snehonir.niyeesho.features.drive.domain.entity.DriveContact
import com.snehonir.niyeesho.features.drive.domain.entity.DriveMetadata
import com.snehonir.niyeesho.features.drive.domain.entity.DriveProfile
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository

class DriveRepositoryImpl(
    private val remote: DriveRemoteDataSource,
) : DriveRepository {
    override suspend fun initializeFolder(accessToken: String, profile: DriveProfile): Result<DriveMetadata> =
        remote.initializeFolder(accessToken, profile.toDto()).map { it.toDomain() }

    override suspend fun syncFromDrive(accessToken: String): Result<List<DriveShoppingList>> =
        remote.readAllLists(accessToken).map { lists -> lists.map { it.toDomain() } }

    override suspend fun updateProfile(accessToken: String, profile: DriveProfile): Result<Unit> =
        remote.updateProfile(accessToken, profile.toDto())

    override suspend fun saveList(accessToken: String, list: DriveShoppingList): Result<Unit> =
        remote.saveList(accessToken, list.toDto())

    override suspend fun deleteList(accessToken: String, listId: String): Result<Unit> =
        remote.deleteList(accessToken, listId)

    override suspend fun saveContacts(accessToken: String, contacts: List<DriveContact>): Result<Unit> =
        remote.saveContacts(accessToken, contacts.map { it.toDto() })

    override suspend fun readContacts(accessToken: String): Result<List<DriveContact>> =
        remote.readContacts(accessToken).map { contacts -> contacts.map { it.toDomain() } }

    override suspend fun getProfileFromDriveUrl(url: String): Result<DriveProfile> =
        remote.getProfileFromDriveUrl(url).map { it.toDomain() }
}
