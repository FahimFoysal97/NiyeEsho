package com.snehonir.niyeesho.features.contacts.data.local

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ContactsLocalDataSourceImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ContactsLocalDataSource {
    private val contactsFlow = MutableStateFlow<List<Contact>>(loadContacts())

    private fun loadContacts(): List<Contact> {
        logD("ContactsLocal(DB)", "loadContacts() called")
        val jsonStr = settings.getString(key = KEY_CONTACTS, defaultValue = "")
        if (jsonStr.isBlank()) return listOf(Contact.preview())
        return try {
            val contacts = json.decodeFromString(ListSerializer(Contact.serializer()), jsonStr)
            logD("ContactsLocal(DB)", "loadContacts() -> loaded ${contacts.size} contacts from local settings")
            contacts
        } catch (e: Exception) {
            logD("ContactsLocal(DB)", "loadContacts() -> failed: ${e.message}")
            listOf(Contact.preview())
        }
    }

    private fun persist(updated: List<Contact>) {
        logD("ContactsLocal(DB)", "persist() -> saving ${updated.size} contacts to local settings")
        contactsFlow.value = updated
        settings.putString(key = KEY_CONTACTS, value = json.encodeToString(ListSerializer(Contact.serializer()), updated))
        logD("ContactsLocal(DB)", "persist() -> successfully saved")
    }

    override fun watchAll(): Flow<List<Contact>> = contactsFlow.asStateFlow().also {
        logD("ContactsLocal(DB)", "watchAll() called")
    }

    override fun watchActive(): Flow<List<Contact>> = contactsFlow.map { list ->
        list.filter { it.status == ContactStatus.ACTIVE && !it.isBlocked }
    }

    override fun watchPendingIncoming(): Flow<List<Contact>> = contactsFlow.map { list ->
        list.filter { it.status == ContactStatus.PENDING_INCOMING }
    }

    override fun watchPendingOutgoing(): Flow<List<Contact>> = contactsFlow.map { list ->
        list.filter { it.status == ContactStatus.PENDING_OUTGOING }
    }

    override suspend fun getAll(): List<Contact> = contactsFlow.value.also {
        logD("ContactsLocal(DB)", "getAll() -> count: ${it.size}")
    }

    override suspend fun getActive(): List<Contact> = contactsFlow.value.filter {
        it.status == ContactStatus.ACTIVE && !it.isBlocked
    }

    override suspend fun getById(uid: String): Contact? = contactsFlow.value.firstOrNull { it.uid == uid }

    override suspend fun upsert(contact: Contact) {
        logD("ContactsLocal(DB)", "upsert() contact '${contact.displayName}' (uid: ${contact.uid}, status: ${contact.status})")
        val current = contactsFlow.value.filterNot { it.uid == contact.uid } + contact
        persist(current)
    }

    override suspend fun upsertAll(contacts: List<Contact>) {
        logD("ContactsLocal(DB)", "upsertAll() with ${contacts.size} contacts")
        var current = contactsFlow.value
        contacts.forEach { contact ->
            current = current.filterNot { it.uid == contact.uid } + contact
        }
        persist(current)
    }

    override suspend fun updateStatus(uid: String, status: ContactStatus) {
        logD("ContactsLocal(DB)", "updateStatus() uid: $uid -> status: $status")
        val current = contactsFlow.value.map { contact ->
            if (contact.uid == uid) contact.copy(status = status) else contact
        }
        persist(current)
    }

    override suspend fun delete(uid: String) {
        logD("ContactsLocal(DB)", "delete() uid: $uid")
        val current = contactsFlow.value.filterNot { it.uid == uid }
        persist(current)
    }

    companion object {
        private const val KEY_CONTACTS = "contacts"
    }
}
