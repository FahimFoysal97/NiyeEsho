package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: UiText,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = { Text(title.resolve()) },
        modifier = modifier,
        navigationIcon = {
            onBackClick?.let {
                IconButton(onClick = it) {
                    Text("<")
                }
            }
        },
        actions = { actions() },
    )
}
