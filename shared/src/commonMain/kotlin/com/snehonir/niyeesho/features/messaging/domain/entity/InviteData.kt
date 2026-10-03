package com.snehonir.niyeesho.features.messaging.domain.entity

data class InviteData(
    val uid: String,
    val name: String,
    val token: String,
    val driveUrl: String,
    val avatar: String? = null,
    val listId: String? = null,
    val listTitle: String? = null,
) {
    companion object {
        fun preview() = InviteData(
            uid = "user_demo",
            name = "Sneho Nir",
            token = "demo_fcm_token",
            driveUrl = "https://drive.google.com/uc?id=profile_demo&export=download",
        )
    }
}
