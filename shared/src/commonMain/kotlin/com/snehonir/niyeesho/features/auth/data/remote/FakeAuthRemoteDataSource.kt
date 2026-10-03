package com.snehonir.niyeesho.features.auth.data.remote

import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRemoteDataSource : AuthRemoteDataSource {
    private val user = MutableStateFlow<User?>(null)

    override fun observeAuthUser(): Flow<User?> = user.asStateFlow()

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        delay(150)
        val fakeUser = User.preview()
        user.value = fakeUser
        return Result.success(fakeUser)
    }

    override suspend fun signOut(): Result<Unit> {
        delay(100)
        user.value = null
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User?> {
        delay(50)
        return Result.success(user.value)
    }
}
