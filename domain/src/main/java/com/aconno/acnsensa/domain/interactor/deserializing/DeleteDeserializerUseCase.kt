package com.aconno.acnsensa.domain.interactor.deserializing

import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

/**
 * @author aconno
 */
class DeleteDeserializerUseCase(
    private val deserializerRepository: DeserializerRepository
) : CompletableUseCaseWithParameter<Deserializer> {
    override fun execute(parameter: Deserializer): Completable {
        return Completable.fromAction {
            deserializerRepository.deleteDeserializer(parameter)
        }
    }
}