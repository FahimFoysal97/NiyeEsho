package com.snehonir.niyeesho.features.drive.di

import com.snehonir.niyeesho.BuildKonfig
import com.snehonir.niyeesho.features.drive.data.remote.DriveRemoteDataSource
import com.snehonir.niyeesho.features.drive.data.remote.DriveRemoteDataSourceImpl
import com.snehonir.niyeesho.features.drive.data.remote.FakeDriveRemoteDataSource
import com.snehonir.niyeesho.features.drive.data.repository.DriveRepositoryImpl
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.drive.domain.usecase.DeleteListFromDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.GetProfileFromDriveUrlUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.ImportUserDataFromDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.InitializeDriveFolderUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.ReadContactsFromDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.SaveContactsToDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.SaveListToDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.SyncFromDriveUseCase
import com.snehonir.niyeesho.features.drive.domain.usecase.UpdateProfileOnDriveUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val driveModule = module {
    single<DriveRemoteDataSource> {
        if (BuildKonfig.USE_FAKE_DATA) FakeDriveRemoteDataSource() else DriveRemoteDataSourceImpl(get())
    }
    singleOf(::DriveRepositoryImpl).bind<DriveRepository>()
    singleOf(::InitializeDriveFolderUseCase)
    singleOf(::SyncFromDriveUseCase)
    singleOf(::UpdateProfileOnDriveUseCase)
    singleOf(::SaveListToDriveUseCase)
    singleOf(::DeleteListFromDriveUseCase)
    singleOf(::SaveContactsToDriveUseCase)
    singleOf(::ReadContactsFromDriveUseCase)
    singleOf(::GetProfileFromDriveUrlUseCase)
    singleOf(::ImportUserDataFromDriveUseCase)
}
