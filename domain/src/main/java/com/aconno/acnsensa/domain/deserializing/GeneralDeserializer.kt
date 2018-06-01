package com.aconno.acnsensa.domain.deserializing

data class GeneralDeserializer(
        override val filter: String = "",
        override var filterType: Deserializer.Type = Deserializer.Type.MAC,
        override val fieldDeserializers: MutableList<FieldDeserializer> = mutableListOf()
) : Deserializer