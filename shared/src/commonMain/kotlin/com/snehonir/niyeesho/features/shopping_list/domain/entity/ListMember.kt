package com.snehonir.niyeesho.features.shopping_list.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class ListMember(
    val uid: String,
    val displayName: String,
    val avatarUrl: String?,
) {
    companion object {
        fun preview() = ListMember("user_demo", "Sneho Nir", null)
    }
}
