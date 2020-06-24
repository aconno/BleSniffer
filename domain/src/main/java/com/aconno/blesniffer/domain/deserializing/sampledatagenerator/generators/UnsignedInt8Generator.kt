package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

class UnsignedInt8Generator : UnsignedIntGenerator<Short>(0.toShort()) {

    override fun getMaxValue(): Long {
        return 255
    }
}