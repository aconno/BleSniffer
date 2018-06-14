package com.aconno.acnsensa.domain.deserializing

/**
 * @author aconno
 */
interface Deserializer {
    var id: Long?
    var name: String
    val filter: String
    var filterType: Type
    val fieldDeserializers: MutableList<FieldDeserializer>
    val pattern: Regex
    var sampleData: ByteArray

    enum class Type {
        MAC, DATA
    }
}