package com.snehonir.niyeesho.features.drive.domain.entity

import kotlinx.datetime.Instant

data class DriveShoppingList(
    val id: String,
    val title: String,
    val ownerId: String,
    val memberIds: List<String>,
    val members: List<DriveListMember>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastSyncedAt: Instant?,
    val items: List<DriveListItem>,
    val isArchived: Boolean = false,
) {
    companion object {
        fun preview() = DriveShoppingList(
            id = "list_001",
            title = "Weekly Groceries",
            ownerId = "user_demo",
            memberIds = listOf("user_demo", "user_rahim"),
            members = listOf(DriveListMember.preview(), DriveListMember.preview("user_rahim", "Rahim Ahmed")),
            createdAt = Instant.parse("2026-01-15T10:30:00Z"),
            updatedAt = Instant.parse("2026-06-15T09:22:00Z"),
            lastSyncedAt = Instant.parse("2026-06-15T10:30:00Z"),
            items = listOf(DriveListItem.preview()),
        )
    }
}

data class DriveListMember(
    val uid: String,
    val displayName: String,
    val avatarUrl: String?,
) {
    companion object {
        fun preview(uid: String = "user_demo", name: String = "Sneho Nir") =
            DriveListMember(uid, name, null)
    }
}

data class DriveListItem(
    val id: String,
    val name: String,
    val quantity: String?,
    val unit: String?,
    val isChecked: Boolean,
    val addedBy: String,
    val addedByName: String,
    val checkedBy: String?,
    val position: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun preview() = DriveListItem(
            id = "item_001",
            name = "Milk",
            quantity = "2",
            unit = "litre",
            isChecked = false,
            addedBy = "user_demo",
            addedByName = "Sneho Nir",
            checkedBy = null,
            position = 0,
            createdAt = Instant.parse("2026-06-15T09:00:00Z"),
            updatedAt = Instant.parse("2026-06-15T09:22:00Z"),
        )
    }
}
