package com.snehonir.niyeesho.features.messaging.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class ShoppingListAction {
    ADD,
    UPDATE,
    DELETE,
}
