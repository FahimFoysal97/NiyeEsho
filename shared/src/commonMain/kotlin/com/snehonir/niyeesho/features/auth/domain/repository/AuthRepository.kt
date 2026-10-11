package com.snehonir.niyeesho.features.auth.domain.repository

import com.snehonir.niyeesho.features.auth.domain.entity.AuthState
import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow

/** Provides authentication state and Google-backed account operations. */
interface AuthRepository {
    fun observeAuthState(): Flow<Result<AuthState>>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun isSignedIn(): Result<Boolean>
    suspend fun getIdToken(): Result<String?>
}
