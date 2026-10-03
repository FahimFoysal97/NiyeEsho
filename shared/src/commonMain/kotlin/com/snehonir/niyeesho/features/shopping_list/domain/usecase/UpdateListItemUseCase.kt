package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository

class UpdateListItemUseCase(private val repository: ListItemRepository) {
    suspend operator fun invoke(item: ListItem): Result<Unit> = repository.updateItem(item)
}
