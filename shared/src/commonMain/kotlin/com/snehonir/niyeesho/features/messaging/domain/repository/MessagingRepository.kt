package com.snehonir.niyeesho.features.messaging.domain.repository

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import kotlinx.coroutines.flow.Flow

/** Sends, receives, and queues peer-to-peer FCM data messages. */
interface MessagingRepository {
    fun observeIncomingMessages(): Flow<FcmMessage>
    fun observeHandshakeRequests(myUid: String): Flow<Unit>
    suspend fun send(recipientUid: String, recipientToken: String, message: FcmMessage): Result<Unit>
    suspend fun handleIncoming(message: FcmMessage): Result<Unit>
    suspend fun fetchPendingHandshakes(myUid: String): Result<Unit>
    suspend fun deleteHandshakeRequest(myUid: String, senderUid: String): Result<Unit>
    suspend fun retryPending(): Result<Int>
    suspend fun generateInviteLink(data: InviteData): Result<String>
    suspend fun sendContactRequest(recipientUid: String, recipientToken: String, senderData: InviteData): Result<Unit>
}
