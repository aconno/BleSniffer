package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class BooleanGenerator : ValueGenerator<Boolean>{

    override fun generateValue(valueSize : Int): Boolean {
        return Random.nextBoolean()
    }
}