package com.aconno.acnsensa.dagger.deserializerlistactivity

import com.aconno.acnsensa.device.permissons.PermissionActionFactory
import com.aconno.acnsensa.device.storage.DeserializerFileStorage
import com.aconno.acnsensa.device.storage.JsonFileStorageImpl
import com.aconno.acnsensa.domain.JsonFileStorage
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.aconno.acnsensa.domain.interactor.deserializing.AddDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.DeleteDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.acnsensa.ui.DeserializerListActivity
import com.aconno.acnsensa.viewmodel.PermissionViewModel
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class DeserializerListActivityModule(private val deserializerListActivity: DeserializerListActivity) {
    @Provides
    @DeserializerListActivityScope
    fun provideEditDeserializerActivity() = deserializerListActivity


    @Provides
    @DeserializerListActivityScope
    fun provideGetAllDeserializersUseCase(deserializerRepository: DeserializerRepository): GetAllDeserializersUseCase {
        return GetAllDeserializersUseCase(deserializerRepository)
    }


    @Provides
    @DeserializerListActivityScope
    fun provideAddDeserializerUseCase(deserializerRepository: DeserializerRepository): AddDeserializerUseCase {
        return AddDeserializerUseCase(deserializerRepository)
    }

    @Provides
    @DeserializerListActivityScope
    fun provideDeleteDeserializerUseCase(deserializerRepository: DeserializerRepository): DeleteDeserializerUseCase {
        return DeleteDeserializerUseCase(deserializerRepository)
    }


    @Provides
    @DeserializerListActivityScope
    fun providePermissionsViewModel(): PermissionViewModel {
        val permissionAction = PermissionActionFactory.getPermissionAction(deserializerListActivity)
        return PermissionViewModel(permissionAction, deserializerListActivity)
    }

    @Provides
    @DeserializerListActivityScope
    fun provideDeserializerFileStorage(): DeserializerFileStorage{
        return DeserializerFileStorage(deserializerListActivity)
    }
}