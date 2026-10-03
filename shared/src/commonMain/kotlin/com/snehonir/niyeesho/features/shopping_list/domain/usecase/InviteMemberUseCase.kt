package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository

class InviteMemberUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(listId: String, memberId: String): Result<Unit> =
        repository.inviteMember(listId, memberId)
}
