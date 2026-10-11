package com.snehonir.niyeesho.core.network

expect object FcmTokenProvider {
    suspend fun getAccessToken(): String?
}
