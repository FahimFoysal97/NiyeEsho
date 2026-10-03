package com.snehonir.niyeesho.features.shopping_list.di

import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSourceImpl
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSourceImpl
import com.snehonir.niyeesho.features.shopping_list.data.repository.ListItemRepositoryImpl
import com.snehonir.niyeesho.features.shopping_list.data.repository.ShoppingListRepositoryImpl
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.*
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.ListDetailViewModel
import com.snehonir.niyeesho.features.shopping_list.presentation.list.ShoppingListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val shoppingListModule = module {
    singleOf(::ShoppingListLocalDataSourceImpl).bind<ShoppingListLocalDataSource>()
    singleOf(::ListItemLocalDataSourceImpl).bind<ListItemLocalDataSource>()
    singleOf(::ShoppingListRepositoryImpl).bind<ShoppingListRepository>()
    singleOf(::ListItemRepositoryImpl).bind<ListItemRepository>()
    singleOf(::WatchShoppingListsUseCase)
    singleOf(::GetShoppingListUseCase)
    singleOf(::CreateShoppingListUseCase)
    singleOf(::UpdateShoppingListUseCase)
    singleOf(::DeleteShoppingListUseCase)
    singleOf(::ArchiveShoppingListUseCase)
    singleOf(::InviteMemberUseCase)
    singleOf(::AddMembersToListUseCase)
    singleOf(::RemoveMemberUseCase)
    singleOf(::WatchListItemsUseCase)
    singleOf(::AddListItemUseCase)
    singleOf(::UpdateListItemUseCase)
    singleOf(::ToggleListItemUseCase)
    singleOf(::DeleteListItemUseCase)
    singleOf(::ReorderListItemsUseCase)
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::ListDetailViewModel)
}
