package com.aconno.blesniffer.domain

import android.net.Uri
import io.reactivex.Single
import java.io.InputStream

/**
 * @aconno
 */
interface JsonFileStorage<T> {
    fun storeItem(item: T, fileUri: Uri): String = storeItems(listOf(item), fileUri)

    fun storeItems(items: List<T>, fileUri: Uri): String

    fun readItem(inputStream: InputStream, charset: String = "UTF-8"): Single<T?> {
        TODO("Reimplement")
    }

    fun readItems(inputStream: InputStream, charset: String = "UTF-8"): Single<List<T>>
}