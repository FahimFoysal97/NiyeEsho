package com.snehonir.niyeesho.features.messaging.domain.repository

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import kotlinx.coroutines.flow.Flow

/** Sends, receives, and queues peer-to-peer FCM data messages. */
interface MessagingRepository {
    fun observeIncomingMessages(): Flow<FcmMessage>
    suspend fun send(recipientUid: String, message: FcmMessage): Result<Unit>
    suspend fun handleIncoming(message: FcmMessage): Result<Unit>
    suspend fun retryPending(): Result<Int>
    suspend fun generateInviteLink(data: InviteData): Result<String>
    suspend fun sendContactRequest(contact: InviteData): Result<Unit>
}
