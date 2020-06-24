package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.random.Random

class MacAddressGenerator : ValueGenerator<String> {

    override fun generateValue(valueSize : Int): String {
        val builder = StringBuilder()
        for(i in 0 until 6) {
            if(i>0) {
                builder.append(":")
            }
            for(j in 0 until 2) {
                builder.append(getRandomHexChar())
            }
        }

        return builder.toString()
    }

    private fun getRandomHexChar() : Char {
        return HEX_CHARS[abs(Random.nextInt()) % HEX_CHARS.length]
    }

    companion object {
        const val HEX_CHARS = "0123456789ABCDEF"
    }
}