package com.snehonir.niyeesho.features.auth.domain.usecase

import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository

/** Checks whether a Firebase user is currently authenticated. */
class IsSignedInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Boolean> = repository.isSignedIn()
}
