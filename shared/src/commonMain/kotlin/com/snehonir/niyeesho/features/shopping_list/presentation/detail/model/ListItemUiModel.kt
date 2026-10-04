package com.snehonir.niyeesho.features.shopping_list.presentation.detail.model

import com.snehonir.niyeesho.core.utils.SyncStatus
import com.snehonir.niyeesho.core.utils.toDisplayDate
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ListItemUiModel(
    val id: String,
    val name: String,
    val quantityLabel: String,
    val addedByName: String,
    val createdAtFormatted: String,
    val checked: Boolean,
    val syncStatus: SyncStatus,
    val isRead: Boolean,
) {
    companion object {
        fun fromDomain(item: ListItem): ListItemUiModel {
            val local = item.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            val timeStr = "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
            val dateStr = item.createdAt.toDisplayDate()
            return ListItemUiModel(
                id = item.id,
                name = item.name,
                quantityLabel = listOfNotNull(item.quantity, item.unit).joinToString(" "),
                addedByName = item.addedByName,
                createdAtFormatted = "$dateStr at $timeStr",
                checked = item.isChecked,
                syncStatus = item.syncStatus,
                isRead = item.isRead,
            )
        }
    }
}
