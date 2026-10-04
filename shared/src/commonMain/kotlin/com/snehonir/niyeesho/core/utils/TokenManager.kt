package com.snehonir.niyeesho.core.utils

import com.russhwolf.settings.Settings

interface TokenManager {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()

    fun getFcmToken(): String?
    fun saveFcmToken(token: String)
}

class TokenManagerImpl(
    private val settings: Settings = Settings(),
) : TokenManager {
    override fun getToken(): String? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        logD("TokenManagerImpl", "getToken() -> retrieved token present: ${!token.isNullOrBlank()}")
        return token
    }

    override fun saveToken(token: String) {
        logD("TokenManagerImpl", "saveToken() -> saving access token")
        settings.putString(KEY_TOKEN, token)
    }

    override fun clearToken() {
        logD("TokenManagerImpl", "clearToken() -> clearing access token")
        settings.remove(KEY_TOKEN)
    }

    override fun getFcmToken(): String? {
        val token = settings.getStringOrNull(KEY_FCM_TOKEN)
        logD("TokenManagerImpl", "getFcmToken() -> retrieved FCM token present: ${!token.isNullOrBlank()}")
        return token
    }

    override fun saveFcmToken(token: String) {
        logD("TokenManagerImpl", "saveFcmToken() -> saving FCM token")
        settings.putString(KEY_FCM_TOKEN, token)
    }

    companion object {
        private const val KEY_TOKEN = "auth_access_token"
        private const val KEY_FCM_TOKEN = "fcm_device_token"
    }
}
