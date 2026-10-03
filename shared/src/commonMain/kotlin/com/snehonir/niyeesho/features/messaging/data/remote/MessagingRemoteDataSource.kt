package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto

interface MessagingRemoteDataSource {
    suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit>
}
