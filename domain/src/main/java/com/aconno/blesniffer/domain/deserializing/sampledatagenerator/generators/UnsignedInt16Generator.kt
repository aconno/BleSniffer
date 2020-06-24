package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

class UnsignedInt16Generator : UnsignedIntGenerator<Int>(0) {

    override fun getMaxValue(): Long {
        return 65535
    }


}