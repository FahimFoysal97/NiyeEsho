package com.snehonir.niyeesho.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.snehonir.niyeesho.core.utils.TokenManagerImpl
import com.snehonir.niyeesho.core.utils.logD

class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val tokenManager = TokenManagerImpl()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logD("AppFirebaseMessagingService", "onNewToken() -> got token: $token")
        tokenManager.saveFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        logD("AppFirebaseMessagingService", "Message received from: ${message.from}")
        val data = message.data
        if (data.isNotEmpty()) {
            logD("AppFirebaseMessagingService", "Message data: $data")
        }
    }
}
