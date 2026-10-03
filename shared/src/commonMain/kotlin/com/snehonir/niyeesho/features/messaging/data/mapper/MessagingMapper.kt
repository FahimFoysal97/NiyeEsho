package com.snehonir.niyeesho.features.messaging.data.mapper

import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import kotlinx.datetime.Instant

fun FcmPayloadDto.toDomain(): FcmMessage? {
    val messageType = runCatching { FcmMessageType.valueOf(type) }.getOrNull() ?: return null
    return FcmMessage(
        type = messageType,
        senderId = senderId,
        senderName = senderName,
        listId = listId?.takeIf(String::isNotBlank),
        timestamp = runCatching { Instant.parse(timestamp) }.getOrElse { return null },
        data = data,
    )
}

fun FcmMessage.toDto() = FcmPayloadDto(
    type = type.name,
    senderId = senderId,
    senderName = senderName,
    listId = listId,
    timestamp = timestamp.toString(),
    data = data,
)
