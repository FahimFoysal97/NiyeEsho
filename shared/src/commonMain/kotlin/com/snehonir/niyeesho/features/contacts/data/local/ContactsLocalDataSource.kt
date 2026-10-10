package com.snehonir.niyeesho.features.contacts.data.local

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import kotlinx.coroutines.flow.Flow

interface ContactsLocalDataSource {
    fun watchAll(): Flow<List<Contact>>
    fun watchActive(): Flow<List<Contact>>
    fun watchPendingIncoming(): Flow<List<Contact>>
    fun watchPendingOutgoing(): Flow<List<Contact>>
    suspend fun getAll(): List<Contact>
    suspend fun getActive(): List<Contact>
    suspend fun getById(uid: String): Contact?
    suspend fun upsert(contact: Contact)
    suspend fun upsertAll(contacts: List<Contact>)
    suspend fun updateStatus(uid: String, status: ContactStatus)
    suspend fun delete(uid: String)
}
