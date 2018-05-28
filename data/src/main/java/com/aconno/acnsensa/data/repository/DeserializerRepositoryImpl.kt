package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.ValueConverter
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single

private val valueDeserializersTypeToken: TypeToken<MutableList<Triple<String, Pair<Int, Int>, ValueConverter>>> by lazy {
    object : TypeToken<MutableList<Triple<String, Pair<Int, Int>, ValueConverter>>>() {}
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
        return deserializerDao.all.map { deserializerEntities -> deserializerEntities.map { toDeserializer(it) } }
    }

    override fun getDeserializerByFilter(filter: String, type: Deserializer.Type): Single<Deserializer> {
        return deserializerDao.getDeserializerByFilter(filter, type.name).map { deserializerEntity -> toDeserializer(deserializerEntity) }
    }

    private fun toEntity(deserializer: Deserializer): DeserializerEntity {
        return DeserializerEntity(
            filter = deserializer.filter,
            filterType = deserializer.filterType.name,
            valueDeserializers = Gson().toJson(deserializer.valueDeserializers)
        )
    }

    private fun toDeserializer(deserializerEntity: DeserializerEntity): Deserializer {
        return GeneralDeserializer(
                filter = deserializerEntity.filter,
                filterType = Deserializer.Type.valueOf(deserializerEntity.filterType),
                valueDeserializers = Gson().fromJson(
                        deserializerEntity.valueDeserializers,
                        valueDeserializersTypeToken.type
                )
        )
    }
}