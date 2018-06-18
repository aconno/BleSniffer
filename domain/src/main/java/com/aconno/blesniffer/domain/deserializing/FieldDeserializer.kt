package com.aconno.blesniffer.domain.deserializing

import com.aconno.blesniffer.domain.ValueConverter

interface FieldDeserializer {
    var name: String
    var startIndexInclusive: Int
    var endIndexExclusive: Int
    var type: ValueConverter
    var color: Int
}