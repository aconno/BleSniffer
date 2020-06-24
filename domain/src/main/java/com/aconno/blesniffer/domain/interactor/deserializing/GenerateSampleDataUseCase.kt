package com.aconno.blesniffer.domain.interactor.deserializing

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.sampledatagenerator.SampleDataGenerator
import com.aconno.blesniffer.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class GenerateSampleDataUseCase : SingleUseCaseWithParameter<ByteArray,Deserializer> {

    override fun execute(parameter: Deserializer): Single<ByteArray> {
        val requiredLength = parameter.fieldDeserializers.map { it.endIndexExclusive }.max()
            ?: return Single.just(byteArrayOf())

        val bytes = ByteArray(requiredLength)
        bytes.forEachIndexed { index, byte -> bytes[index] = 0xFF.toByte() }

        parameter.fieldDeserializers.forEach {
            SampleDataGenerator.generateSampleValueForType(it.type).copyInto(bytes,it.startIndexInclusive)
        }

        return Single.just(bytes)
    }
}