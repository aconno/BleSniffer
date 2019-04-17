package com.aconno.blesniffer.data.repository

import com.aconno.blesniffer.data.repository.mappers.DeserializerEntityMapper
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single

class DeserializerRepositoryImpl(
    private val deserializerDao: DeserializerDao,
    private val deserializerEntityMapper: DeserializerEntityMapper
) : DeserializerRepository {

    override fun addDeserializer(deserializer: Deserializer) {
        deserializerDao.insert(deserializerEntityMapper.toEntity(deserializer))
    }

    override fun updateDeserializer(deserializer: Deserializer) {
        deserializerDao.update(deserializerEntityMapper.toEntity(deserializer))
    }

    override fun deleteDeserializer(deserializer: Deserializer) {
        deserializerDao.delete(deserializerEntityMapper.toEntity(deserializer))
    }

    override fun getAllDeserializers(): Single<List<Deserializer>> {
        return deserializerDao.all
            .map { deserializerEntities ->
                deserializerEntities.map {
                    deserializerEntityMapper.toDeserializer(
                        it
                    )
                }
            }
    }

    override fun getDeserializerByFilter(
        filter: String,
        type: Deserializer.Type
    ): Single<Deserializer> {
        return deserializerDao.getDeserializerByFilter(filter, type.name)
            .map { deserializerEntity -> deserializerEntityMapper.toDeserializer(deserializerEntity) }
    }

    override fun getDeserializerById(id: Long): Single<Deserializer> {
        return deserializerDao.getDeserializerById(id)
            .map { deserializerEntity -> deserializerEntityMapper.toDeserializer(deserializerEntity) }
    }
}