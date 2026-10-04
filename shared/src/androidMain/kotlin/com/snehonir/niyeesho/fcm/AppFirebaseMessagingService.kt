package com.snehonir.niyeesho.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.snehonir.niyeesho.core.utils.TokenManagerImpl
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.data.mapper.toDomain
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.domain.usecase.HandleIncomingMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    private val handleIncomingMessage: HandleIncomingMessageUseCase by inject()
    private val tokenManager = TokenManagerImpl()
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        require(token.isNotBlank()) { "FCM token cannot be blank." }
        logD("AppFirebaseMessagingService", "onNewToken() -> got token: $token")
        tokenManager.saveFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        logD("AppFirebaseMessagingService", "Message received from: ${message.from}")
        val data = message.data
        if (data.isNotEmpty()) {
            logD("AppFirebaseMessagingService", "Processing FCM payload: $data")
            runCatching {
                val payload = FcmPayloadDto(
                    type = data["type"].orEmpty(),
                    senderId = data["senderId"].orEmpty(),
                    senderName = data["senderName"].orEmpty(),
                    listId = data["listId"],
                    timestamp = data["timestamp"].orEmpty(),
                    data = data["data"].orEmpty(),
                )
                payload.toDomain()?.let { fcmMessage ->
                    logD("AppFirebaseMessagingService", "Handling incoming FCM message type: ${fcmMessage.type}")
                    scope.launch { handleIncomingMessage(fcmMessage) }
                }
            }.onFailure {
                logD("AppFirebaseMessagingService", "Failed to decode or handle FCM payload: ${it.message}")
            }
        }
    }
}
