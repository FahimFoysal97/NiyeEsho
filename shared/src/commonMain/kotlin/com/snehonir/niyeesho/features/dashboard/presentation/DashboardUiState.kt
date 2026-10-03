package com.snehonir.niyeesho.features.dashboard.presentation

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DashboardUiState(
    val lists: ImmutableList<DashboardUiModel> = persistentListOf(),
    val availableContacts: ImmutableList<Contact> = persistentListOf(),
    val isLoading: Boolean = true,
    val isCreateDialogVisible: Boolean = false,
    val isFabMenuVisible: Boolean = false,
    val isInviteDialogVisible: Boolean = false,
    val inviteLink: String? = null,
    val error: String? = null,
    val userName: String = "Sneho Nir",
    val userEmail: String = "sneho@example.com",
)
