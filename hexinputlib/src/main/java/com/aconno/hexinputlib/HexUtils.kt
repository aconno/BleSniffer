package com.aconno.hexinputlib

import kotlin.math.ceil

/**
 * An object providing some util methods for dealing with hexadecimal content. This methods are
 * intended to be used for implementation of a [HexFormatter][com.aconno.hexinputlib.formatter.HexFormatter]
 * (e.g. when implementing a custom formatter) but they could also be used anywhere else if there is a need to.
 */
object HexUtils {
    /**
     * A string containing all 16 hexadecimal values, sorted ascending.
     */
    const val HEX_CHARS = "0123456789ABCDEF"

    /**
     * Converts [hexValues] to a byte array, i.e. interprets each pair of hex values as one byte.
     * For example, this method would convert values [F,8,B,C,A,3] to a byte array [0xF8,0xBC,0xA3].
     * Another example is when there is an odd number of hex values: this method would convert values
     * [F,8,B,C,A] to a byte array [0xF8,0xBC,0x0A].
     *
     * @param hexValues list of hex values
     * @return a byte array created as a result of conversion from [hexValues]
     */
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

    /**
     * Converts the specified byte array to a list of hex values. For example, this method would
     * convert byte array [0xF8,0xBC,0xA3] to a list of values [F,8,B,C,A,3].
     *
     * @param values a byte array
     * @return list of hex values created as a result of conversion from [values]
     */
    fun bytesToHex(values: ByteArray) : List<Char> {
        return values.flatMap { String.format("%02X",it).toList() }
    }
}