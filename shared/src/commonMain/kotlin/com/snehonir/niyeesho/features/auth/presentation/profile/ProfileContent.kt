package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.BrandPageHeader
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Spacing.md),
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        BrandPageHeader(
                            title = "Your profile",
                            subtitle = "Your Google account & sync settings.",
                        )
                        uiState.user?.let { user ->
                            AvatarImage(user.avatarUrl, user.displayName)
                            Text(user.displayName, style = MaterialTheme.typography.titleLarge)
                            Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        uiState.error?.let { ErrorView(UiText.Plain(it)) }
                        Spacer(Modifier.height(Spacing.xs))
                        AppButton(
                            text = UiText.Plain("Sign out"),
                            onClick = { onAction(ProfileUiAction.SignOutClicked) },
                            isLoading = uiState.isSigningOut,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
