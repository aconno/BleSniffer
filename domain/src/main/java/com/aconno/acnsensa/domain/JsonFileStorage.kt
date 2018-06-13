package com.aconno.acnsensa.domain

import io.reactivex.Single
import java.io.InputStream

/**
 * @aconno
 */
interface JsonFileStorage<T> {
    fun storeItem(item: T, fileName: String): String = storeItems(listOf(item), fileName)

    fun storeItems(items: List<T>, fileName: String): String

    fun readItem(inputStream: InputStream, charset: String = "UTF-8"): Single<T?> {
        TODO("Reimplement")
    }

    fun readItems(inputStream: InputStream, charset: String = "UTF-8"): Single<List<T>>
}