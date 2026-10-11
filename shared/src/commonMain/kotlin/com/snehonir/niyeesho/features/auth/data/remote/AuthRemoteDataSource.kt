package com.snehonir.niyeesho.features.auth.data.remote

import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    fun observeAuthUser(): Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun getIdToken(): Result<String?>
}
