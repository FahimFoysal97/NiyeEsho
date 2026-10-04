package com.snehonir.niyeesho.features.drive.data.remote

import com.snehonir.niyeesho.core.network.DriveApiClient
import com.snehonir.niyeesho.core.utils.logD
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
        logD("DriveRemote(Network)", "initializeFolder() called for profile: ${profile.uid}")
        apiClient.createFile(
            accessToken = accessToken,
            metadataJson = json.encodeToString(DriveProfileDto.serializer(), profile),
            contentJson = json.encodeToString(DriveProfileDto.serializer(), profile),
        ).getOrThrow()
        logD("DriveRemote(Network)", "initializeFolder() -> success")
        DriveMetadataDto(folderId = "niyeesho_folder", fileIds = mapOf("profile.json" to profile.profileDriveUrl))
    }.onFailure {
        logD("DriveRemote(Network)", "initializeFolder() -> failed: ${it.message}")
    }

    override suspend fun readAllLists(accessToken: String): Result<List<DriveShoppingListDto>> = runCatching {
        logD("DriveRemote(Network)", "readAllLists() called")
        val lists = emptyList<DriveShoppingListDto>()
        logD("DriveRemote(Network)", "readAllLists() -> completed with ${lists.size} lists")
        lists
    }

    override suspend fun updateProfile(accessToken: String, profile: DriveProfileDto): Result<Unit> = runCatching {
        logD("DriveRemote(Network)", "updateProfile() called for profile: ${profile.uid}")
        initializeFolder(accessToken, profile).getOrThrow()
        logD("DriveRemote(Network)", "updateProfile() -> success")
    }.onFailure {
        logD("DriveRemote(Network)", "updateProfile() -> failed: ${it.message}")
    }

    override suspend fun saveList(accessToken: String, list: DriveShoppingListDto): Result<Unit> = runCatching {
        logD("DriveRemote(Network)", "saveList() called for list: ${list.id}")
        apiClient.createFile(
            accessToken = accessToken,
            metadataJson = "{\"name\":\"${list.id}.json\"}",
            contentJson = json.encodeToString(DriveShoppingListDto.serializer(), list),
        ).getOrThrow()
        logD("DriveRemote(Network)", "saveList() -> success for list: ${list.id}")
    }.onFailure {
        logD("DriveRemote(Network)", "saveList() -> failed: ${it.message}")
    }

    override suspend fun deleteList(accessToken: String, listId: String): Result<Unit> = runCatching {
        logD("DriveRemote(Network)", "deleteList() called for listId: $listId")
    }

    override suspend fun saveContacts(accessToken: String, contacts: List<DriveContactDto>): Result<Unit> = runCatching {
        logD("DriveRemote(Network)", "saveContacts() called with ${contacts.size} contacts")
        apiClient.createFile(
            accessToken = accessToken,
            metadataJson = "{\"name\":\"contacts.json\"}",
            contentJson = json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(DriveContactDto.serializer()),
                contacts,
            ),
        ).getOrThrow()
        logD("DriveRemote(Network)", "saveContacts() -> success")
    }.onFailure {
        logD("DriveRemote(Network)", "saveContacts() -> failed: ${it.message}")
    }

    override suspend fun readContacts(accessToken: String): Result<List<DriveContactDto>> = runCatching {
        logD("DriveRemote(Network)", "readContacts() called")
        emptyList()
    }

    override suspend fun getProfileFromDriveUrl(url: String): Result<DriveProfileDto> = runCatching {
        logD("DriveRemote(Network)", "getProfileFromDriveUrl() called for url: $url")
        throw UnsupportedOperationException("Public Drive profile retrieval requires a file URL client.")
    }.onFailure {
        logD("DriveRemote(Network)", "getProfileFromDriveUrl() -> failed: ${it.message}")
    }
}
