package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onAction: (ProfileUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            uiState.user?.let { user ->
                AvatarImage(user.avatarUrl, user.displayName)
                Text(user.displayName)
                Text(user.email)
            }
            uiState.error?.let { ErrorView(UiText.Plain(it)) }
            AppButton(
                text = UiText.Resource("niyeesho_sign_out"),
                onClick = { onAction(ProfileUiAction.SignOutClicked) },
                isLoading = uiState.isSigningOut,
            )
        }
    }
}
