package com.snehonir.niyeesho.features.contacts.domain.repository

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import kotlinx.coroutines.flow.Flow

/** Owns the local contacts cache and its Drive/token synchronization. */
interface ContactsRepository {
    fun watchContacts(): Flow<Result<List<Contact>>>
    fun watchActiveContacts(): Flow<Result<List<Contact>>>
    fun watchPendingIncomingContacts(): Flow<Result<List<Contact>>>
    fun watchPendingOutgoingContacts(): Flow<Result<List<Contact>>>
    suspend fun getContact(uid: String): Contact?
    suspend fun getActiveContacts(): List<Contact>
    suspend fun addContact(contact: Contact): Result<Unit>
    suspend fun updateStatus(uid: String, status: ContactStatus): Result<Unit>
    suspend fun removeContact(uid: String): Result<Unit>
    suspend fun searchContacts(query: String): Result<List<Contact>>
    suspend fun refreshContactToken(uid: String): Result<Contact>
    suspend fun syncWithDrive(accessToken: String): Result<Unit>
}
