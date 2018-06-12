package com.aconno.acnsensa.domain.interactor.deserializing

import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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