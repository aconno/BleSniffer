package com.aconno.blesniffer.domain

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

@Throws(IllegalArgumentException::class)
fun String.hexPairToByte(): Byte = toUpperCase().let {
    HEX_CHARS.indexOf(this[0]).let {
        if (it == -1) {
            throw IllegalArgumentException("${this[0]} is not a valid hexadecimal character!")
        } else {
            it
        }
    }.shl(4).or(
        HEX_CHARS.indexOf(this[1]).let {
            if (it == -1) {
                throw IllegalArgumentException("${this[1]} is not a valid hexadecimal character!")
            } else {
                it
            }
        }
    ).toByte()
}