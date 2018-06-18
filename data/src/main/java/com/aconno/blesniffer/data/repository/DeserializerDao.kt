package com.aconno.blesniffer.data.repository

import android.arch.persistence.room.*
import io.reactivex.Single

/**
 * @author aconno
 */
@Dao
abstract class DeserializerDao {

    @get:Query("SELECT * FROM deserializers")
    abstract val all: Single<List<DeserializerEntity>>

    @Query("SELECT * FROM deserializers WHERE filter=:filter AND filterType=:type")
    abstract fun getDeserializerByFilter(filter: String, type: String): Single<DeserializerEntity>

    @Query("SELECT * FROM deserializers WHERE id=:id")
    abstract fun getDeserializerById(id: Long): Single<DeserializerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(beacon: DeserializerEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(beacon: DeserializerEntity)

    @Delete
    abstract fun delete(beacon: DeserializerEntity)
}