package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.core.network.NetworkConstants
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository
import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MessagingRemoteDataSourceImpl(
    private val client: HttpClient,
    private val authRepository: AuthRepository,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : MessagingRemoteDataSource {
    private suspend fun buildRtdbUrl(baseUrl: String, path: String): String {
        val idToken = runCatching { authRepository.getIdToken().getOrNull() }.getOrNull()
        val authParam = if (!idToken.isNullOrBlank()) "?auth=$idToken" else ""
        return "$baseUrl$path.json$authParam"
    }

    override suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit> = runCatching {
        logD("MessagingRemote(Network)", "send() -> Handshake handled via Realtime Database sync node.")
        Unit
    }.onFailure {
        logD("MessagingRemote(Network)", "send() -> failed: ${it.message}")
    }

    override suspend fun postHandshakeRequest(recipientUid: String, payload: FcmPayloadDto): Result<Unit> = runCatching {
        var lastError: Throwable? = null
        for (baseUrl in NetworkConstants.rtdbFallbackUrls) {
            try {
                val rtdbUrl = buildRtdbUrl(baseUrl, "/requests/$recipientUid/${payload.senderId}")
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
                        val redirectUrl = buildRtdbUrl(correctUrl, "/requests/$recipientUid/${payload.senderId}")
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
        var rtdbUrl = buildRtdbUrl(primaryUrl, "/requests/$myUid")
        logD("SystemNotifier", "fetchHandshakeRequests() -> checking RTDB: $rtdbUrl")
        var responseText = client.get(rtdbUrl).bodyAsText()
        logD("MessagingRemote(Network)", "fetchHandshakeRequests() -> RTDB raw responseText: $responseText")

        if (responseText.contains("\"correctUrl\"")) {
            val correctUrl = runCatching { json.parseToJsonElement(responseText).jsonObject["correctUrl"]?.jsonPrimitive?.content }.getOrNull()
            if (!correctUrl.isNullOrBlank()) {
                rtdbUrl = buildRtdbUrl(correctUrl, "/requests/$myUid")
                logD("SystemNotifier", "fetchHandshakeRequests() -> redirecting to RTDB: $rtdbUrl")
                responseText = client.get(rtdbUrl).bodyAsText()
            }
        }

        if (responseText.isBlank() || responseText == "null" || responseText.contains("\"error\"")) {
            return@runCatching emptyList()
        }

        val element = json.parseToJsonElement(responseText) as? JsonObject ?: return@runCatching emptyList()

        val list = element.values.mapNotNull { child ->
            if (child is JsonObject) parseHandshakeJsonObject(child) else null
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
                val rtdbUrl = buildRtdbUrl(baseUrl, "/requests/$myUid/$senderUid")
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

    override fun watchHandshakeRequests(myUid: String): Flow<List<FcmPayloadDto>> = channelFlow {
        if (myUid.isBlank()) return@channelFlow
        val primaryUrl = NetworkConstants.rtdbFallbackUrls.first()
        val rtdbUrl = buildRtdbUrl(primaryUrl, "/requests/$myUid")

        logD("SystemNotifier", "watchHandshakeRequests() -> opening Realtime SSE Stream: $rtdbUrl")

        try {
            client.prepareGet(rtdbUrl) {
                header("Accept", "text/event-stream")
            }.execute { response ->
                val channel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.startsWith("data:")) {
                        val jsonStr = line.removePrefix("data:").trim()
                        if (jsonStr.isNotBlank() && jsonStr != "null") {
                            logD("SystemNotifier", "RTDB SSE stream received event: $jsonStr")
                            val element = runCatching { json.parseToJsonElement(jsonStr) }.getOrNull()
                            if (element is JsonObject) {
                                val list = element.values.mapNotNull { child ->
                                    if (child is JsonObject) parseHandshakeJsonObject(child) else null
                                }
                                if (list.isNotEmpty()) {
                                    send(list)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logD("SystemNotifier", "RTDB SSE stream closed: ${e.message}")
        }
    }

    private fun parseHandshakeJsonObject(child: JsonObject): FcmPayloadDto? {
        return try {
            val type = child["type"]?.jsonPrimitive?.content ?: "CONTACT_REQUEST"
            val senderId = child["senderId"]?.jsonPrimitive?.content ?: ""
            val senderName = child["senderName"]?.jsonPrimitive?.content ?: "Friend"
            val listId = child["listId"]?.jsonPrimitive?.contentOrNull
            val timestamp = child["timestamp"]?.jsonPrimitive?.content ?: Clock.System.now().toString()
            val data = child["data"]?.jsonPrimitive?.content ?: "{}"

            if (senderId.isNotBlank()) {
                FcmPayloadDto(type, senderId, senderName, listId, timestamp, data)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
