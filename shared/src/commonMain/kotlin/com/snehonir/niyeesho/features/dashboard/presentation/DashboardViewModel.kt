package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.contacts.domain.usecase.RemoveContactUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListItem
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListMember
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.drive.domain.usecase.SyncFromDriveUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.CreateShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.DeleteShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.GetShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.WatchShoppingListsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val watchLists: WatchShoppingListsUseCase,
    private val watchContacts: WatchContactsUseCase,
    private val getShoppingList: GetShoppingListUseCase,
    private val createList: CreateShoppingListUseCase,
    private val generateInviteLink: GenerateInviteLinkUseCase,
    private val syncFromDrive: SyncFromDriveUseCase,
    private val settings: Settings,
    private val shoppingListLocal: ShoppingListLocalDataSource,
    private val listItemLocal: ListItemLocalDataSource,
    private val driveRepository: DriveRepository,
    private val tokenManager: TokenManager,
    private val deleteShoppingList: DeleteShoppingListUseCase,
    private val removeContactUseCase: RemoveContactUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<DashboardUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<DashboardUiEvent> = _uiEvent.receiveAsFlow()

    init {
        logD("DashboardViewModel", "DashboardViewModel initialized, checking 24h / daily 1:00 AM sync rule")
        viewModelScope.launch {
            checkAndPerformScheduledOr24HourSync()
        }
        viewModelScope.launch {
            combine(
                watchLists(),
                watchContacts(),
            ) { listsResult, contactsResult ->
                val lists = listsResult.getOrDefault(emptyList())
                val contacts = contactsResult.getOrDefault(emptyList())
                logD("DashboardViewModel", "Watched lists count: ${lists.size}, contacts count: ${contacts.size}")

                val existingListIds = lists.map { it.id }.toSet()
                val listModels = lists.map(DashboardUiModel::fromDomain)
                val contactModels = contacts
                    .filter { "contact_list_${it.uid}" !in existingListIds }
                    .map(DashboardUiModel::fromContact)

                val combined = (listModels + contactModels)
                    .distinctBy { it.id }
                    .sortedByDescending { it.updatedAtEpoch }
                    .toImmutableList()

                _uiState.update {
                    it.copy(
                        lists = combined,
                        availableContacts = contacts.distinctBy { contact -> contact.uid }.toImmutableList(),
                        isLoading = false,
                    )
                }
            }.collect {}
        }
    }

    private suspend fun checkAndPerformScheduledOr24HourSync() {
        runCatching {
            val lastSyncedEpoch = settings.getLong("last_synced_at", 0L)
            val nowInstant = Clock.System.now()
            val nowEpoch = nowInstant.toEpochMilliseconds()
            val twentyFourHours = 24 * 3600 * 1000L

            val timeZone = TimeZone.currentSystemDefault()
            val localNow = nowInstant.toLocalDateTime(timeZone)

            // Today at 01:00 AM
            val today1Am = LocalDateTime(localNow.date, LocalTime(1, 0))
            val today1AmEpoch = today1Am.toInstant(timeZone).toEpochMilliseconds()

            val isPast1AmToday = nowEpoch >= today1AmEpoch
            val syncedBeforeToday1Am = lastSyncedEpoch < today1AmEpoch

            val isScheduled1AmDue = isPast1AmToday && syncedBeforeToday1Am
            val needs24HourSync = lastSyncedEpoch == 0L || (nowEpoch - lastSyncedEpoch > twentyFourHours)

            if (needs24HourSync || isScheduled1AmDue) {
                logD("DashboardViewModel", "Sync required (Daily 1:00 AM schedule or 24h rule). Performing sync...")
                val localLists = shoppingListLocal.watchAll().first()
                val token = tokenManager.getToken()
                if (!token.isNullOrBlank()) {
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
                    val remoteLists = syncFromDrive(token).getOrThrow()
                    val syncTime = remoteLists.maxOfOrNull { it.lastSyncedAt ?: it.updatedAt }?.toEpochMilliseconds() ?: nowEpoch
                    settings.putLong("last_synced_at", syncTime)
                    logD("DashboardViewModel", "Daily 1:00 AM / 24h sync completed successfully at $syncTime")
                }
            }
        }.onFailure {
            logD("DashboardViewModel", "Scheduled daily sync failed: ${it.message}")
        }
    }

    fun onAction(action: DashboardUiAction) {
        logD("DashboardViewModel", "onAction called with action: $action")
        when (action) {
            DashboardUiAction.FabClicked -> _uiState.update { it.copy(isFabMenuVisible = true) }
            DashboardUiAction.FabMenuDismissed -> _uiState.update { it.copy(isFabMenuVisible = false) }
            DashboardUiAction.AddGroupClicked -> _uiState.update { it.copy(isFabMenuVisible = false, isCreateDialogVisible = true) }
            DashboardUiAction.InviteShareClicked -> generateInvite()
            DashboardUiAction.InviteDismissed -> _uiState.update { it.copy(isInviteDialogVisible = false, inviteLink = null) }
            DashboardUiAction.CreateClicked -> _uiState.update { it.copy(isCreateDialogVisible = true) }
            DashboardUiAction.CreateDismissed -> _uiState.update { it.copy(isCreateDialogVisible = false) }
            is DashboardUiAction.CreateConfirmed -> create(action.title, action.ownerId, action.members)
            is DashboardUiAction.ListClicked -> handleListClick(action.id)
            is DashboardUiAction.DeleteList -> deleteList(action.id)
            is DashboardUiAction.RemoveContact -> removeContact(action.uid)
            DashboardUiAction.OpenProfile -> viewModelScope.launch {
                logD("DashboardViewModel", "Opening profile event")
                _uiEvent.send(DashboardUiEvent.NavigateToProfile)
            }
            DashboardUiAction.SyncNowClicked -> {}
            DashboardUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun deleteList(id: String) {
        viewModelScope.launch {
            deleteShoppingList(id).onFailure { fail(it) }
        }
    }

    private fun removeContact(uid: String) {
        viewModelScope.launch {
            removeContactUseCase(uid).onFailure { fail(it) }
        }
    }

    private fun generateInvite() {
        logD("DashboardViewModel", "generateInvite() started")
        viewModelScope.launch {
            _uiState.update { it.copy(isFabMenuVisible = false) }
            generateInviteLink(InviteData.preview())
                .onSuccess { link ->
                    logD("DashboardViewModel", "generateInviteLink succeeded with link length: ${link.length}")
                    _uiState.update { it.copy(inviteLink = link, isInviteDialogVisible = true) }
                }
                .onFailure { fail(it) }
        }
    }

    private fun handleListClick(id: String) = viewModelScope.launch {
        logD("DashboardViewModel", "handleListClick() for id: $id")
        if (id.startsWith("contact_list_")) {
            val existing = getShoppingList(id)
            if (existing.isFailure) {
                val contactUid = id.removePrefix("contact_list_")
                val contact = _uiState.value.availableContacts.firstOrNull { it.uid == contactUid }
                val contactName = contact?.displayName ?: "Contact Shopping List"
                val contactMember = contact?.let { ListMember(it.uid, it.displayName, it.avatarUrl) }
                val members = listOfNotNull(contactMember)
                logD("DashboardViewModel", "Creating direct contact list for contactUid: $contactUid")
                createList(title = contactName, ownerId = "user_demo", members = members, id = id)
            }
        }
        _uiEvent.send(DashboardUiEvent.NavigateToDetail(id))
    }

    private fun create(title: String, ownerId: String, members: List<ListMember> = emptyList()) = viewModelScope.launch {
        logD("DashboardViewModel", "create() list with title: $title")
        createList(title, ownerId, members)
            .onSuccess { createdList ->
                logD("DashboardViewModel", "createList succeeded with id: ${createdList.id}")
                _uiState.update { it.copy(isCreateDialogVisible = false) }
                _uiEvent.send(DashboardUiEvent.NavigateToDetail(createdList.id))
            }
            .onFailure { fail(it) }
    }

    private fun fail(error: Throwable) {
        val message = error.message ?: "Unable to load dashboard data."
        logD("DashboardViewModel", "Dashboard failure: $message")
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(DashboardUiEvent.ShowError(message)) }
    }
}
