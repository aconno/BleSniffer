package com.aconno.blesniffer.domain.deserializing.sampledatagenerator

import com.aconno.blesniffer.domain.ValueConverter
import com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators.*

object SampleDataGenerator {
    private val TYPES_TO_GENERATORS_MAP = mapOf(
        ValueConverter.BOOLEAN to BooleanGenerator(),
        ValueConverter.BYTE to ByteGenerator(),
        ValueConverter.FLOAT to FloatGenerator(),
        ValueConverter.MAC_ADDRESS to MacAddressGenerator(),
        ValueConverter.SINT16 to SignedInt16Generator(),
        ValueConverter.SINT32 to SignedInt32Generator(),
        ValueConverter.SINT8 to SignedInt8Generator(),
        ValueConverter.UINT8 to UnsignedInt8Generator(),
        ValueConverter.UINT16 to UnsignedInt16Generator(),
        ValueConverter.UINT32 to UnsignedInt32Generator(),
        ValueConverter.UTF8STRING to UTF8StringGenerator(),
        ValueConverter.TIME to TimeGenerator()
    )

    fun generateSampleValueForType(type : ValueConverter, valueSizeBytes : Int) : ByteArray {
        return type.converter.serialize(TYPES_TO_GENERATORS_MAP[type]?.generateValue(valueSizeBytes).toString())
    }

}