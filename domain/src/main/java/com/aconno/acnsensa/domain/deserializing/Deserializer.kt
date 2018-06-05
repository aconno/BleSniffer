package com.aconno.acnsensa.domain.deserializing

import java.util.regex.Pattern

/**
 * @author aconno
 */
interface Deserializer {
    val filter: String
    var filterType: Type
    val fieldDeserializers: MutableList<FieldDeserializer>
    val pattern: Regex

    enum class Type {
        MAC, DATA
    }
}