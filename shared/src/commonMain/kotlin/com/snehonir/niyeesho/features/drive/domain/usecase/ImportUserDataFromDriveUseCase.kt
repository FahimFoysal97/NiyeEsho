package com.snehonir.niyeesho.features.drive.domain.usecase

import com.snehonir.niyeesho.features.contacts.data.local.ContactsLocalDataSource
import com.snehonir.niyeesho.features.drive.data.mapper.toContact
import com.snehonir.niyeesho.features.drive.data.mapper.toListItem
import com.snehonir.niyeesho.features.drive.data.mapper.toShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource

/** Imports all Drive shopping lists, list items, and contacts into local storage on Google Sign-In. */
class ImportUserDataFromDriveUseCase(
    private val driveRepository: DriveRepository,
    private val shoppingListLocal: ShoppingListLocalDataSource,
    private val listItemLocal: ListItemLocalDataSource,
    private val contactsLocal: ContactsLocalDataSource,
) {
    suspend operator fun invoke(accessToken: String): Result<Unit> = runCatching {
        val driveLists = driveRepository.syncFromDrive(accessToken).getOrDefault(emptyList())
        driveLists.forEach { driveList ->
            shoppingListLocal.upsert(driveList.toShoppingList())
            driveList.items.forEach { driveItem ->
                listItemLocal.upsert(driveItem.toListItem(driveList.id))
            }
        }

        val driveContacts = driveRepository.readContacts(accessToken).getOrDefault(emptyList())
        driveContacts.forEach { driveContact ->
            contactsLocal.upsert(driveContact.toContact())
        }
    }
}
