package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

interface ValueGenerator<T> {
    fun generateValue() : T
}