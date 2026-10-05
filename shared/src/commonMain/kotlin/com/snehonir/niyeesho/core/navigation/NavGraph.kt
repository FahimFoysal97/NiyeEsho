package com.snehonir.niyeesho.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

data class NavGraphDestinations(
    val signIn: @Composable () -> Unit,
    val shoppingLists: @Composable () -> Unit,
    val listDetail: @Composable (listId: String) -> Unit,
    val contacts: @Composable () -> Unit,
    val invite: @Composable () -> Unit,
    val profile: @Composable () -> Unit,
    val inviteAccept: @Composable (InviteDeepLink?) -> Unit,
)

@Composable
fun NiyeEshoNavGraph(
    destinations: NavGraphDestinations,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoute.SignIn.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { NavAnimations.enter },
        exitTransition = { NavAnimations.exit },
        popEnterTransition = { NavAnimations.popEnter },
        popExitTransition = { NavAnimations.popExit },
    ) {
        composable(NavRoute.SignIn.route) {
            destinations.signIn()
        }
        composable(NavRoute.ShoppingLists.route) {
            destinations.shoppingLists()
        }
        composable(
            route = NavRoute.ListDetailPath,
            arguments = listOf(navArgument("listId") { type = NavType.StringType }),
        ) { entry ->
            destinations.listDetail(entry.arguments?.getString("listId").orEmpty())
        }
        composable(NavRoute.Contacts.route) {
            destinations.contacts()
        }
        composable(NavRoute.Invite.route) {
            destinations.invite()
        }
        composable(NavRoute.Profile.route) {
            destinations.profile()
        }
        composable(
            route = NavRoute.InviteAccept.route,
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType; defaultValue = "" },
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
                navArgument("email") { type = NavType.StringType; nullable = true },
                navArgument("token") { type = NavType.StringType; defaultValue = "" },
                navArgument("driveUrl") { type = NavType.StringType; defaultValue = "" },
                navArgument("avatar") { type = NavType.StringType; nullable = true },
                navArgument("listId") { type = NavType.StringType; nullable = true },
                navArgument("listTitle") { type = NavType.StringType; nullable = true },
            ),
        ) { entry ->
            val arguments = entry.arguments
            val uid = arguments?.getString("uid").orEmpty()
            val name = arguments?.getString("name").orEmpty()
            val email = arguments?.getString("email")
            val token = arguments?.getString("token").orEmpty()
            val driveUrl = arguments?.getString("driveUrl").orEmpty()
            val invite = if (uid.isBlank() || name.isBlank() || token.isBlank() || driveUrl.isBlank()) {
                null
            } else {
                InviteDeepLink(
                    uid = uid,
                    name = name,
                    email = email,
                    token = token,
                    driveUrl = driveUrl,
                    avatar = arguments?.getString("avatar"),
                    listId = arguments?.getString("listId"),
                    listTitle = arguments?.getString("listTitle"),
                )
            }
            destinations.inviteAccept(invite)
        }
    }
}
