package com.aconno.blesniffer.dagger.deserializerlistactivity

import com.aconno.blesniffer.device.storage.DeserializerFileStorage
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.deserializing.*
import com.aconno.blesniffer.ui.DeserializerListActivity
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
    fun provideAddDeserializersUseCase(deserializerRepository: DeserializerRepository): AddDeserializersUseCase {
        return AddDeserializersUseCase(deserializerRepository)
    }

    @Provides
    @DeserializerListActivityScope
    fun provideDeleteDeserializerUseCase(deserializerRepository: DeserializerRepository): DeleteDeserializerUseCase {
        return DeleteDeserializerUseCase(deserializerRepository)
    }

    @Provides
    @DeserializerListActivityScope
    fun provideDeleteDeserializersUseCase(deserializerRepository: DeserializerRepository): DeleteDeserializersUseCase {
        return DeleteDeserializersUseCase(deserializerRepository)
    }

    @Provides
    @DeserializerListActivityScope
    fun provideDeserializerFileStorage(): DeserializerFileStorage {
        return DeserializerFileStorage(deserializerListActivity)
    }
}