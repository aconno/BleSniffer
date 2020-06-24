package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

class Unsigned32Generator : UnsignedIntGenerator<Long>(0L) {

    override fun getMaxValue(): Long {
        return 4294967296L
    }
}