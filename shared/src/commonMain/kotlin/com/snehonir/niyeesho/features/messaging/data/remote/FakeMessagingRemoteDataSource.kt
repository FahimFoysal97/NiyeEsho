package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMessagingRemoteDataSource : MessagingRemoteDataSource {
    private val pendingHandshakes = mutableMapOf<String, MutableList<FcmPayloadDto>>()

    override suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit> {
        delay(60)
        return if (recipientToken.isBlank()) {
            Result.failure(IllegalArgumentException("Recipient FCM token is blank."))
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun postHandshakeRequest(recipientUid: String, payload: FcmPayloadDto): Result<Unit> {
        delay(30)
        val list = pendingHandshakes.getOrPut(recipientUid) { mutableListOf() }
        list.add(payload)
        return Result.success(Unit)
    }

    override suspend fun fetchHandshakeRequests(myUid: String): Result<List<FcmPayloadDto>> {
        delay(30)
        val list = pendingHandshakes[myUid] ?: emptyList()
        return Result.success(list.toList())
    }

    override suspend fun deleteHandshakeRequest(myUid: String, senderUid: String): Result<Unit> {
        delay(30)
        pendingHandshakes[myUid]?.removeAll { it.senderId == senderUid }
        return Result.success(Unit)
    }

    override fun watchHandshakeRequests(myUid: String): Flow<List<FcmPayloadDto>> = flow {
        emit(pendingHandshakes[myUid] ?: emptyList())
    }
}
