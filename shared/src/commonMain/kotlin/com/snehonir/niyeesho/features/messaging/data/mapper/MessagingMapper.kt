package com.snehonir.niyeesho.features.messaging.data.mapper

import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun FcmPayloadDto.toDomain(): FcmMessage? {
    val messageType = runCatching { FcmMessageType.valueOf(type) }.getOrNull() ?: return null
    val parsedData = runCatching {
        val trimmed = data.trim()
        if (trimmed.isBlank()) FcmMessageData.Generic()
        else if (trimmed.startsWith("{")) json.decodeFromString<FcmMessageData>(trimmed)
        else FcmMessageData.Generic(text = trimmed)
    }.getOrDefault(FcmMessageData.Generic(text = data))

    return FcmMessage(
        type = messageType,
        senderId = senderId,
        senderName = senderName,
        listId = listId?.takeIf(String::isNotBlank),
        timestamp = runCatching { Instant.parse(timestamp) }.getOrElse { return null },
        data = parsedData,
    )
}

fun FcmMessage.toDto() = FcmPayloadDto(
    type = type.name,
    senderId = senderId,
    senderName = senderName,
    listId = listId,
    timestamp = timestamp.toString(),
    data = json.encodeToString(FcmMessageData.serializer(), data),
)
