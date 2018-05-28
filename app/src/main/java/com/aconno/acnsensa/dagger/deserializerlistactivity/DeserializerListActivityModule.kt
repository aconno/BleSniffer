package com.aconno.acnsensa.dagger.deserializerlistactivity

import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.interactor.deserializing.AddDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.DeleteDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.acnsensa.ui.DeserializerListActivity
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
}