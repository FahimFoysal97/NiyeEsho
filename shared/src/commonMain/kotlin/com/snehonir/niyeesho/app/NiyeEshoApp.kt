package com.snehonir.niyeesho.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTheme
import com.snehonir.niyeesho.core.navigation.NavGraphDestinations
import com.snehonir.niyeesho.core.navigation.NavRoute
import com.snehonir.niyeesho.core.navigation.NiyeEshoNavGraph
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.auth.domain.usecase.IsSignedInUseCase
import com.snehonir.niyeesho.features.auth.presentation.profile.ProfileScreen
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInScreen
import com.snehonir.niyeesho.features.contacts.presentation.ContactsScreen
import com.snehonir.niyeesho.features.invite.presentation.InviteScreen
import com.snehonir.niyeesho.features.dashboard.presentation.DashboardScreen
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.ListDetailScreen
import org.koin.compose.koinInject

import com.snehonir.niyeesho.features.splash.presentation.SplashScreen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NiyeEshoApp(
    modifier: Modifier = Modifier,
    onShareInvite: (String) -> Unit = {},
) {
    logD("NiyeEshoApp", "NiyeEshoApp composable starting")
    val navController = rememberNavController()
    val isSignedIn = koinInject<IsSignedInUseCase>()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        logD("NiyeEshoApp", "LaunchedEffect checking auth status via IsSignedInUseCase")
        val signedIn = isSignedIn().getOrDefault(false)
        logD("NiyeEshoApp", "Auth check result signedIn = $signedIn")
        // Allow user to see the animated splash screen
        delay(3000L.milliseconds)
        startDestination = if (signedIn) NavRoute.ShoppingLists.route else NavRoute.SignIn.route
        logD("NiyeEshoApp", "Determined startDestination = $startDestination")
    }

    NiyeEshoTheme {
        if (startDestination == null) {
            logD("NiyeEshoApp", "Showing animated SplashScreen while startDestination is loading")
            SplashScreen(modifier = modifier.fillMaxSize())
        } else {
            logD("NiyeEshoApp", "Navigating to NiyeEshoNavGraph with startDestination = $startDestination")
            NiyeEshoNavGraph(
                navController = navController,
                modifier = modifier.fillMaxSize().safeDrawingPadding(),
                startDestination = startDestination ?: NavRoute.SignIn.route,
                destinations = NavGraphDestinations(
                    signIn = {
                        SignInScreen(
                            onNavigateToShoppingLists = {
                                logD("NiyeEshoApp", "Navigation: SignIn -> ShoppingLists")
                                navController.navigate(NavRoute.ShoppingLists.route) {
                                    popUpTo(NavRoute.SignIn.route) { inclusive = true }
                                }
                            },
                        )
                    },
                    shoppingLists = {
                        DashboardScreen(
                            onNavigateToDetail = { id ->
                                logD("NiyeEshoApp", "Navigation: Dashboard -> ListDetail for id = $id")
                                navController.navigate(NavRoute.ListDetail(id).route)
                            },
                            onNavigateToContacts = {
                                logD("NiyeEshoApp", "Navigation: Dashboard -> Contacts")
                                navController.navigate(NavRoute.Contacts.route)
                            },
                            onNavigateToProfile = {
                                logD("NiyeEshoApp", "Navigation: Dashboard -> Profile")
                                navController.navigate(NavRoute.Profile.route)
                            },
                            onNavigateToInvite = {
                                logD("NiyeEshoApp", "Navigation: Dashboard -> Invite")
                                navController.navigate(NavRoute.Invite.route)
                            },
                            onShareInvite = onShareInvite,
                        )
                    },
                    listDetail = { id ->
                        ListDetailScreen(
                            listId = id,
                            onBack = {
                                logD("NiyeEshoApp", "Navigation: ListDetail -> Back")
                                navController.popBackStack()
                            },
                            onShareInvite = onShareInvite,
                            onNavigateToDetail = { listId ->
                                logD("NiyeEshoApp", "Navigation: ListDetail -> ListDetail for listId = $listId")
                                navController.navigate(NavRoute.ListDetail(listId).route)
                            },
                        )
                    },
                    contacts = {
                        ContactsScreen(
                            onOpenContact = { listId ->
                                logD("NiyeEshoApp", "Navigation: Contacts -> ListDetail for listId = $listId")
                                navController.navigate(NavRoute.ListDetail(listId).route)
                            },
                            onShareInvite = onShareInvite,
                            onBack = {
                                logD("NiyeEshoApp", "Navigation: Contacts -> Back")
                                navController.popBackStack()
                            },
                            onNavigateToLists = {
                                logD("NiyeEshoApp", "Navigation: Contacts -> ShoppingLists")
                                navController.navigate(NavRoute.ShoppingLists.route)
                            },
                            onNavigateToProfile = {
                                logD("NiyeEshoApp", "Navigation: Contacts -> Profile")
                                navController.navigate(NavRoute.Profile.route)
                            },
                            onNavigateToInvite = {
                                logD("NiyeEshoApp", "Navigation: Contacts -> Invite")
                                navController.navigate(NavRoute.Invite.route)
                            },
                        )
                    },
                    invite = {
                        InviteScreen(
                            onShareInvite = onShareInvite,
                            onNavigateToLists = {
                                navController.navigate(NavRoute.ShoppingLists.route) {
                                    popUpTo(NavRoute.ShoppingLists.route) { inclusive = true }
                                }
                            },
                            onNavigateToContacts = {
                                navController.navigate(NavRoute.Contacts.route) {
                                    popUpTo(NavRoute.Contacts.route) { inclusive = true }
                                }
                            },
                            onNavigateToProfile = {
                                navController.navigate(NavRoute.Profile.route) {
                                    popUpTo(NavRoute.Profile.route) { inclusive = true }
                                }
                            },
                        )
                    },
                    profile = {
                        ProfileScreen(
                            onNavigateToSignIn = {
                                logD("NiyeEshoApp", "Navigation: Profile -> SignIn (Signed out)")
                                navController.navigate(NavRoute.SignIn.route) {
                                    popUpTo(0)
                                }
                            },
                            onBack = {
                                logD("NiyeEshoApp", "Navigation: Profile -> Back")
                                navController.popBackStack()
                            },
                            onNavigateToLists = {
                                logD("NiyeEshoApp", "Navigation: Profile -> ShoppingLists")
                                navController.navigate(NavRoute.ShoppingLists.route)
                            },
                            onNavigateToContacts = {
                                logD("NiyeEshoApp", "Navigation: Profile -> Contacts")
                                navController.navigate(NavRoute.Contacts.route)
                            },
                            onNavigateToInvite = {
                                logD("NiyeEshoApp", "Navigation: Profile -> Invite")
                                navController.navigate(NavRoute.Invite.route)
                            },
                        )
                    },
                    inviteAccept = { },
                ),
            )
        }
    }
    logD("NiyeEshoApp", "NiyeEshoApp composable completed render")
}
