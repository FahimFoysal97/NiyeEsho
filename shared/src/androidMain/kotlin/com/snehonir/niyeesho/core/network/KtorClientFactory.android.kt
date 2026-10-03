package com.snehonir.niyeesho.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual class KtorClientFactory actual constructor() {
    actual fun create(): HttpClient = HttpClient(OkHttp)
}
