package com.snehonir.niyeesho.features.messaging.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class ContactHandshakeAction {
    REQUEST,
    ACCEPT,
    DECLINE,
    REMOVE,
}
