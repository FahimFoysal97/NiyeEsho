package com.snehonir.niyeesho.features.auth.data.repository

import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.features.auth.data.local.AuthLocalDataSource
import com.snehonir.niyeesho.features.auth.data.mapper.toAuthState
import com.snehonir.niyeesho.features.auth.data.remote.AuthRemoteDataSource
import com.snehonir.niyeesho.features.auth.domain.entity.AuthState
import com.snehonir.niyeesho.features.auth.domain.entity.User
import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class AuthRepositoryImpl(
    private val local: AuthLocalDataSource,
    private val remote: AuthRemoteDataSource,
    private val driveRepository: DriveRepository,
    private val tokenManager: TokenManager,
) : AuthRepository {
    override fun observeAuthState(): Flow<Result<AuthState>> =
        local.observeUser()
            .map { Result.success(it.toAuthState()) }
            .onStart {
                remote.getCurrentUser().onSuccess { user ->
                    if (user == null) local.clearUser() else local.saveUser(user)
                }
            }
            .catch { emit(Result.failure(it)) }

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        remote.signInWithGoogle(idToken).onSuccess { user ->
            local.saveUser(user)
            tokenManager.saveToken(idToken)
            driveRepository.syncFromDrive(idToken)
        }

    override suspend fun signOut(): Result<Unit> =
        remote.signOut().onSuccess {
            local.clearUser()
            tokenManager.clearToken()
        }

    override suspend fun getCurrentUser(): Result<User?> =
        local.getUser()?.let { Result.success(it) }
            ?: remote.getCurrentUser().onSuccess { user ->
                if (user != null) local.saveUser(user)
            }

    override suspend fun isSignedIn(): Result<Boolean> =
        getCurrentUser().map { it != null }
}
