package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class UnsignedInt32Generator : ValueGenerator<Long> {

    override fun generateValue(): Long {
        return Random.nextLong(0,MAX_VALUE)
    }

    companion object {
        const val MAX_VALUE = 4294967296L
    }
}