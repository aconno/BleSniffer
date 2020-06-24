package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.math.abs
import kotlin.random.Random

class SignedInt32Generator : ValueGenerator<Int> {

    override fun generateValue(): Int {
        return abs(Random.nextInt())
    }
}