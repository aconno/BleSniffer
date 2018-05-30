package com.aconno.acnsensa.domain

/**
 * @aconno
 */
interface JsonFileStorage<T> {
    fun storeItem(item: T, fileName: String): String = storeItems(listOf(item), fileName)

    fun storeItems(items: List<T>, fileName: String): String

    fun readItem(fileName: String): T? = readItems(fileName).let { if (it.isNotEmpty()) it[0] else null }

    fun readItems(fileName: String): List<T>
}