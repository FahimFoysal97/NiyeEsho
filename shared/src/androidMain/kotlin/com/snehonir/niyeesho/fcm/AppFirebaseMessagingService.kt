package com.snehonir.niyeesho.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.snehonir.niyeesho.core.utils.TokenManagerImpl
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.data.mapper.toDomain
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
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
                    logD("AppFirebaseMessagingService", "Handling incoming FCM message type: ${fcmMessage.type} from ${fcmMessage.senderName}")

                    when (fcmMessage.type) {
                        FcmMessageType.CONTACT_REQUEST -> {
                            postSystemNotification(
                                title = "New Connection Request",
                                messageText = "${fcmMessage.senderName} wants to connect with you on Niye Esho.",
                            )
                        }
                        FcmMessageType.CONTACT_ACCEPTED -> {
                            postSystemNotification(
                                title = "Connection Accepted!",
                                messageText = "${fcmMessage.senderName} accepted your connection request.",
                            )
                        }
                        else -> Unit
                    }

                    scope.launch { handleIncomingMessage(fcmMessage) }
                }
            }.onFailure {
                logD("AppFirebaseMessagingService", "Failed to decode or handle FCM payload: ${it.message}")
            }
        }
    }

    private fun postSystemNotification(title: String, messageText: String) {
        runCatching {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "niye_esho_handshake_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Niye Esho Connection Requests",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Notifications for incoming contact requests and connection approvals"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            notificationManager.notify(notificationId, notification)
            logD("AppFirebaseMessagingService", "Posted system notification: '$title' - '$messageText'")
        }.onFailure {
            logD("AppFirebaseMessagingService", "Failed to post notification: ${it.message}")
        }
    }
}
