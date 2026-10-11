package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.core.network.FcmTokenProvider
import com.snehonir.niyeesho.core.network.NetworkConstants
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmMessageRequestDto
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmSendRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MessagingRemoteDataSourceImpl(
    private val client: HttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : MessagingRemoteDataSource {
    override suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit> = runCatching {
        logD("MessagingRemote(Network)", "send() called for recipientToken: ${recipientToken.take(10)}..., payloadType: ${payload.type}")
        val accessToken = FcmTokenProvider.getAccessToken()
        if (!accessToken.isNullOrBlank()) {
            logD("MessagingRemote(Network)", "Attaching FCM OAuth2 Access Token (${accessToken.take(15)}...)")
        } else {
            logD("MessagingRemote(Network)", "No FCM OAuth2 Access Token available from FcmTokenProvider.")
        }
        client.post(
            "${NetworkConstants.fcmBaseUrl}projects/${NetworkConstants.fcmProjectId}/messages:send",
        ) {
            contentType(ContentType.Application.Json)
            if (!accessToken.isNullOrBlank()) {
                header("Authorization", "Bearer $accessToken")
            }
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

    override suspend fun postHandshakeRequest(recipientUid: String, payload: FcmPayloadDto): Result<Unit> = runCatching {
        var lastError: Throwable? = null
        for (baseUrl in NetworkConstants.rtdbFallbackUrls) {
            try {
                val rtdbUrl = "$baseUrl/requests/$recipientUid/${payload.senderId}.json"
                logD("MessagingRemote(Network)", "postHandshakeRequest() -> posting to RTDB: $rtdbUrl")
                val response = client.put(rtdbUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(json.encodeToString(FcmPayloadDto.serializer(), payload))
                }
                val responseText = response.bodyAsText()
                logD("MessagingRemote(Network)", "postHandshakeRequest() -> RTDB status: ${response.status}, text: $responseText")

                if (responseText.contains("\"correctUrl\"")) {
                    val correctUrl = runCatching { json.parseToJsonElement(responseText).jsonObject["correctUrl"]?.jsonPrimitive?.content }.getOrNull()
                    if (!correctUrl.isNullOrBlank()) {
                        val redirectUrl = "$correctUrl/requests/$recipientUid/${payload.senderId}.json"
                        logD("MessagingRemote(Network)", "postHandshakeRequest() -> posting to redirect RTDB: $redirectUrl")
                        client.put(redirectUrl) {
                            contentType(ContentType.Application.Json)
                            setBody(json.encodeToString(FcmPayloadDto.serializer(), payload))
                        }
                    }
                }
                if (response.status.value in 200..299) return@runCatching Unit
            } catch (e: Exception) {
                lastError = e
                logD("MessagingRemote(Network)", "postHandshakeRequest() -> $baseUrl failed (${e.message}), trying next URL")
            }
        }
        if (lastError != null) throw lastError
        Unit
    }.onFailure {
        logD("MessagingRemote(Network)", "postHandshakeRequest() -> failed: ${it.message}")
    }

    override suspend fun fetchHandshakeRequests(myUid: String): Result<List<FcmPayloadDto>> = runCatching {
        val primaryUrl = NetworkConstants.rtdbFallbackUrls.first()
        var rtdbUrl = "$primaryUrl/requests/$myUid.json"
        logD("SystemNotifier", "fetchHandshakeRequests() -> checking RTDB: $rtdbUrl")
        var responseText = client.get(rtdbUrl).bodyAsText()
        logD("MessagingRemote(Network)", "fetchHandshakeRequests() -> RTDB raw responseText: $responseText")

        if (responseText.contains("\"correctUrl\"")) {
            val correctUrl = runCatching { json.parseToJsonElement(responseText).jsonObject["correctUrl"]?.jsonPrimitive?.content }.getOrNull()
            if (!correctUrl.isNullOrBlank()) {
                rtdbUrl = "$correctUrl/requests/$myUid.json"
                logD("SystemNotifier", "fetchHandshakeRequests() -> redirecting to RTDB: $rtdbUrl")
                responseText = client.get(rtdbUrl).bodyAsText()
            }
        }

        if (responseText.isBlank() || responseText == "null" || responseText.contains("\"error\"")) {
            return@runCatching emptyList()
        }

        val element = json.parseToJsonElement(responseText) as? JsonObject ?: return@runCatching emptyList()

        val list = element.values.mapNotNull { child ->
            try {
                if (child is JsonObject) {
                    val type = child["type"]?.jsonPrimitive?.content ?: "CONTACT_REQUEST"
                    val senderId = child["senderId"]?.jsonPrimitive?.content ?: ""
                    val senderName = child["senderName"]?.jsonPrimitive?.content ?: "Friend"
                    val listId = child["listId"]?.jsonPrimitive?.contentOrNull
                    val timestamp = child["timestamp"]?.jsonPrimitive?.content ?: Clock.System.now().toString()
                    val data = child["data"]?.jsonPrimitive?.content ?: "{}"

                    if (senderId.isNotBlank()) {
                        FcmPayloadDto(type, senderId, senderName, listId, timestamp, data)
                    } else null
                } else null
            } catch (e: Exception) {
                null
            }
        }
        logD("SystemNotifier", "fetchHandshakeRequests() -> found ${list.size} requests for $myUid")
        list
    }.onFailure {
        logD("SystemNotifier", "fetchHandshakeRequests() -> error: ${it.message}")
    }

    override suspend fun deleteHandshakeRequest(myUid: String, senderUid: String): Result<Unit> = runCatching {
        if (myUid.isBlank() || senderUid.isBlank()) return@runCatching
        for (baseUrl in NetworkConstants.rtdbFallbackUrls) {
            try {
                val rtdbUrl = "$baseUrl/requests/$myUid/$senderUid.json"
                logD("MessagingRemote(Network)", "deleteHandshakeRequest() -> deleting RTDB node: $rtdbUrl")
                client.delete(rtdbUrl)
            } catch (e: Exception) {
                logD("MessagingRemote(Network)", "deleteHandshakeRequest() -> $baseUrl failed (${e.message})")
            }
        }
        Unit
    }.onFailure {
        logD("MessagingRemote(Network)", "deleteHandshakeRequest() -> failed: ${it.message}")
    }
}
