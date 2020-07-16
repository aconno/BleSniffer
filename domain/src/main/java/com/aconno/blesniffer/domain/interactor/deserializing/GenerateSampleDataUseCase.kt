package com.aconno.blesniffer.domain.interactor.deserializing

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.sampledatagenerator.SampleDataGenerator
import com.aconno.blesniffer.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GenerateSampleDataUseCase : SingleUseCaseWithParameter<ByteArray,Deserializer> {

    override fun execute(parameter: Deserializer): Single<ByteArray> {
        val requiredLength = max(
            parameter.fieldDeserializers.map { it.endIndexExclusive }.max() ?: 0,
            parameter.fieldDeserializers.map { it.startIndexInclusive }.max() ?: 0
        )

        val bytes = ByteArray(requiredLength)
        bytes.forEachIndexed { index, byte -> bytes[index] = 0xFF.toByte() }

        parameter.fieldDeserializers.forEach {
            SampleDataGenerator.generateSampleValueForType(it.type,abs(it.endIndexExclusive - it.startIndexInclusive)).copyInto(bytes,min(it.startIndexInclusive,it.endIndexExclusive))
        }

        return Single.just(bytes)
    }
}