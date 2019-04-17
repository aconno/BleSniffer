package com.aconno.blesniffer.domain.deserializing

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

data class GeneralDeserializer(
    override var id: Long? = null,
    override var name: String = "Unnamed",
    override var filter: String = "",
    override var filterType: Deserializer.Type = Deserializer.Type.MAC,
    override val fieldDeserializers: MutableList<FieldDeserializer> = mutableListOf(),
    override var sampleData: ByteArray = byteArrayOf()
) : Deserializer {
    override val pattern: Regex by lazy {
        if (filter.isNotBlank()) {
            try {
                Pattern.compile(filter, Pattern.CASE_INSENSITIVE).toRegex()
            } catch (exception: PatternSyntaxException) {
                Pattern.compile(".*", Pattern.CASE_INSENSITIVE).toRegex()
            }
        } else {
            Pattern.compile(".*", Pattern.CASE_INSENSITIVE).toRegex()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeneralDeserializer

        if (id != other.id) return false
        if (name != other.name) return false
        if (filter != other.filter) return false
        if (filterType != other.filterType) return false
        if (fieldDeserializers != other.fieldDeserializers) return false
        if (!sampleData.contentEquals(other.sampleData)) return false
        if (pattern != other.pattern) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + filter.hashCode()
        result = 31 * result + filterType.hashCode()
        result = 31 * result + fieldDeserializers.hashCode()
        result = 31 * result + sampleData.contentHashCode()
        result = 31 * result + pattern.hashCode()
        return result
    }
}