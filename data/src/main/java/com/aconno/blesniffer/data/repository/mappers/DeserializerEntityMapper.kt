package com.aconno.blesniffer.data.repository.mappers

import com.aconno.blesniffer.data.repository.DeserializerEntity
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DeserializerEntityMapper {

    private val valueDeserializersTypeToken: TypeToken<MutableList<GeneralFieldDeserializer>> by lazy {
        object : TypeToken<MutableList<GeneralFieldDeserializer>>() {}
    }

    fun toEntity(deserializer: Deserializer): DeserializerEntity {
        return DeserializerEntity(
            id = deserializer.id,
            name = deserializer.name,
            filter = deserializer.filter,
            filterType = deserializer.filterType.name,
            fieldDeserializers = Gson().toJson(deserializer.fieldDeserializers),
            sampleData = deserializer.sampleData
        )
    }

    fun toDeserializer(deserializerEntity: DeserializerEntity): Deserializer {
        return GeneralDeserializer(
            id = deserializerEntity.id,
            name = deserializerEntity.name,
            filter = deserializerEntity.filter,
            filterType = Deserializer.Type.valueOf(deserializerEntity.filterType),
            fieldDeserializers = Gson().fromJson(
                deserializerEntity.fieldDeserializers,
                valueDeserializersTypeToken.type
            ),
            sampleData = deserializerEntity.sampleData
        )
    }
}