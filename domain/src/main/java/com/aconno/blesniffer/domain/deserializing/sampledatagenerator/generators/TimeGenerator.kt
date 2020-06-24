package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class TimeGenerator : ValueGenerator<Long> {

    override fun generateValue(valueSize : Int): Long {
        return Random.nextLong(0,System.currentTimeMillis())
    }
}