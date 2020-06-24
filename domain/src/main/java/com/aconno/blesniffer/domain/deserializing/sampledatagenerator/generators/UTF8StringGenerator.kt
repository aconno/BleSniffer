package com.aconno.blesniffer.domain.deserializing.sampledatagenerator.generators

import java.lang.StringBuilder

class UTF8StringGenerator : ValueGenerator<String>{

    override fun generateValue(valueSize : Int): String {
        val stringBuilder = StringBuilder()
        for(i in 0 until valueSize / TEXT_SAMPLE.length) {
            stringBuilder.append(TEXT_SAMPLE)
        }

        stringBuilder.append(TEXT_SAMPLE.subSequence(0,valueSize % TEXT_SAMPLE.length))

        return stringBuilder.toString()
    }

    companion object {
        const val TEXT_SAMPLE = "text"
    }

}