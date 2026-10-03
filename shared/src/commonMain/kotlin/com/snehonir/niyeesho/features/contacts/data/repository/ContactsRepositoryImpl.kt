package com.snehonir.niyeesho.features.contacts.data.repository

import com.snehonir.niyeesho.features.contacts.data.local.ContactsLocalDataSource
import com.snehonir.niyeesho.features.contacts.data.mapper.toContact
import com.snehonir.niyeesho.features.contacts.data.mapper.toDriveContact
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import com.snehonir.niyeesho.features.drive.domain.usecase.GetProfileFromDriveUrlUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.ReadContactsFromDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.SaveContactsToDriveUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ContactsRepositoryImpl(
    private val local: ContactsLocalDataSource,
    private val readContacts: ReadContactsFromDriveUseCase,
    private val saveContacts: SaveContactsToDriveUseCase,
    private val getProfile: GetProfileFromDriveUrlUseCase,
) : ContactsRepository {
    override fun watchContacts(): Flow<Result<List<Contact>>> =
        local.watchAll().map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override suspend fun addContact(contact: Contact): Result<Unit> = runCatching {
        local.upsert(contact)
    }

    override suspend fun removeContact(uid: String): Result<Unit> = runCatching {
        local.delete(uid)
    }

    override suspend fun searchContacts(query: String): Result<List<Contact>> = runCatching {
        val normalized = query.trim().lowercase()
        local.getAll().filter {
            normalized.isBlank() ||
                it.displayName.lowercase().contains(normalized) ||
                it.email.lowercase().contains(normalized)
        }
    }

    override suspend fun refreshContactToken(uid: String): Result<Contact> = runCatching {
        val contact = local.getAll().firstOrNull { it.uid == uid }
            ?: error("Contact not found.")
        val profile = getProfile(contact.profileDriveUrl).getOrThrow()
        val refreshed = contact.copy(
            displayName = profile.displayName,
            email = profile.email,
            avatarUrl = profile.avatarUrl,
            cachedFcmToken = profile.fcmToken,
            tokenCachedAt = profile.tokenUpdatedAt,
        )
        local.upsert(refreshed)
        refreshed
    }

    override suspend fun syncWithDrive(accessToken: String): Result<Unit> = runCatching {
        val remoteContacts = readContacts(accessToken).getOrThrow().map { it.toContact() }
        local.upsertAll(remoteContacts)
        saveContacts(accessToken, local.getAll().map { it.toDriveContact() }).getOrThrow()
    }
}
