package com.aconno.acnsensa.domain.deserializing

import com.aconno.acnsensa.domain.ValueConverter

interface FieldDeserializer {
    var name: String
    var startIndexInclusive: Int
    var endIndexExclusive: Int
    var type: ValueConverter
    var color: Int
}