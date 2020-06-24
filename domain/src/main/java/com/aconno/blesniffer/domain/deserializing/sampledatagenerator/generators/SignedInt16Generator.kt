package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.math.abs
import kotlin.random.Random

class SignedInt16Generator : SignedIntGenerator<Short>(){

    override fun generateValue(): Short {
        return abs(Random.nextInt(Short.MAX_VALUE + 1)).toShort()
    }

}