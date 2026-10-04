package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.BuildKonfig
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.toDisplayDate
import com.snehonir.niyeesho.core.utils.toDisplayTime
import kotlinx.datetime.Clock

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onAction: (ProfileUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToLists: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var searchableEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(
                message = errorMsg,
                duration = SnackbarDuration.Short,
            )
            onAction(ProfileUiAction.ErrorDismissed)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AppBottomBar(
                currentTab = AppNavTab.PROFILE,
                onTabSelected = { tab ->
                    when (tab) {
                        AppNavTab.LISTS -> onNavigateToLists()
                        AppNavTab.CONTACTS -> onNavigateToContacts()
                        AppNavTab.PROFILE -> Unit
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NiyeEshoDarkBg,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(NiyeEshoDarkBg),
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = NiyeEshoGreenPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.lg),
                    contentPadding = PaddingValues(top = Spacing.md, bottom = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    item {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }

                    uiState.user?.let { user ->
                        // User Profile Hero Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.lg),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                                ) {
                                    // Avatar circle
                                    Box(modifier = Modifier.size(68.dp)) {
                                        AvatarImage(
                                            avatarUrl = user.avatarUrl,
                                            contentDescription = user.displayName,
                                            modifier = Modifier.size(68.dp),
                                        )
                                        /*Surface(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .align(Alignment.BottomEnd),
                                            shape = CircleShape,
                                            color = NiyeEshoDarkBg,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, NiyeEshoDarkSurface),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = NiyeEshoGreenPrimary,
                                                    modifier = Modifier.size(12.dp),
                                                )
                                            }
                                        }*/
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = user.displayName.ifBlank { "Sneho Nir" },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                        )
                                        Text(
                                            text = user.email.ifBlank { "sneho@gmail.com" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NiyeEshoGreenDarkMuted,
                                        )
                                    }

                                    Spacer(Modifier.height(Spacing.xs))

                                    val isDriveSyncedRecently = uiState.lastSyncedAt?.let { lastSynced ->
                                        val now = Clock.System.now()
                                        (now - lastSynced).inWholeHours < 24
                                    } ?: false

                                    // Dynamic 3 Stat Boxes Row (Real List & Contact counts)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                    ) {
                                        StatBox(uiState.listCount.toString(), "Lists", Modifier.weight(1f))
                                        StatBox(uiState.contactCount.toString(), "Contacts", Modifier.weight(1f))
                                        StatBox("✓", "Sync", Modifier.weight(1f), isSuccess = isDriveSyncedRecently)
                                    }
                                }
                            }
                        }

                        // GOOGLE DRIVE Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                                ) {
                                    Text(
                                        text = "GOOGLE DRIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = NiyeEshoGreenDarkMuted,
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Sync",
                                                tint = NiyeEshoGreenDarkMuted,
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Column {
                                                Text("Last synced", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                                val syncedText = uiState.lastSyncedAt?.let {
                                                    "${it.toDisplayTime()} (${it.toDisplayDate()})"
                                                } ?: "Just now"
                                                Text(syncedText, style = MaterialTheme.typography.bodySmall, color = NiyeEshoGreenDarkMuted)
                                            }
                                        }

                                        IconButton(
                                            onClick = { onAction(ProfileUiAction.ManualSyncClicked) },
                                            modifier = Modifier.size(32.dp),
                                            enabled = !uiState.isSyncing,
                                        ) {
                                            if (uiState.isSyncing) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = NiyeEshoGreenPrimary,
                                                    strokeWidth = 2.dp,
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Refresh",
                                                    tint = NiyeEshoGreenPrimary,
                                                    modifier = Modifier.size(24.dp),
                                                )
                                            }
                                        }
                                    }

                                    /*HorizontalDivider(color = NiyeEshoDarkBg, thickness = 1.dp)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "Folder",
                                                tint = NiyeEshoGreenPrimary,
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Column {
                                                Text("ShopSync folder", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                                Text("3 lists · 12KB", style = MaterialTheme.typography.bodySmall, color = NiyeEshoGreenDarkMuted)
                                            }
                                        }
                                    }*/
                                }
                            }
                        }

                        // SETTINGS Card (Skipping notification and search by email switches)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                                ) {
                                    Text(
                                        text = "SETTINGS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = NiyeEshoGreenDarkMuted,
                                    )

                                    // Notifications
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = "Notifications",
                                                tint = NiyeEshoGreenMuted,
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Text("Notifications", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                        }
                                        Switch(
                                            checked = notificationsEnabled,
                                            onCheckedChange = { notificationsEnabled = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = NiyeEshoGreenPrimary,
                                                uncheckedThumbColor = Color.White,
                                                uncheckedTrackColor = NiyeEshoDarkBg,
                                            ),
                                        )
                                    }

                                    HorizontalDivider(color = NiyeEshoDarkBg, thickness = 1.dp)

                                    // Searchable
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Searchable",
                                                tint = NiyeEshoGreenMuted,
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Text("Searchable by email", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                        }
                                        Switch(
                                            checked = searchableEnabled,
                                            onCheckedChange = { searchableEnabled = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = NiyeEshoGreenPrimary,
                                                uncheckedThumbColor = Color.White,
                                                uncheckedTrackColor = NiyeEshoDarkBg,
                                            ),
                                        )
                                    }

                                    HorizontalDivider(color = NiyeEshoDarkBg, thickness = 1.dp)

                                    // Sign Out
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onAction(ProfileUiAction.SignOutClicked) }
                                            .padding(vertical = Spacing.sm),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = "Sign out",
                                            tint = Color(0xFFE05252),
                                            modifier = Modifier.size(20.dp),
                                        )
                                        Text(
                                            text = "Sign out",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE05252),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // App Branding Footer
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            Text(
                                text = "Niye Esho v${BuildKonfig.APP_VERSION}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGreenMuted,
                            )
                            Text(
                                text = "Bari ferar age, shob mone koriye dei.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NiyeEshoGreenDarkMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = NiyeEshoDarkBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) NiyeEshoGreenPrimary else Color.White,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = NiyeEshoGreenDarkMuted,
            )
        }
    }
}
