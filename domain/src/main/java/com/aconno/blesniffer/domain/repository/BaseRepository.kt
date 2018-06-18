package com.aconno.blesniffer.domain.repository

import io.reactivex.Single

interface BaseRepository<T : BaseModel<Y>, Y> {
    fun add(item: T)
    fun update(item: T)
    fun delete(item: T)
    fun getByKey(key: Y): Single<T>
    fun getAll(): Single<List<T>>
}