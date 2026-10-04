package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.core.network.NetworkConstants
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmMessageRequestDto
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmSendRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class MessagingRemoteDataSourceImpl(
    private val client: HttpClient,
    private val json: Json = Json,
) : MessagingRemoteDataSource {
    override suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit> = runCatching {
        logD("MessagingRemote(Network)", "send() called for recipientToken: ${recipientToken.take(10)}..., payloadType: ${payload.type}")
        client.post(
            "${NetworkConstants.fcmBaseUrl}projects/${NetworkConstants.fcmProjectId}/messages:send",
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    FcmSendRequestDto.serializer(),
                    FcmSendRequestDto(
                        FcmMessageRequestDto(
                            token = recipientToken,
                            data = mapOf(
                                "type" to payload.type,
                                "senderId" to payload.senderId,
                                "senderName" to payload.senderName,
                                "listId" to (payload.listId ?: ""),
                                "timestamp" to payload.timestamp,
                                "data" to payload.data,
                            ),
                        ),
                    ),
                ),
            )
        }
        logD("MessagingRemote(Network)", "send() -> success")
    }.onFailure {
        logD("MessagingRemote(Network)", "send() -> failed: ${it.message}")
    }
}
