package com.snehonir.niyeesho.features.auth.data.local

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class AuthLocalDataSourceImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : AuthLocalDataSource {
    private val userFlow = MutableStateFlow<User?>(loadUser())

    private fun loadUser(): User? {
        logD("AuthLocal(DB)", "loadUser() called")
        val jsonStr = settings.getString(key = KEY_USER, defaultValue = "")
        if (jsonStr.isBlank()) {
            logD("AuthLocal(DB)", "loadUser() -> no user found in local Settings")
            return null
        }
        return try {
            val user = json.decodeFromString(User.serializer(), jsonStr)
            logD("AuthLocal(DB)", "loadUser() -> success, user loaded: ${user.uid}")
            user
        } catch (e: Exception) {
            logD("AuthLocal(DB)", "loadUser() -> failed to decode user: ${e.message}")
            null
        }
    }

    override fun observeUser(): Flow<User?> = userFlow.asStateFlow().also {
        logD("AuthLocal(DB)", "observeUser() called")
    }

    override suspend fun getUser(): User? = userFlow.value.also {
        logD("AuthLocal(DB)", "getUser() -> current user: ${it?.uid}")
    }

    override suspend fun saveUser(user: User) {
        logD("AuthLocal(DB)", "saveUser() -> saving user: ${user.uid}")
        userFlow.value = user
        settings.putString(key = KEY_USER, value = json.encodeToString(User.serializer(), user))
        logD("AuthLocal(DB)", "saveUser() -> completed successfully")
    }

    override suspend fun clearUser() {
        logD("AuthLocal(DB)", "clearUser() -> clearing user from local settings")
        userFlow.value = null
        settings.remove(KEY_USER)
        logD("AuthLocal(DB)", "clearUser() -> completed successfully")
    }

    companion object {
        private const val KEY_USER = "auth_user"
    }
}
