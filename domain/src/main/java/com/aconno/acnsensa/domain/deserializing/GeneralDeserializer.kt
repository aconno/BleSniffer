package com.aconno.acnsensa.domain.deserializing

import com.aconno.acnsensa.domain.ValueConverter

class GeneralDeserializer(
        override val filter: String,
        override val filterType: Deserializer.Type,
        override val valueDeserializers: MutableList<Triple<String, Pair<Int, Int>, ValueConverter>>
) : Deserializer