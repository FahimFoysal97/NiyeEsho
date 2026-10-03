package com.snehonir.niyeesho.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTheme
import com.snehonir.niyeesho.core.navigation.NavGraphDestinations
import com.snehonir.niyeesho.core.navigation.NavRoute
import com.snehonir.niyeesho.core.navigation.NiyeEshoNavGraph
import com.snehonir.niyeesho.features.auth.presentation.profile.ProfileScreen
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInScreen
import com.snehonir.niyeesho.features.contacts.presentation.ContactsScreen
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.ListDetailScreen
import com.snehonir.niyeesho.features.shopping_list.presentation.list.ShoppingListScreen

@Composable
fun NiyeEshoApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NiyeEshoTheme {
        NiyeEshoNavGraph(
            navController = navController,
            modifier = modifier,
            startDestination = NavRoute.SignIn.route,
            destinations = NavGraphDestinations(
                signIn = {
                    SignInScreen(
                        onNavigateToShoppingLists = {
                            navController.navigate(NavRoute.ShoppingLists.route) {
                                popUpTo(NavRoute.SignIn.route) { inclusive = true }
                            }
                        },
                    )
                },
                shoppingLists = {
                    ShoppingListScreen(
                        onNavigateToDetail = { id ->
                            navController.navigate(NavRoute.ListDetail(id).route)
                        },
                    )
                },
                listDetail = { id ->
                    ListDetailScreen(
                        listId = id,
                        onInviteMember = { },
                    )
                },
                contacts = {
                    ContactsScreen(
                        onOpenContact = { },
                        onShareInvite = { },
                    )
                },
                profile = {
                    ProfileScreen(
                        onNavigateToSignIn = {
                            navController.navigate(NavRoute.SignIn.route) {
                                popUpTo(0)
                            }
                        },
                    )
                },
                inviteAccept = { },
            ),
        )
    }
}
