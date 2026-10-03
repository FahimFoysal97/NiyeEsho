package com.snehonir.niyeesho.features.auth.domain.usecase

import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository

/** Signs the current user out of Firebase and the local cache. */
class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.signOut()
}
