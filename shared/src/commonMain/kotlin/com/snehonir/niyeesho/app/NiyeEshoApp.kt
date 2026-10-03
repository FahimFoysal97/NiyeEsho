package com.snehonir.niyeesho.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTheme
import com.snehonir.niyeesho.core.navigation.NavGraphDestinations
import com.snehonir.niyeesho.core.navigation.NavRoute
import com.snehonir.niyeesho.core.navigation.NiyeEshoNavGraph
import com.snehonir.niyeesho.features.auth.domain.usecase.IsSignedInUseCase
import com.snehonir.niyeesho.features.auth.presentation.profile.ProfileScreen
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInScreen
import com.snehonir.niyeesho.features.contacts.presentation.ContactsScreen
import com.snehonir.niyeesho.features.dashboard.presentation.DashboardScreen
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.ListDetailScreen
import com.snehonir.niyeesho.features.shopping_list.presentation.list.ShoppingListScreen
import org.koin.compose.koinInject

@Composable
fun NiyeEshoApp(
    modifier: Modifier = Modifier,
    onShareInvite: (String) -> Unit = {},
) {
    val navController = rememberNavController()
    val isSignedIn = koinInject<IsSignedInUseCase>()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = isSignedIn().getOrDefault(false).let { signedIn ->
            if (signedIn) NavRoute.ShoppingLists.route else NavRoute.SignIn.route
        }
    }

    NiyeEshoTheme {
        if (startDestination == null) {
            Box(
                modifier = modifier.fillMaxSize().safeDrawingPadding(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            NiyeEshoNavGraph(
                navController = navController,
                modifier = modifier.fillMaxSize().safeDrawingPadding(),
                startDestination = startDestination ?: NavRoute.SignIn.route,
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
                        DashboardScreen(
                            onNavigateToDetail = { id ->
                                navController.navigate(NavRoute.ListDetail(id).route)
                            },
                            onNavigateToProfile = {
                                navController.navigate(NavRoute.Profile.route)
                            },
                        )
                    },
                    listDetail = { id ->
                        ListDetailScreen(
                            listId = id,
                            onShareInvite = onShareInvite,
                        )
                    },
                    contacts = {
                        ContactsScreen(
                            onOpenContact = { },
                            onShareInvite = onShareInvite,
                            onBack = { navController.popBackStack() },
                        )
                    },
                    profile = {
                        ProfileScreen(
                            onNavigateToSignIn = {
                                navController.navigate(NavRoute.SignIn.route) {
                                    popUpTo(0)
                                }
                            },
                            onBack = { navController.popBackStack() },
                        )
                    },
                    inviteAccept = { },
                ),
            )
        }
    }
}