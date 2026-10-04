package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.BuildKonfig
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.core.utils.toDisplayDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onAction: (ProfileUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(
            title = UiText.Plain("Account Profile"),
            onBackClick = onBack,
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg),
                contentPadding = PaddingValues(vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                uiState.user?.let { user ->
                    // Profile Hero Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.xl),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                            ) {
                                AvatarImage(
                                    avatarUrl = user.avatarUrl,
                                    contentDescription = user.displayName,
                                    size = 80.dp,
                                )

                                Text(
                                    text = user.displayName.ifBlank { "Google User" },
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(top = Spacing.xs),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(Spacing.md),
                                        )
                                        Text(
                                            text = user.email.ifBlank { "account@gmail.com" },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }

                                Text(
                                    text = "Signed in via Gmail",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                )
                            }
                        }
                    }

                    // Manual Sync & Cloud Status Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.lg),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f).padding(end = Spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                                ) {
                                    Text(
                                        text = "Google Drive Sync",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    val syncedText = uiState.lastSyncedAt?.let {
                                        val local = it.toLocalDateTime(TimeZone.currentSystemDefault())
                                        "Last synced: ${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')} (${it.toDisplayDate()})"
                                    } ?: "Not synced yet"
                                    Text(
                                        text = syncedText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Button(
                                    onClick = { onAction(ProfileUiAction.ManualSyncClicked) },
                                    enabled = !uiState.isSyncing && !uiState.isSyncedSuccessfully,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.isSyncedSuccessfully) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.primary,
                                        contentColor = if (uiState.isSyncedSuccessfully) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onPrimary,
                                    ),
                                ) {
                                    if (uiState.isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp,
                                        )
                                    } else if (uiState.isSyncedSuccessfully) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Synced",
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                            Text("Done", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    } else {
                                        Text("Sync Now")
                                    }
                                }
                            }
                        }
                    }

                    // User Details Information Card (Gmail / Auth actual data)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.lg),
                                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                            ) {
                                Text(
                                    text = "Google Account Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )

                                ProfileDetailRow(
                                    icon = Icons.Default.Person,
                                    label = "Display Name",
                                    value = user.displayName.ifBlank { "Not provided" },
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                ProfileDetailRow(
                                    icon = Icons.Default.Email,
                                    label = "Gmail Address",
                                    value = user.email.ifBlank { "Not provided" },
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                ProfileDetailRow(
                                    icon = Icons.Default.AccountCircle,
                                    label = "Firebase UID",
                                    value = user.uid,
                                )
                            }
                        }
                    }
                }

                uiState.error?.let { error ->
                    item { ErrorView(UiText.Plain(error), modifier = Modifier.fillMaxWidth()) }
                }

                // Sign Out Button
                item {
                    Spacer(Modifier.height(Spacing.xs))
                    Button(
                        onClick = { onAction(ProfileUiAction.SignOutClicked) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                        enabled = !uiState.isSigningOut,
                    ) {
                        if (uiState.isSigningOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Spacing.lg),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Sign Out", fontWeight = FontWeight.SemiBold)
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Bari ferar age, shob mone koriye dei.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(Spacing.xxl),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Spacing.lg),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
