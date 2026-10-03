package com.snehonir.niyeesho.features.contacts.data.local

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsLocalDataSource {
    fun watchAll(): Flow<List<Contact>>
    suspend fun getAll(): List<Contact>
    suspend fun upsert(contact: Contact)
    suspend fun upsertAll(contacts: List<Contact>)
    suspend fun delete(uid: String)
}
