package com.aconno.acnsensa.domain.interactor.deserializing

import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Single

/**
 * @author aconno
 */
class GetAllDeserializersUseCase(
    private val deserializerRepository: DeserializerRepository
) : SingleUseCase<List<Deserializer>> {
    override fun execute(): Single<List<Deserializer>> {
        return deserializerRepository.getAllDeserializers()
    }
}