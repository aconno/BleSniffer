package com.aconno.blesniffer.domain.interactor.deserializing

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.type.SingleUseCase
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