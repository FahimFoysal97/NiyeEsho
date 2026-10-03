package com.snehonir.niyeesho.features.shopping_list.presentation.detail.model

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem

data class ListItemUiModel(
    val id: String,
    val name: String,
    val quantityLabel: String,
    val checked: Boolean,
) {
    companion object {
        fun fromDomain(item: ListItem) = ListItemUiModel(
            item.id,
            item.name,
            listOfNotNull(item.quantity, item.unit).joinToString(" "),
            item.isChecked,
        )
    }
}
