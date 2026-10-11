package com.snehonir.niyeesho.features.messaging.data.repository

import com.snehonir.niyeesho.core.utils.Constants
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.core.utils.logE
import com.snehonir.niyeesho.features.messaging.data.handler.FcmMessageHandler
import com.snehonir.niyeesho.features.messaging.data.local.MessagingLocalDataSource
import com.snehonir.niyeesho.features.messaging.data.mapper.toDomain
import com.snehonir.niyeesho.features.messaging.data.mapper.toDto
import com.snehonir.niyeesho.features.messaging.data.remote.MessagingRemoteDataSource
import com.snehonir.niyeesho.features.messaging.domain.entity.ContactHandshakeAction
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
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

    override suspend fun send(recipientUid: String, recipientToken: String, message: FcmMessage): Result<Unit> {
        val targetToken = recipientToken.ifBlank { recipientUid }
        logD("MessagingRepo", "send() -> type: ${message.type}, recipientUid: $recipientUid, targetToken: ${targetToken.take(15)}...")

        val dto = message.toDto()
        if (dto.data.encodeToByteArray().size > Constants.MAX_FCM_PAYLOAD_BYTES) {
            logD("MessagingRepo", "Payload exceeds max size, enqueuing locally for sync.")
            local.enqueue(recipientUid, message)
            return Result.success(Unit)
        }

        // Always post to Realtime Handshake Sync node for peer-to-peer reliability
        remote.postHandshakeRequest(recipientUid, dto)

        return remote.send(targetToken, dto).fold(
            onSuccess = {
                logD("MessagingRepo", "send() -> FCM network dispatch SUCCESS to targetToken: ${targetToken.take(15)}...")
                Result.success(Unit)
            },
            onFailure = { error ->
                logD("MessagingRepo", "Direct FCM send failed (${error.message}); message saved to Realtime Sync node and local queue.")
                local.enqueue(recipientUid, message)
                Result.success(Unit)
            },
        )
    }

    override suspend fun handleIncoming(message: FcmMessage): Result<Unit> {
        logD("MessagingRepo", "handleIncoming() -> message type: ${message.type}, senderId: ${message.senderId}, senderName: ${message.senderName}")
        return handler.handle(message)
    }

    override suspend fun fetchPendingHandshakes(myUid: String): Result<Unit> = runCatching {
        logD("SystemNotifier", "fetchPendingHandshakes() -> checking for pending handshakes for $myUid")
        val result = remote.fetchHandshakeRequests(myUid)
        result.onSuccess { list ->
            logD("SystemNotifier", "fetchPendingHandshakes() -> found ${list.size} pending handshake payloads for $myUid")
            list.forEach { dto ->
                try {
                    val fcmMsg = dto.toDomain()
                    logD("SystemNotifier", "Processing pending handshake type: ${fcmMsg.type} from ${fcmMsg.senderName}")
                    val handleResult = handler.handle(fcmMsg)
                    handleResult.onSuccess {
                        logD("SystemNotifier", "Successfully handled pending handshake from ${fcmMsg.senderName}")
                        if (fcmMsg.type == FcmMessageType.CONTACT_ACCEPTED || fcmMsg.type == FcmMessageType.CONTACT_DECLINED) {
                            deleteHandshakeRequest(myUid, fcmMsg.senderId)
                        }
                    }.onFailure { err ->
                        logE("SystemNotifier", "Failed to handle pending handshake from ${fcmMsg.senderName}: ${err.message}", err)
                    }
                } catch (e: Throwable) {
                    logE("SystemNotifier", "Error processing DTO: ${e.message}", e)
                }
            }
        }.onFailure { err ->
            logE("SystemNotifier", "fetchHandshakeRequests failed: ${err.message}", err)
        }
        Unit
    }

    override suspend fun deleteHandshakeRequest(myUid: String, senderUid: String): Result<Unit> =
        remote.deleteHandshakeRequest(myUid, senderUid)

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
        val now = Clock.System.now().toEpochMilliseconds()
        val expiresAt = now + Constants.INVITE_LINK_EXPIRY_MS
        buildString {
            append("niyeesho://invite?uid=").append(data.uid)
            append("&name=").append(data.name)
            data.email?.let { append("&email=").append(it) }
            append("&token=").append(data.token)
            append("&driveUrl=").append(data.driveUrl)
            data.avatar?.let { append("&avatar=").append(it) }
            data.listId?.let { append("&listId=").append(it) }
            data.listTitle?.let { append("&listTitle=").append(it) }
            append("&ts=").append(now)
            append("&expiresAt=").append(expiresAt)
        }
    }

    override suspend fun sendContactRequest(recipientUid: String, recipientToken: String, senderData: InviteData): Result<Unit> {
        val message = FcmMessage(
            type = FcmMessageType.CONTACT_REQUEST,
            senderId = senderData.uid,
            senderName = senderData.name,
            listId = null,
            timestamp = Clock.System.now(),
            data = FcmMessageData.ContactHandshake(
                action = ContactHandshakeAction.REQUEST,
                email = senderData.email,
                avatarUrl = senderData.avatar,
                fcmToken = senderData.token,
            ),
        )
        logD("MessagingRepo", "sendContactRequest() -> sending CONTACT_REQUEST to recipientUid: $recipientUid, recipientToken: ${recipientToken.take(15)}... from senderUid: ${senderData.uid} (${senderData.name})")
        return send(recipientUid, recipientToken, message)
    }
}
