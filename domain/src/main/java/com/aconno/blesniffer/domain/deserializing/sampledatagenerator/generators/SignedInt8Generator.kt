package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class SignedInt8Generator : ValueGenerator<Byte> {

    override fun generateValue(valueSize : Int): Byte {
        return Random.nextInt(Byte.MAX_VALUE + 1).toByte()
    }
}