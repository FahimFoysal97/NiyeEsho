package com.snehonir.niyeesho.testing

import com.snehonir.niyeesho.features.auth.domain.entity.AuthState
import com.snehonir.niyeesho.features.auth.domain.entity.User
import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAuthRepository : AuthRepository {
    private val user = MutableStateFlow<User?>(null)

    override fun observeAuthState(): Flow<Result<AuthState>> =
        user.map { Result.success(it?.let(AuthState::SignedIn) ?: AuthState.SignedOut) }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val signedIn = User.preview()
        user.value = signedIn
        return Result.success(signedIn)
    }

    override suspend fun signOut(): Result<Unit> {
        user.value = null
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User?> = Result.success(user.value)
    override suspend fun isSignedIn(): Result<Boolean> = Result.success(user.value != null)
}
