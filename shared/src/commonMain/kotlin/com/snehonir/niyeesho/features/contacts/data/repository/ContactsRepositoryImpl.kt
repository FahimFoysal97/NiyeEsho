package com.snehonir.niyeesho.features.contacts.data.repository

import com.snehonir.niyeesho.features.contacts.data.local.ContactsLocalDataSource
import com.snehonir.niyeesho.features.contacts.data.mapper.toContact
import com.snehonir.niyeesho.features.contacts.data.mapper.toDriveContact
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ContactsRepositoryImpl(
    private val local: ContactsLocalDataSource,
    private val driveRepository: DriveRepository,
) : ContactsRepository {
    override fun watchContacts(): Flow<Result<List<Contact>>> =
        local.watchActive().map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override fun watchActiveContacts(): Flow<Result<List<Contact>>> =
        local.watchActive().map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override fun watchPendingIncomingContacts(): Flow<Result<List<Contact>>> =
        local.watchPendingIncoming().map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override fun watchPendingOutgoingContacts(): Flow<Result<List<Contact>>> =
        local.watchPendingOutgoing().map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override suspend fun getContact(uid: String): Contact? = local.getById(uid)

    override suspend fun getActiveContacts(): List<Contact> = local.getActive()

    override suspend fun addContact(contact: Contact): Result<Unit> = runCatching {
        local.upsert(contact)
    }

    override suspend fun updateStatus(uid: String, status: ContactStatus): Result<Unit> = runCatching {
        local.updateStatus(uid, status)
    }

    override suspend fun removeContact(uid: String): Result<Unit> = runCatching {
        local.delete(uid)
    }

    override suspend fun searchContacts(query: String): Result<List<Contact>> = runCatching {
        val normalized = query.trim().lowercase()
        local.getActive().filter {
            normalized.isBlank() ||
                it.displayName.lowercase().contains(normalized) ||
                it.email.lowercase().contains(normalized)
        }
    }

    override suspend fun refreshContactToken(uid: String): Result<Contact> = runCatching {
        val contact = local.getById(uid)
            ?: error("Contact not found.")
        val profile = driveRepository.getProfileFromDriveUrl(contact.profileDriveUrl).getOrThrow()
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
        val remoteContacts = driveRepository.readContacts(accessToken).getOrThrow().map { it.toContact() }
        local.upsertAll(remoteContacts)
        val activeLocal = local.getActive().map { it.toDriveContact() }
        driveRepository.saveContacts(accessToken, activeLocal).getOrThrow()
    }
}
