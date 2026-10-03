package com.snehonir.niyeesho.features.drive.data.remote

import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveContactDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveListItemDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveListMemberDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveMetadataDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveProfileDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveShoppingListDto
import kotlinx.coroutines.delay

class FakeDriveRemoteDataSource : DriveRemoteDataSource {
    private val profile = DriveProfileDto(
        uid = "user_demo",
        displayName = "Sneho Nir",
        email = "sneho@example.com",
        avatarUrl = null,
        fcmToken = "demo_fcm_token",
        tokenUpdatedAt = "2026-06-15T10:00:00Z",
        appVersion = "1.0.0",
        profileDriveUrl = "https://drive.google.com/uc?id=profile_demo&export=download",
    )

    private val lists = mutableListOf(
        createList("list_001", "Weekly Groceries", listOf("Milk", "Eggs", "Rice", "Bananas", "Spinach", "Bread")),
        createList("list_002", "Weekend Barbecue", listOf("Chicken", "Charcoal", "Corn", "Lemonade", "Paper plates", "Sauce")),
        createList("list_003", "Home Essentials", listOf("Laundry detergent", "Dish soap", "Tissues", "Light bulbs", "Batteries", "Trash bags")),
    )

    override suspend fun initializeFolder(accessToken: String, profile: DriveProfileDto): Result<DriveMetadataDto> {
        delay(120)
        return Result.success(
            DriveMetadataDto(
                lastSyncedAt = "2026-06-15T10:30:00Z",
                folderId = "niyeesho_folder_demo",
                fileIds = mapOf("profile.json" to "profile_demo", "metadata.json" to "metadata_demo"),
            ),
        )
    }

    override suspend fun readAllLists(accessToken: String): Result<List<DriveShoppingListDto>> {
        delay(180)
        return Result.success(lists.toList())
    }

    override suspend fun updateProfile(accessToken: String, profile: DriveProfileDto): Result<Unit> {
        delay(80)
        return Result.success(Unit)
    }

    override suspend fun saveList(accessToken: String, list: DriveShoppingListDto): Result<Unit> {
        delay(100)
        lists.removeAll { it.id == list.id }
        lists += list
        return Result.success(Unit)
    }

    override suspend fun deleteList(accessToken: String, listId: String): Result<Unit> {
        delay(80)
        lists.removeAll { it.id == listId }
        return Result.success(Unit)
    }

    override suspend fun saveContacts(accessToken: String, contacts: List<DriveContactDto>): Result<Unit> {
        delay(80)
        return Result.success(Unit)
    }

    override suspend fun readContacts(accessToken: String): Result<List<DriveContactDto>> {
        delay(100)
        return Result.success(
            listOf(
                DriveContactDto(
                    uid = "user_rahim",
                    displayName = "Rahim Ahmed",
                    email = "rahim@example.com",
                    profileDriveUrl = "https://drive.google.com/uc?id=rahim_profile&export=download",
                    cachedFcmToken = "rahim_fcm_token",
                    tokenCachedAt = "2026-06-15T08:00:00Z",
                    connectedAt = "2026-01-10T00:00:00Z",
                ),
            ),
        )
    }

    override suspend fun getProfileFromDriveUrl(url: String): Result<DriveProfileDto> {
        delay(100)
        return Result.success(profile.copy(profileDriveUrl = url))
    }

    private fun createList(id: String, title: String, names: List<String>) =
        DriveShoppingListDto(
            id = id,
            title = title,
            ownerId = "user_demo",
            memberIds = listOf("user_demo", "user_rahim"),
            members = listOf(
                DriveListMemberDto("user_demo", "Sneho Nir"),
                DriveListMemberDto("user_rahim", "Rahim Ahmed"),
            ),
            createdAt = "2026-01-15T10:30:00Z",
            updatedAt = "2026-06-15T09:22:00Z",
            lastSyncedAt = "2026-06-15T10:30:00Z",
            items = names.mapIndexed { index, name ->
                DriveListItemDto(
                    id = "${id}_item_${index + 1}",
                    name = name,
                    quantity = if (index % 2 == 0) "2" else "1",
                    unit = if (index % 2 == 0) "pack" else "item",
                    isChecked = index == 0,
                    addedBy = if (index % 2 == 0) "user_demo" else "user_rahim",
                    addedByName = if (index % 2 == 0) "Sneho Nir" else "Rahim Ahmed",
                    checkedBy = if (index == 0) "user_demo" else null,
                    position = index,
                    createdAt = "2026-06-15T09:00:00Z",
                    updatedAt = "2026-06-15T09:22:00Z",
                )
            },
        )
}
