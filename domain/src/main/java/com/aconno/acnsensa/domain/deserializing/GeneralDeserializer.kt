package com.aconno.acnsensa.domain.deserializing

import java.util.regex.Pattern

data class GeneralDeserializer(
        override var id: Long? = null,
        override val filter: String = "",
        override var filterType: Deserializer.Type = Deserializer.Type.MAC,
        override val fieldDeserializers: MutableList<FieldDeserializer> = mutableListOf()
) : Deserializer {
    override val pattern: Regex = Pattern.compile(filter).toRegex()
}