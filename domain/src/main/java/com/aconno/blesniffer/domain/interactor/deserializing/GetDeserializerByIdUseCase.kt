package com.aconno.blesniffer.domain.interactor.deserializing

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.blesniffer.domain.interactor.type.SingleUseCaseWithTwoParameters
import io.reactivex.Single

class GetDeserializerByIdUseCase(
        private val deserializerRepository: DeserializerRepository
) : SingleUseCaseWithParameter<Deserializer, Long> {

    override fun execute(parameter: Long): Single<Deserializer> {
        return deserializerRepository.getDeserializerById(parameter)
    }
}