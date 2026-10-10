package com.snehonir.niyeesho.features.messaging.di

import com.snehonir.niyeesho.features.messaging.data.handler.ContactMessageHandler
import com.snehonir.niyeesho.features.messaging.data.handler.FcmMessageHandler
import com.snehonir.niyeesho.features.messaging.data.handler.ItemMessageHandler
import com.snehonir.niyeesho.features.messaging.data.handler.ListMessageHandler
import com.snehonir.niyeesho.features.messaging.data.handler.SyncMessageHandler
import com.snehonir.niyeesho.features.messaging.data.handler.TokenMessageHandler
import com.snehonir.niyeesho.features.messaging.data.local.MessagingLocalDataSource
import com.snehonir.niyeesho.features.messaging.data.local.MessagingLocalDataSourceImpl
import com.snehonir.niyeesho.features.messaging.data.remote.MessagingRemoteDataSource
import com.snehonir.niyeesho.features.messaging.data.remote.MessagingRemoteDataSourceImpl
import com.snehonir.niyeesho.features.messaging.data.repository.MessagingRepositoryImpl
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository
import com.snehonir.niyeesho.features.messaging.domain.usecase.BroadcastTokenUpdateUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateQrCodeUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.HandleIncomingMessageUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.SendContactRequestUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.SendFcmMessageUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val messagingModule = module {
    singleOf(::MessagingLocalDataSourceImpl).bind<MessagingLocalDataSource>()
    single<MessagingRemoteDataSource> { MessagingRemoteDataSourceImpl(get(), get()) }
    singleOf(::ContactMessageHandler)
    singleOf(::ListMessageHandler)
    singleOf(::ItemMessageHandler)
    single { TokenMessageHandler { get<MessagingLocalDataSource>().emitIncoming(it).let { Result.success(Unit) } } }
    single { SyncMessageHandler { get<MessagingLocalDataSource>().emitIncoming(it).let { Result.success(Unit) } } }
    singleOf(::FcmMessageHandler)
    singleOf(::MessagingRepositoryImpl).bind<MessagingRepository>()
    singleOf(::SendFcmMessageUseCase)
    singleOf(::BroadcastTokenUpdateUseCase)
    singleOf(::HandleIncomingMessageUseCase)
    singleOf(::GenerateInviteLinkUseCase)
    singleOf(::GenerateQrCodeUseCase)
    singleOf(::SendContactRequestUseCase)
}
