package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.auth.domain.usecase.SignOutUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListItem
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListMember
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.drive.domain.usecase.SyncFromDriveUseCase
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.WatchShoppingListsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

private const val PREF_LAST_SYNCED_AT = "last_synced_at"

class ProfileViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val syncFromDrive: SyncFromDriveUseCase,
    private val watchLists: WatchShoppingListsUseCase,
    private val watchContacts: WatchContactsUseCase,
    private val shoppingListLocal: ShoppingListLocalDataSource,
    private val listItemLocal: ListItemLocalDataSource,
    private val driveRepository: DriveRepository,
    private val tokenManager: TokenManager,
    private val settings: Settings,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ProfileUiEvent> = _uiEvent.receiveAsFlow()

    init {
        onAction(ProfileUiAction.Load)
        viewModelScope.launch {
            watchLists().collect { result ->
                result.onSuccess { lists ->
                    _uiState.update { it.copy(listCount = lists.size) }
                }
            }
        }
        viewModelScope.launch {
            watchContacts().collect { result ->
                result.onSuccess { contacts ->
                    _uiState.update { it.copy(contactCount = contacts.size) }
                }
            }
        }
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.Load -> load()
            ProfileUiAction.ManualSyncClicked -> manualSync()
            ProfileUiAction.SignOutClicked -> signOut()
            ProfileUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val storedSyncEpoch = settings.getLong(PREF_LAST_SYNCED_AT, 0L)
            val lastSyncedInstant = if (storedSyncEpoch > 0L) Instant.fromEpochMilliseconds(storedSyncEpoch) else null
            getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isLoading = false, lastSyncedAt = lastSyncedInstant ?: user?.updatedAt, error = null) }
                }
                .onFailure { throwable -> fail(throwable) }
        }
    }

    private fun manualSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            runCatching {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    error(AppStrings.ERROR_TOKEN_NOT_FOUND)
                }
                // 1. Upload unuploaded local data to cloud first
                val localLists = shoppingListLocal.watchAll().first()
                for (list in localLists) {
                    val items = listItemLocal.watchForList(list.id).first()
                    val driveList = DriveShoppingList(
                        id = list.id,
                        title = list.title,
                        ownerId = list.ownerId,
                        memberIds = list.memberIds,
                        members = list.members.map { DriveListMember(it.uid, it.displayName, it.avatarUrl) },
                        createdAt = list.createdAt,
                        updatedAt = list.updatedAt,
                        lastSyncedAt = list.lastSyncedAt,
                        items = items.map { item ->
                            DriveListItem(
                                id = item.id,
                                name = item.name,
                                quantity = item.quantity,
                                unit = item.unit,
                                isChecked = item.isChecked,
                                addedBy = item.addedBy,
                                addedByName = item.addedByName,
                                checkedBy = item.checkedBy,
                                position = item.position,
                                createdAt = item.createdAt,
                                updatedAt = item.updatedAt,
                            )
                        },
                        isArchived = list.isArchived,
                    )
                    driveRepository.saveList(token, driveList).getOrThrow()
                }

                // 2. Sync from cloud
                syncFromDrive(token).getOrThrow()

                // 3. Set real completion timestamp
                val completionTime = Clock.System.now()
                settings.putLong(PREF_LAST_SYNCED_AT, completionTime.toEpochMilliseconds())
                completionTime
            }.onSuccess { realServerTime ->
                _uiState.update { it.copy(isSyncing = false, isSyncedSuccessfully = true, lastSyncedAt = realServerTime, error = null) }
                delay(1000)
                _uiState.update { it.copy(isSyncedSuccessfully = false) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSyncing = false) }
                fail(throwable)
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true) }
            signOutUseCase()
                .onSuccess { _uiEvent.send(ProfileUiEvent.NavigateToSignIn) }
                .onFailure { throwable -> fail(throwable) }
            _uiState.update { it.copy(isSigningOut = false) }
        }
    }

    private suspend fun fail(throwable: Throwable) {
        val message = throwable.message ?: "Unable to complete sync operation."
        _uiState.update { it.copy(isLoading = false, error = message) }
        _uiEvent.send(ProfileUiEvent.ShowError(message))
    }
}
