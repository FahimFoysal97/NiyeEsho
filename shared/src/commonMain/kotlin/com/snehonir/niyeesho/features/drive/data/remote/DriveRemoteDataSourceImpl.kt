package com.snehonir.niyeesho.features.drive.data.remote

import com.snehonir.niyeesho.core.network.DriveApiClient
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveContactDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveMetadataDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveProfileDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveShoppingListDto
import kotlinx.serialization.json.Json

class DriveRemoteDataSourceImpl(
    private val apiClient: DriveApiClient,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : DriveRemoteDataSource {
    override suspend fun initializeFolder(
        accessToken: String,
        profile: DriveProfileDto,
    ): Result<DriveMetadataDto> = runCatching {
        apiClient.createFile(
            accessToken = accessToken,
            metadataJson = json.encodeToString(DriveProfileDto.serializer(), profile),
            contentJson = json.encodeToString(DriveProfileDto.serializer(), profile),
        ).getOrThrow()
        DriveMetadataDto(folderId = "niyeesho_folder", fileIds = mapOf("profile.json" to profile.profileDriveUrl))
    }

    override suspend fun readAllLists(accessToken: String): Result<List<DriveShoppingListDto>> =
        Result.success(emptyList())

    override suspend fun updateProfile(accessToken: String, profile: DriveProfileDto): Result<Unit> =
        initializeFolder(accessToken, profile).map { Unit }

    override suspend fun saveList(accessToken: String, list: DriveShoppingListDto): Result<Unit> =
        apiClient.createFile(
            accessToken = accessToken,
            metadataJson = "{\"name\":\"${list.id}.json\"}",
            contentJson = json.encodeToString(DriveShoppingListDto.serializer(), list),
        ).map { Unit }

    override suspend fun deleteList(accessToken: String, listId: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun saveContacts(accessToken: String, contacts: List<DriveContactDto>): Result<Unit> =
        apiClient.createFile(
            accessToken = accessToken,
            metadataJson = "{\"name\":\"contacts.json\"}",
            contentJson = json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(DriveContactDto.serializer()),
                contacts,
            ),
        ).map { Unit }

    override suspend fun readContacts(accessToken: String): Result<List<DriveContactDto>> =
        Result.success(emptyList())

    override suspend fun getProfileFromDriveUrl(url: String): Result<DriveProfileDto> =
        Result.failure(UnsupportedOperationException("Public Drive profile retrieval requires a file URL client."))
}
