package com.snehonir.niyeesho.features.messaging.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmSendRequestDto(
    val message: FcmMessageRequestDto,
)

@Serializable
data class FcmMessageRequestDto(
    val token: String,
    val data: Map<String, String>,
)
