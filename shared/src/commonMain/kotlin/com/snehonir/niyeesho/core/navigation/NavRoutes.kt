package com.snehonir.niyeesho.core.navigation

sealed interface NavRoute {
    val route: String

    data object SignIn : NavRoute {
        override val route: String = "sign_in"
    }

    data object ShoppingLists : NavRoute {
        override val route: String = "shopping_lists"
    }

    data class ListDetail(val listId: String) : NavRoute {
        override val route: String = "list_detail/$listId"
    }

    data object Contacts : NavRoute {
        override val route: String = "contacts"
    }

    data object Invite : NavRoute {
        override val route: String = "invite"
    }

    data object Profile : NavRoute {
        override val route: String = "profile"
    }

    data object InviteAccept : NavRoute {
        const val path = "invite_accept"
        override val route: String = "$path?uid={uid}&name={name}&token={token}" +
            "&driveUrl={driveUrl}&avatar={avatar}&listId={listId}&listTitle={listTitle}"
    }

    companion object {
        const val ListDetailPath = "list_detail/{listId}"
    }
}
