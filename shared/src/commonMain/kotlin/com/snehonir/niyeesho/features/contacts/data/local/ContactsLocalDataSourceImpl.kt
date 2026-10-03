package com.snehonir.niyeesho.features.contacts.data.local

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactsLocalDataSourceImpl : ContactsLocalDataSource {
    private val contacts = MutableStateFlow<List<Contact>>(listOf(Contact.preview()))

    override fun watchAll(): Flow<List<Contact>> = contacts.asStateFlow()
    override suspend fun getAll(): List<Contact> = contacts.value
    override suspend fun upsert(contact: Contact) {
        contacts.value = contacts.value.filterNot { it.uid == contact.uid } + contact
    }
    override suspend fun upsertAll(contacts: List<Contact>) {
        contacts.forEach { upsert(it) }
    }
    override suspend fun delete(uid: String) {
        contacts.value = contacts.value.filterNot { it.uid == uid }
    }
}
