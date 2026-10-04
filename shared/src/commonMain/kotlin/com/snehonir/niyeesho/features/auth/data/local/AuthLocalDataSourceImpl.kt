package com.snehonir.niyeesho.features.auth.data.local

import com.russhwolf.settings.Settings
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
        val jsonStr = settings.getString(key = KEY_USER, defaultValue = "")
        if (jsonStr.isBlank()) return null
        return try {
            json.decodeFromString(User.serializer(), jsonStr)
        } catch (_: Exception) {
            null
        }
    }

    override fun observeUser(): Flow<User?> = userFlow.asStateFlow()
    override suspend fun getUser(): User? = userFlow.value

    override suspend fun saveUser(user: User) {
        userFlow.value = user
        settings.putString(key = KEY_USER, value = json.encodeToString(User.serializer(), user))
    }

    override suspend fun clearUser() {
        userFlow.value = null
        settings.remove(KEY_USER)
    }

    companion object {
        private const val KEY_USER = "auth_user"
    }
}
