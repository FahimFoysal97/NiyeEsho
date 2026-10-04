package com.snehonir.niyeesho.core.utils

import com.russhwolf.settings.Settings

interface TokenManager {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}

class TokenManagerImpl(
    private val settings: Settings = Settings(),
) : TokenManager {
    override fun getToken(): String? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        logD("TokenManager", "getToken() -> retrieved token present: ${!token.isNullOrBlank()}")
        return token
    }

    override fun saveToken(token: String) {
        logD("TokenManager", "saveToken() -> saving access token")
        settings.putString(KEY_TOKEN, token)
    }

    override fun clearToken() {
        logD("TokenManager", "clearToken() -> clearing access token")
        settings.remove(KEY_TOKEN)
    }

    companion object {
        private const val KEY_TOKEN = "auth_access_token"
    }
}
