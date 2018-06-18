package com.aconno.blesniffer.domain.interactor.deserializing

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class UpdateDeserializerUseCase(
        private val deserializerRepository: DeserializerRepository
) : CompletableUseCaseWithParameter<Deserializer> {

    override fun execute(parameter: Deserializer): Completable {
        return Completable.fromAction {
            deserializerRepository.updateDeserializer(parameter)
        }
    }
}