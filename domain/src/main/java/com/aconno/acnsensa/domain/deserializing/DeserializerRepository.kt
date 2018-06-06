package com.aconno.acnsensa.domain.deserializing

import io.reactivex.Single

/**
 * @author aconno
 */
interface DeserializerRepository {
    fun addDeserializer(deserializer: Deserializer)
    fun updateDeserializer(deserializer: Deserializer)
    fun deleteDeserializer(deserializer: Deserializer)
    fun getAllDeserializers(): Single<List<Deserializer>>
    fun getDeserializerByFilter(filter: String, type: Deserializer.Type): Single<Deserializer>
    fun getDeserializerById(id: Long): Single<Deserializer>
}