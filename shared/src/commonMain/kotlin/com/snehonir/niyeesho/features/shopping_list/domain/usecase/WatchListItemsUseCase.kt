package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository
import kotlinx.coroutines.flow.Flow

class WatchListItemsUseCase(private val repository: ListItemRepository) {
    operator fun invoke(listId: String): Flow<Result<List<ListItem>>> = repository.watchListItems(listId)
}
