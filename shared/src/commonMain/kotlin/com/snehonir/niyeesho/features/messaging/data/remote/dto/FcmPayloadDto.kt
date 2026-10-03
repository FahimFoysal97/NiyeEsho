package com.snehonir.niyeesho.features.messaging.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmPayloadDto(
    val type: String,
    val senderId: String,
    val senderName: String,
    val listId: String? = null,
    val timestamp: String,
    val data: String = "{}",
)
