package com.aconno.blesniffer.domain.deserializing

import com.aconno.blesniffer.domain.ValueConverter

data class GeneralFieldDeserializer(
        override var name: String = "",
        override var startIndexInclusive: Int = 0,
        override var endIndexExclusive: Int = 0,
        override var type: ValueConverter = ValueConverter.BOOLEAN,
        override var color: Int = -3407872
) : FieldDeserializer