package com.snehonir.niyeesho.features.messaging.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface FcmMessageData {

    @Serializable
    @SerialName("shopping_list")
    data class ShoppingList(
        val action: ShoppingListAction = ShoppingListAction.ADD,
        val listId: String = "",
        val title: String = "",
    ) : FcmMessageData

    @Serializable
    @SerialName("shopping_list_item")
    data class ShoppingListItem(
        val action: ShoppingListItemAction = ShoppingListItemAction.ADD,
        val itemId: String = "",
        val listId: String = "",
        val name: String = "",
        val quantity: String? = null,
        val unit: String? = null,
        val isChecked: Boolean = false,
    ) : FcmMessageData

    @Serializable
    @SerialName("contact_handshake")
    data class ContactHandshake(
        val action: ContactHandshakeAction = ContactHandshakeAction.REQUEST,
        val email: String? = null,
        val avatarUrl: String? = null,
        val fcmToken: String? = null,
        val expiresAt: Long? = null,
    ) : FcmMessageData

    @Serializable
    @SerialName("token_update")
    data class TokenUpdate(
        val action: TokenUpdateAction = TokenUpdateAction.REFRESH,
        val fcmToken: String = "",
    ) : FcmMessageData

    @Serializable
    @SerialName("sync")
    data class Sync(
        val action: SyncAction = SyncAction.REQUEST,
        val syncKey: String = "",
    ) : FcmMessageData

    @Serializable
    @SerialName("generic")
    data class Generic(
        val text: String = "",
    ) : FcmMessageData
}
