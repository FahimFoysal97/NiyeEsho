package com.snehonir.niyeesho.features.messaging.data.repository

import com.snehonir.niyeesho.core.utils.Constants
import com.snehonir.niyeesho.features.messaging.data.handler.FcmMessageHandler
import com.snehonir.niyeesho.features.messaging.data.local.MessagingLocalDataSource
import com.snehonir.niyeesho.features.messaging.data.mapper.toDto
import com.snehonir.niyeesho.features.messaging.data.remote.MessagingRemoteDataSource
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class MessagingRepositoryImpl(
    private val remote: MessagingRemoteDataSource,
    private val local: MessagingLocalDataSource,
    private val handler: FcmMessageHandler,
) : MessagingRepository {
    override fun observeIncomingMessages(): Flow<FcmMessage> = local.observeIncoming()

    override suspend fun send(recipientUid: String, message: FcmMessage): Result<Unit> {
        if (message.data.encodeToByteArray().size > Constants.MAX_FCM_PAYLOAD_BYTES) {
            return local.enqueue(recipientUid, message).let {
                Result.failure(IllegalArgumentException("FCM payload exceeds the safe limit; sync request required."))
            }
        }
        return remote.send(recipientUid, message.toDto()).onFailure {
            local.enqueue(recipientUid, message)
        }
    }

    override suspend fun handleIncoming(message: FcmMessage): Result<Unit> =
        handler.handle(message)

    override suspend fun retryPending(): Result<Int> {
        var retried = 0
        local.pending().forEach { pending ->
            if (pending.retryCount >= 3) return@forEach
            remote.send(pending.recipientUid, pending.message.toDto())
                .onSuccess {
                    local.remove(pending.id)
                    retried++
                }
                .onFailure { local.incrementRetry(pending.id) }
        }
        return Result.success(retried)
    }

    override suspend fun generateInviteLink(data: InviteData): Result<String> = runCatching {
        buildString {
            append("niyeesho://invite?uid=").append(data.uid)
            append("&name=").append(data.name)
            data.email?.let { append("&email=").append(it) }
            append("&token=").append(data.token)
            append("&driveUrl=").append(data.driveUrl)
            data.avatar?.let { append("&avatar=").append(it) }
            data.listId?.let { append("&listId=").append(it) }
            data.listTitle?.let { append("&listTitle=").append(it) }
        }
    }

    override suspend fun sendContactRequest(contact: InviteData): Result<Unit> =
        send(
            recipientUid = contact.uid,
            message = FcmMessage(
                type = FcmMessageType.CONTACT_ACCEPTED,
                senderId = contact.uid,
                senderName = contact.name,
                listId = contact.listId,
                timestamp = Clock.System.now(),
                data = "{\"token\":\"${contact.token}\",\"driveUrl\":\"${contact.driveUrl}\"}",
            ),
        )
}
