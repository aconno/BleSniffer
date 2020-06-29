package com.aconno.blesniffer.domain.deserializing


/**
 * @author aconno
 */
interface Deserializer {
    var id: Long?
    var name: String
    var filter: String
    var filterType: Type
    val fieldDeserializers: MutableList<FieldDeserializer>
    val pattern: Regex
    var sampleData: ByteArray

    enum class Type {
        MAC, DATA
    }
}