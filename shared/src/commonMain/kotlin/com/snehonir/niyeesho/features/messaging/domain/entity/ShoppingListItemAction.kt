package com.snehonir.niyeesho.features.messaging.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class ShoppingListItemAction {
    ADD,
    UPDATE,
    DELETE,
    REORDER,
}
