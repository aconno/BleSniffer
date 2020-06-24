package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.math.abs
import kotlin.random.Random

class FloatGenerator : ValueGenerator<Float> {
    override fun generateValue(valueSize : Int): Float {
        return abs(Random.nextFloat())
    }
}