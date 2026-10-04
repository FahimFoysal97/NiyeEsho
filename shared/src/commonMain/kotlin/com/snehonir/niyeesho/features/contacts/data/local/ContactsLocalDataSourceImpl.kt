package com.snehonir.niyeesho.features.contacts.data.local

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ContactsLocalDataSourceImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ContactsLocalDataSource {
    private val contactsFlow = MutableStateFlow<List<Contact>>(loadContacts())

    private fun loadContacts(): List<Contact> {
        val jsonStr = settings.getString(key = KEY_CONTACTS, defaultValue = "")
        if (jsonStr.isBlank()) return listOf(Contact.preview())
        return try {
            json.decodeFromString(ListSerializer(Contact.serializer()), jsonStr)
        } catch (_: Exception) {
            listOf(Contact.preview())
        }
    }

    private fun persist(updated: List<Contact>) {
        contactsFlow.value = updated
        settings.putString(key = KEY_CONTACTS, value = json.encodeToString(ListSerializer(Contact.serializer()), updated))
    }

    override fun watchAll(): Flow<List<Contact>> = contactsFlow.asStateFlow()
    override suspend fun getAll(): List<Contact> = contactsFlow.value
    override suspend fun upsert(contact: Contact) {
        val current = contactsFlow.value.filterNot { it.uid == contact.uid } + contact
        persist(current)
    }
    override suspend fun upsertAll(contacts: List<Contact>) {
        var current = contactsFlow.value
        contacts.forEach { contact ->
            current = current.filterNot { it.uid == contact.uid } + contact
        }
        persist(current)
    }
    override suspend fun delete(uid: String) {
        val current = contactsFlow.value.filterNot { it.uid == uid }
        persist(current)
    }

    companion object {
        private const val KEY_CONTACTS = "contacts"
    }
}
