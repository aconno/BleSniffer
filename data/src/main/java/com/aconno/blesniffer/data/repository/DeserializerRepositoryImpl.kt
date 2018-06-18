package com.aconno.blesniffer.data.repository

import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single

private val valueDeserializersTypeToken: TypeToken<MutableList<GeneralFieldDeserializer>> by lazy {
    object : TypeToken<MutableList<GeneralFieldDeserializer>>() {}
}

class DeserializerRepositoryImpl(
        private val deserializerDao: DeserializerDao
) : DeserializerRepository {

    override fun addDeserializer(deserializer: Deserializer) {
        deserializerDao.insert(toEntity(deserializer))
    }

    override fun updateDeserializer(deserializer: Deserializer) {
        deserializerDao.update(toEntity(deserializer))
    }

    override fun deleteDeserializer(deserializer: Deserializer) {
        deserializerDao.delete(toEntity(deserializer))
    }

    override fun getAllDeserializers(): Single<List<Deserializer>> {
        return deserializerDao.all
                .map { deserializerEntities -> deserializerEntities.map { toDeserializer(it) } }
    }

    override fun getDeserializerByFilter(filter: String, type: Deserializer.Type): Single<Deserializer> {
        return deserializerDao.getDeserializerByFilter(filter, type.name)
                .map { deserializerEntity -> toDeserializer(deserializerEntity) }
    }

    override fun getDeserializerById(id: Long): Single<Deserializer> {
        return deserializerDao.getDeserializerById(id)
                .map { deserializerEntity -> toDeserializer(deserializerEntity) }
    }

    private fun toEntity(deserializer: Deserializer): DeserializerEntity {
        return DeserializerEntity(
                id = deserializer.id,
                name = deserializer.name,
                filter = deserializer.filter,
                filterType = deserializer.filterType.name,
                fieldDeserializers = Gson().toJson(deserializer.fieldDeserializers),
                sampleData = deserializer.sampleData
        )
    }

    private fun toDeserializer(deserializerEntity: DeserializerEntity): Deserializer {
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