package com.snehonir.niyeesho.features.auth.domain.usecase

import com.snehonir.niyeesho.features.auth.domain.entity.User
import com.snehonir.niyeesho.features.auth.domain.repository.AuthRepository

/** Signs the current user in using a Google ID token. */
class SignInWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<User> =
        if (idToken.isBlank()) {
            Result.failure(IllegalArgumentException("Google ID token cannot be blank."))
        } else {
            repository.signInWithGoogle(idToken)
        }
}
