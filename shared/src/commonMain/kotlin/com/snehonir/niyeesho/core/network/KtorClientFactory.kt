package com.snehonir.niyeesho.core.network

import io.ktor.client.HttpClient

expect class KtorClientFactory() {
    fun create(): HttpClient
}
