package com.aconno.blesniffer.domain.interactor.deserializing

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

/**
 * @author aconno
 */
class DeleteDeserializersUseCase(
        private val deserializerRepository: DeserializerRepository
) : CompletableUseCaseWithParameter<List<Deserializer>> {
    override fun execute(parameter: List<Deserializer>): Completable {
        return Completable.fromAction {
            var deleted = 0
            parameter.forEach {
                deserializerRepository.deleteDeserializer(it)
                deleted++
            }
        }
    }
}