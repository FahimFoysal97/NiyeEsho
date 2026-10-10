package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto

interface MessagingRemoteDataSource {
    suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit>
    suspend fun postHandshakeRequest(recipientUid: String, payload: FcmPayloadDto): Result<Unit>
    suspend fun fetchHandshakeRequests(myUid: String): Result<List<FcmPayloadDto>>
}
