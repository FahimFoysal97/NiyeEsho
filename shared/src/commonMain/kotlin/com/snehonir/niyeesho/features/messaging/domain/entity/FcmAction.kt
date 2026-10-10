package com.snehonir.niyeesho.features.messaging.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class FcmAction {
    ADD,
    UPDATE,
    DELETE,
    REQUEST,
    ACCEPT,
    DECLINE,
}
