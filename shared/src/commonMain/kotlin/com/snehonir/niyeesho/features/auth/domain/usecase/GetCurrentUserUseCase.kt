package com.snehonir.niyeesho.features.auth.domain.usecase

import com.snehonir.niyeesho.features.auth.domain.entity.User
import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository

/** Reads the authenticated user from the local-first repository. */
class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<User?> = repository.getCurrentUser()
}
