package com.aconno.hexinputlib

import kotlin.math.ceil

object HexUtils {
    const val HEX_CHARS = "0123456789ABCDEF"

    fun hexToBytes(hexValues : List<Char>) : ByteArray {
        val bytes = ByteArray(ceil(hexValues.size/2f).toInt())
        for(i in hexValues.indices step 2) {
            val byte = if(i == hexValues.lastIndex) {
                hexToByte('0',hexValues[i])
            } else {
                hexToByte(hexValues[i],hexValues[i+1])
            }
            bytes[i/2] = byte
        }

        return bytes
    }

    private fun hexToByte(higherOrderHexChar : Char, lowerOrderHexChar : Char) : Byte {
        return (hexCharToDecimal(lowerOrderHexChar) + hexCharToDecimal(higherOrderHexChar)*16).toByte()
    }

    private fun hexCharToDecimal(hexChar : Char) : Int {
        return HEX_CHARS.indexOf(hexChar.toUpperCase())
    }

    fun bytesToHex(values: ByteArray) : List<Char> {
        return values.flatMap { String.format("%02X",it).toList() }
    }
}