package com.aconno.acnsensa.domain

import io.reactivex.Single

/**
 * @aconno
 */
interface JsonFileStorage<T> {
    fun storeItem(item: T, fileName: String): String = storeItems(listOf(item), fileName)

    fun storeItems(items: List<T>, fileName: String): String

    fun readItem(fileName: String): Single<T?> {
        TODO("Reimplement")
    }

    fun readItems(fileName: String): Single<List<T>>
}