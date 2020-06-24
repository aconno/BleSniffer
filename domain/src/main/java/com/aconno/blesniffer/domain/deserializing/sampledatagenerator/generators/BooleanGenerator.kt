package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class BooleanGenerator : ValueGenerator<Boolean>{

    override fun generateValue(): Boolean {
        return Random.nextBoolean()
    }
}