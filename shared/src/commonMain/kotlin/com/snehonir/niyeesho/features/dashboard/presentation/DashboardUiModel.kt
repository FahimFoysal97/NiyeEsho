package com.snehonir.niyeesho.features.dashboard.presentation

import com.snehonir.niyeesho.core.utils.SyncStatus
import com.snehonir.niyeesho.core.utils.toDisplayDate
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DashboardUiModel(
    val id: String,
    val title: String,
    val isContact: Boolean,
    val memberCount: Int,
    val createdAtFormatted: String,
    val updatedAtFormatted: String,
    val updatedAtEpoch: Long,
    val syncStatus: SyncStatus,
) {
    companion object {
        fun fromDomain(list: ShoppingList): DashboardUiModel {
            val localUpdated = list.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
            val timeStr = "${localUpdated.hour.toString().padStart(2, '0')}:${localUpdated.minute.toString().padStart(2, '0')}"
            val dateStr = list.updatedAt.toDisplayDate()
            return DashboardUiModel(
                id = list.id,
                title = list.title,
                isContact = list.id.startsWith("contact_list_"),
                memberCount = list.members.size,
                createdAtFormatted = list.createdAt.toDisplayDate(),
                updatedAtFormatted = "$dateStr at $timeStr",
                updatedAtEpoch = list.updatedAt.toEpochMilliseconds(),
                syncStatus = list.syncStatus,
            )
        }

        fun fromContact(contact: Contact): DashboardUiModel {
            val localUpdated = contact.connectedAt.toLocalDateTime(TimeZone.currentSystemDefault())
            val timeStr = "${localUpdated.hour.toString().padStart(2, '0')}:${localUpdated.minute.toString().padStart(2, '0')}"
            val dateStr = contact.connectedAt.toDisplayDate()
            return DashboardUiModel(
                id = "contact_list_${contact.uid}",
                title = contact.displayName,
                isContact = true,
                memberCount = 1,
                createdAtFormatted = contact.connectedAt.toDisplayDate(),
                updatedAtFormatted = "$dateStr at $timeStr",
                updatedAtEpoch = contact.connectedAt.toEpochMilliseconds(),
                syncStatus = SyncStatus.Synced,
            )
        }
    }
}
