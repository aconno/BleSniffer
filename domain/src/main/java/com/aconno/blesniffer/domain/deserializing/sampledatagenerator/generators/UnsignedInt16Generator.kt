package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

class UnsignedInt16Generator : ValueGenerator<Int> {

    override fun generateValue(): Int {
        return Random.nextInt(0,MAX_VALUE)
    }

    companion object {
        const val MAX_VALUE = 65535
    }


}