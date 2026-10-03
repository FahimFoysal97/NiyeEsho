package com.snehonir.niyeesho.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual class KtorClientFactory actual constructor() {
    actual fun create(): HttpClient = HttpClient(Darwin)
}
