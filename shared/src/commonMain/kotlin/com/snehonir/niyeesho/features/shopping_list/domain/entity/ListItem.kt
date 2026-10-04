package com.snehonir.niyeesho.features.shopping_list.domain.entity

import com.snehonir.niyeesho.core.utils.SyncStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ListItem(
    val id: String,
    val listId: String,
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
    val syncStatus: SyncStatus = SyncStatus.Synced,
    val isRead: Boolean = true,
) {
    companion object {
        fun preview() = ListItem(
            id = "item_001",
            listId = "list_001",
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
            isRead = false,
        )
    }
}
