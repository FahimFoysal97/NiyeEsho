package com.snehonir.niyeesho.features.auth.data.local

import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    fun observeUser(): Flow<User?>
    suspend fun getUser(): User?
    suspend fun saveUser(user: User)
    suspend fun clearUser()
}
