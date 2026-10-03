package com.snehonir.niyeesho.features.messaging.data.local

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import kotlinx.coroutines.flow.Flow

interface MessagingLocalDataSource {
    fun observeIncoming(): Flow<FcmMessage>
    suspend fun emitIncoming(message: FcmMessage)
    suspend fun enqueue(recipientUid: String, message: FcmMessage)
    suspend fun pending(): List<PendingMessage>
    suspend fun remove(id: String)
    suspend fun incrementRetry(id: String)
}

data class PendingMessage(
    val id: String,
    val recipientUid: String,
    val message: FcmMessage,
    val retryCount: Int = 0,
)
