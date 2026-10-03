package com.snehonir.niyeesho.features.auth.data.local

import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthLocalDataSourceImpl : AuthLocalDataSource {
    private val user = MutableStateFlow<User?>(null)

    override fun observeUser(): Flow<User?> = user.asStateFlow()
    override suspend fun getUser(): User? = user.value
    override suspend fun saveUser(user: User) {
        this.user.value = user
    }
    override suspend fun clearUser() {
        user.value = null
    }
}
