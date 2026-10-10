package com.snehonir.niyeesho.features.contacts.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class ContactStatus {
    ACTIVE,
    PENDING_OUTGOING,
    PENDING_INCOMING,
    DECLINED,
}
