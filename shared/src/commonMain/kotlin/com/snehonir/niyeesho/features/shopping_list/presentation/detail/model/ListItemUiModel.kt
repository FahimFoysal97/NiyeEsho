package com.snehonir.niyeesho.features.shopping_list.presentation.detail.model

import com.snehonir.niyeesho.core.utils.SyncStatus
import com.snehonir.niyeesho.core.utils.toDisplayDate
import com.snehonir.niyeesho.core.utils.toDisplayTime
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem

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
            val timeStr = item.createdAt.toDisplayTime()
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
