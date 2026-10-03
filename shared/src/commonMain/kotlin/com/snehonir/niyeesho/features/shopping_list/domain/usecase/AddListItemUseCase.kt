package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository

class AddListItemUseCase(private val repository: ListItemRepository) {
    suspend operator fun invoke(item: ListItem): Result<Unit> =
        if (item.name.isBlank()) Result.failure(IllegalArgumentException("Item name is required."))
        else repository.addItem(item)
}
