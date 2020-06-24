package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import kotlin.random.Random

abstract class UnsignedIntGenerator<T : Number>(private val sampleValue : T) : ValueGenerator<T> {

    override fun generateValue(): T {
        return Random.nextLong(0,getMaxValue() + 1).to(sampleValue).second
    }

    abstract fun getMaxValue() : Long
}
