package com.aconno.acnsensa.domain.deserializing

/**
 * @author aconno
 */
interface Deserializer {
    val filter: String
    val filterType: Type
    val fieldDeserializers: MutableList<FieldDeserializer>

    enum class Type {
        MAC, DATA
    }
}