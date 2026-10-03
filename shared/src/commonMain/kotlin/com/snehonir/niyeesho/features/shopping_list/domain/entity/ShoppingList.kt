package com.snehonir.niyeesho.features.shopping_list.domain.entity

import com.snehonir.niyeesho.core.utils.SyncStatus
import kotlinx.datetime.Instant

data class ShoppingList(
    val id: String,
    val title: String,
    val ownerId: String,
    val memberIds: List<String>,
    val members: List<ListMember>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastSyncedAt: Instant?,
    val isArchived: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.Synced,
) {
    companion object {
        fun preview() = ShoppingList(
            id = "list_001",
            title = "Weekly Groceries",
            ownerId = "user_demo",
            memberIds = listOf("user_demo", "user_rahim"),
            members = listOf(ListMember.preview(), ListMember("user_rahim", "Rahim Ahmed", null)),
            createdAt = Instant.parse("2026-01-15T10:30:00Z"),
            updatedAt = Instant.parse("2026-06-15T09:22:00Z"),
            lastSyncedAt = Instant.parse("2026-06-15T10:30:00Z"),
        )
    }
}
