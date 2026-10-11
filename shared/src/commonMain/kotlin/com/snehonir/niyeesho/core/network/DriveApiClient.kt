package com.snehonir.niyeesho.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DriveApiClient(
    private val client: HttpClient,
    private val baseUrl: String = NetworkConstants.driveBaseUrl,
) {
    suspend fun getFileContent(fileId: String, accessToken: String): Result<String> = runCatching {
        client.get(ApiRoutes.file(baseUrl, fileId)) {
            parameter(ApiRoutes.Params.ALT, ApiRoutes.Params.ALT_MEDIA)
            bearerAuth(accessToken)
        }.bodyAsText()
    }

    suspend fun createFile(
        accessToken: String,
        metadataJson: String,
        contentJson: String,
    ): Result<String> = runCatching {
        client.post(ApiRoutes.files(baseUrl)) {
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(contentJson)
            header(ApiRoutes.Headers.METADATA, metadataJson)
        }.bodyAsText()
    }

    suspend fun updateFile(
        fileId: String,
        accessToken: String,
        contentJson: String,
    ): Result<String> = runCatching {
        client.patch(ApiRoutes.file(baseUrl, fileId)) {
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(contentJson)
        }.bodyAsText()
    }

    suspend fun deleteFile(fileId: String, accessToken: String): Result<Unit> = runCatching {
        client.delete(ApiRoutes.file(baseUrl, fileId)) {
            bearerAuth(accessToken)
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.bearerAuth(token: String) {
        header(ApiRoutes.Headers.AUTHORIZATION, "${ApiRoutes.Headers.BEARER_PREFIX}$token")
    }
}
