package com.snehonir.niyeesho.features.messaging.data.local

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock

class MessagingLocalDataSourceImpl : MessagingLocalDataSource {
    private val incoming = MutableSharedFlow<FcmMessage>(extraBufferCapacity = 32)
    private val pendingMessages = mutableListOf<PendingMessage>()

    override fun observeIncoming(): Flow<FcmMessage> = incoming.asSharedFlow()

    override suspend fun emitIncoming(message: FcmMessage) {
        incoming.emit(message)
    }

    override suspend fun enqueue(recipientUid: String, message: FcmMessage) {
        pendingMessages += PendingMessage(
            id = "${message.senderId}-${message.timestamp}-${Clock.System.now()}",
            recipientUid = recipientUid,
            message = message,
        )
    }

    override suspend fun pending(): List<PendingMessage> = pendingMessages.toList()

    override suspend fun remove(id: String) {
        pendingMessages.removeAll { it.id == id }
    }

    override suspend fun incrementRetry(id: String) {
        val index = pendingMessages.indexOfFirst { it.id == id }
        if (index >= 0) {
            pendingMessages[index] = pendingMessages[index].copy(
                retryCount = pendingMessages[index].retryCount + 1,
            )
        }
    }
}
