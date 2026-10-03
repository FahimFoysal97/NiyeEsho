package com.snehonir.niyeesho.features.auth.data.remote

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRemoteDataSource : AuthRemoteDataSource {
    private val settings = Settings()
    private val user = MutableStateFlow(readPersistedUser())

    override fun observeAuthUser(): Flow<User?> = user.asStateFlow()

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        delay(150)
        val fakeUser = User.preview()
        user.value = fakeUser
        persist(fakeUser)
        return Result.success(fakeUser)
    }

    override suspend fun signOut(): Result<Unit> {
        delay(100)
        user.value = null
        settings.remove(PersistedKeys.SIGNED_IN)
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User?> {
        delay(50)
        return Result.success(user.value)
    }

    private fun persist(user: User) {
        settings.putBoolean(PersistedKeys.SIGNED_IN, true)
        settings.putString(PersistedKeys.UID, user.uid)
        settings.putString(PersistedKeys.NAME, user.displayName)
        settings.putString(PersistedKeys.EMAIL, user.email)
        settings.putString(PersistedKeys.CREATED_AT, user.createdAt.toString())
        settings.putString(PersistedKeys.UPDATED_AT, user.updatedAt.toString())
        user.avatarUrl?.let { settings.putString(PersistedKeys.AVATAR, it) }
    }

    private fun readPersistedUser(): User? {
        if (!settings.getBoolean(PersistedKeys.SIGNED_IN, false)) return null
        val uid = settings.getStringOrNull(PersistedKeys.UID) ?: return null
        val name = settings.getStringOrNull(PersistedKeys.NAME) ?: return null
        val email = settings.getStringOrNull(PersistedKeys.EMAIL) ?: return null
        val createdAt = settings.getStringOrNull(PersistedKeys.CREATED_AT) ?: return null
        val updatedAt = settings.getStringOrNull(PersistedKeys.UPDATED_AT) ?: return null
        return runCatching {
            User(
                uid = uid,
                displayName = name,
                email = email,
                avatarUrl = settings.getStringOrNull(PersistedKeys.AVATAR),
                createdAt = kotlinx.datetime.Instant.parse(createdAt),
                updatedAt = kotlinx.datetime.Instant.parse(updatedAt),
            )
        }.getOrNull()
    }

    private object PersistedKeys {
        const val SIGNED_IN = "auth.signed_in"
        const val UID = "auth.uid"
        const val NAME = "auth.name"
        const val EMAIL = "auth.email"
        const val AVATAR = "auth.avatar"
        const val CREATED_AT = "auth.created_at"
        const val UPDATED_AT = "auth.updated_at"
    }
}