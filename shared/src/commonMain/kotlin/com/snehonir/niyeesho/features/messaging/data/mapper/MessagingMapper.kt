package com.snehonir.niyeesho.features.messaging.data.mapper

import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.domain.entity.ContactHandshakeAction
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val json = Json { ignoreUnknownKeys = true }

fun FcmPayloadDto.toDomain(): FcmMessage {
    val messageType = runCatching { FcmMessageType.valueOf(type) }.getOrNull() ?: FcmMessageType.CONTACT_REQUEST

    val parsedData = runCatching {
        val trimmed = data.trim()
        if (trimmed.isBlank()) {
            FcmMessageData.Generic()
        } else if (trimmed.startsWith("{")) {
            runCatching { json.decodeFromString<FcmMessageData>(trimmed) }.getOrElse {
                val element = runCatching { json.parseToJsonElement(trimmed) as? JsonObject }.getOrNull()
                if (element != null) {
                    val email = element["email"]?.jsonPrimitive?.contentOrNull
                    val avatarUrl = element["avatarUrl"]?.jsonPrimitive?.contentOrNull
                    val fcmToken = element["fcmToken"]?.jsonPrimitive?.contentOrNull
                    FcmMessageData.ContactHandshake(
                        action = ContactHandshakeAction.REQUEST,
                        email = email,
                        avatarUrl = avatarUrl,
                        fcmToken = fcmToken,
                    )
                } else {
                    FcmMessageData.Generic(text = trimmed)
                }
            }
        } else {
            FcmMessageData.Generic(text = trimmed)
        }
    }.getOrElse { error ->
        logD("MessagingMapper", "Failed to parse data JSON: $data (${error.message})")
        FcmMessageData.Generic(text = data)
    }

    val parsedTime = runCatching { Instant.parse(timestamp) }.getOrElse { Clock.System.now() }

    return FcmMessage(
        type = messageType,
        senderId = senderId,
        senderName = senderName,
        listId = listId?.takeIf(String::isNotBlank),
        timestamp = parsedTime,
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
