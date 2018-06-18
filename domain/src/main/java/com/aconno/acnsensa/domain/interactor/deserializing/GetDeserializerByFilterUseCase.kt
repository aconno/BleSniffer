package com.aconno.acnsensa.domain.interactor.deserializing

import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithTwoParameters
import io.reactivex.Single

class GetDeserializerByFilterUseCase(
        private val deserializerRepository: DeserializerRepository
) : SingleUseCaseWithTwoParameters<Deserializer, String, String> {

    override fun execute(parameter: String, parameter2: String): Single<Deserializer> {
        return deserializerRepository.getDeserializerByFilter(parameter, Deserializer.Type.valueOf(parameter2))
    }
}