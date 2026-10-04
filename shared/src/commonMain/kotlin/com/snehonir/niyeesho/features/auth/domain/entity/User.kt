package com.snehonir.niyeesho.features.auth.domain.entity

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun preview() = User(
            uid = "user_demo",
            displayName = "Sneho Nir",
            email = "sneho@example.com",
            avatarUrl = null,
            createdAt = Instant.parse("2026-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2026-06-15T10:00:00Z"),
        )
    }
}
