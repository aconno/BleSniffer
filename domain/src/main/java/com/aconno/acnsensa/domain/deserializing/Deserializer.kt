package com.aconno.acnsensa.domain.deserializing

import com.aconno.acnsensa.domain.ValueConverter

/**
 * @author aconno
 */
interface Deserializer {
    val filter: String
    val filterType: Type
    val valueDeserializers: MutableList<Triple<String, Pair<Int, Int>, ValueConverter>>

    enum class Type {
        MAC, DATA
    }
}