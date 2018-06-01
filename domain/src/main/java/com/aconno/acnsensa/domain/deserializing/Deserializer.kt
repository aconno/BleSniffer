package com.aconno.acnsensa.domain.deserializing

/**
 * @author aconno
 */
interface Deserializer {
    val filter: String
    var filterType: Type
    val fieldDeserializers: MutableList<FieldDeserializer>

    enum class Type {
        MAC, DATA
    }
}