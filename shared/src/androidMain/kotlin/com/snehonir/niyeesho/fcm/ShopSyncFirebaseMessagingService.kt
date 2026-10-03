package com.snehonir.niyeesho.fcm

import com.snehonir.niyeesho.features.messaging.data.mapper.toDomain
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.domain.usecase.HandleIncomingMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class NiyeEshoFirebaseMessagingService(
    private val handleIncomingMessage: HandleIncomingMessageUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    fun onNewToken(token: String) {
        require(token.isNotBlank()) { "FCM token cannot be blank." }
    }

    fun onMessageReceived(data: Map<String, String>) {
        val payload = json.decodeFromString(
            FcmPayloadDto.serializer(),
            json.encodeToString(
                FcmPayloadDto.serializer(),
                FcmPayloadDto(
                    type = data["type"].orEmpty(),
                    senderId = data["senderId"].orEmpty(),
                    senderName = data["senderName"].orEmpty(),
                    listId = data["listId"],
                    timestamp = data["timestamp"].orEmpty(),
                    data = data["data"].orEmpty(),
                ),
            ),
        )
        payload.toDomain()?.let { message ->
            scope.launch { handleIncomingMessage(message) }
        }
    }
}
