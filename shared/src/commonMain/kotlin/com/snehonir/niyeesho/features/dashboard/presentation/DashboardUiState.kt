package com.snehonir.niyeesho.features.dashboard.presentation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DashboardUiState(
    val lists: ImmutableList<DashboardUiModel> = persistentListOf(),
    val isLoading: Boolean = true,
    val isCreateDialogVisible: Boolean = false,
    val error: String? = null,
    val userName: String = "Sneho Nir",
    val userEmail: String = "sneho@example.com",
)
