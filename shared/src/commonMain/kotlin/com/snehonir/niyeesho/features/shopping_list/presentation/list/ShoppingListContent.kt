package com.snehonir.niyeesho.features.shopping_list.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.BrandPageHeader
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateListDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.ShoppingListCard

@Composable
fun ShoppingListContent(
    uiState: ShoppingListUiState,
    onAction: (ShoppingListUiAction) -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(
            title = UiText.Plain("Niye Esho"),
            actions = {
                TextButton(onClick = onNavigateToContacts) { Text("Contacts") }
                TextButton(onClick = onNavigateToProfile) { Text("Profile") }
            },
        )
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                BrandPageHeader(
                    modifier = Modifier.padding(top = Spacing.sm),
                    title = "My shopping lists",
                    subtitle = "Keep every family request in one place.",
                )
                uiState.error?.let { ErrorView(UiText.Plain(it), modifier = Modifier.fillMaxWidth()) }
                if (uiState.lists.isEmpty()) {
                    Text(
                        text = "No lists yet. Create one for your next trip.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = Spacing.xxl * 3),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        items(uiState.lists, key = { it.id }) { list ->
                            ShoppingListCard(list, { onAction(ShoppingListUiAction.ListClicked(list.id)) })
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { onAction(ShoppingListUiAction.CreateClicked) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new list")
            }
        }
    }
    if (uiState.isCreateDialogVisible) {
        CreateListDialog(
            onConfirm = { title -> onAction(ShoppingListUiAction.CreateConfirmed(title, "user_demo")) },
            onDismiss = { onAction(ShoppingListUiAction.CreateDismissed) },
        )
    }
}
