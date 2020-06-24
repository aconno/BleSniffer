package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class UnsignedInt8Generator : ValueGenerator<Short> {

    override fun generateValue(): Short {
        return Random.nextInt(0,MAX_VALUE).toShort()
    }

    companion object {
        const val MAX_VALUE = 255
    }
}