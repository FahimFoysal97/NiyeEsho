package com.snehonir.niyeesho.features.auth.data.remote

import com.snehonir.niyeesho.features.auth.domain.entity.User
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class AuthRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
) : AuthRemoteDataSource {
    override fun observeAuthUser(): Flow<User?> =
        firebaseAuth.authStateChanged.map { firebaseUser ->
            firebaseUser?.toDomain()
        }

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        firebaseAuth.signInWithCredential(
            GoogleAuthProvider.credential(idToken, null),
        ).user?.toDomain() ?: error("Firebase returned no authenticated user.")
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): Result<User?> = runCatching {
        firebaseAuth.currentUser?.toDomain()
    }

    private fun dev.gitlive.firebase.auth.FirebaseUser.toDomain(): User {
        val now = Clock.System.now()
        return User(
            uid = uid,
            displayName = displayName.orEmpty(),
            email = email.orEmpty(),
            avatarUrl = photoURL,
            createdAt = now,
            updatedAt = now,
        )
    }
}
