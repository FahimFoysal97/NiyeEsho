package com.snehonir.niyeesho.features.dashboard.presentation

import com.snehonir.niyeesho.core.utils.SyncStatus
import com.snehonir.niyeesho.core.utils.toDisplayDate
import com.snehonir.niyeesho.core.utils.toDisplayTime
import com.snehonir.niyeesho.core.utils.toRelativeTimeString
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList

data class DashboardUiModel(
    val id: String,
    val title: String,
    val isContact: Boolean,
    val memberCount: Int,
    val members: List<ListMember> = emptyList(),
    val totalItems: Int = 0,
    val completedItems: Int = 0,
    val lastActivityText: String,
    val createdAtFormatted: String,
    val updatedAtFormatted: String,
    val updatedAtEpoch: Long,
    val syncStatus: SyncStatus,
) {
    val progress: Float
        get() = if (totalItems > 0) completedItems.toFloat() / totalItems.toFloat() else 0f

    val progressLabel: String
        get() = if (totalItems > 0 && completedItems == totalItems) "100%" else "$completedItems/$totalItems"

    val progressDetailLabel: String
        get() = "$completedItems of $totalItems done"

    val isDone: Boolean
        get() = totalItems > 0 && completedItems == totalItems

    companion object {
        fun fromDomain(list: ShoppingList, total: Int = 0, completed: Int = 0): DashboardUiModel {
            val timeStr = list.updatedAt.toDisplayTime()
            val dateStr = list.updatedAt.toDisplayDate()
            val relativeTime = list.updatedAt.toRelativeTimeString()
            return DashboardUiModel(
                id = list.id,
                title = list.title,
                isContact = list.id.startsWith("contact_list_"),
                memberCount = list.members.size,
                members = list.members,
                totalItems = total,
                completedItems = completed,
                lastActivityText = relativeTime,
                createdAtFormatted = list.createdAt.toDisplayDate(),
                updatedAtFormatted = "$dateStr at $timeStr",
                updatedAtEpoch = list.updatedAt.toEpochMilliseconds(),
                syncStatus = list.syncStatus,
            )
        }

        fun fromContact(contact: Contact): DashboardUiModel {
            val timeStr = contact.connectedAt.toDisplayTime()
            val dateStr = contact.connectedAt.toDisplayDate()
            val member = ListMember(contact.uid, contact.displayName, contact.avatarUrl)
            val relativeTime = contact.connectedAt.toRelativeTimeString()
            return DashboardUiModel(
                id = "contact_list_${contact.uid}",
                title = contact.displayName,
                isContact = true,
                memberCount = 1,
                members = listOf(member),
                totalItems = 0,
                completedItems = 0,
                lastActivityText = relativeTime,
                createdAtFormatted = contact.connectedAt.toDisplayDate(),
                updatedAtFormatted = "$dateStr at $timeStr",
                updatedAtEpoch = contact.connectedAt.toEpochMilliseconds(),
                syncStatus = SyncStatus.Synced,
            )
        }
    }
}
