package com.aconno.acnsensa.domain

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

enum class ValueConverter(var default: Any, var converter: Converter<*>) {
    BOOLEAN(false, object : Converter<Boolean>(false) {
        override fun fromString(string: String): Boolean {
            return if (string.equals("true", true) || string == "1") true
            else if (string.equals("false", true) || string == "0") false
            else throw IllegalArgumentException("$string is an illegal value for type Boolean")
        }

        override fun serializeInternal(data: Boolean): ByteArray {
            return byteArrayOf(if (data) 0x01 else 0x00)
        }

        override fun deserializeInternal(data: ByteArray): Boolean {
            return when {
                data[0] == 0x01.toByte() -> true
                data[0] == 0x00.toByte() -> false
                else -> throw IllegalArgumentException("${bytesToHex(data)} is an illegal value for type Boolean")
            }
        }
    }),
    BYTE(0, object : Converter<Byte>(0) {
        override fun fromString(string: String): Byte {
            return string.toByte()
        }

        override fun serializeInternal(data: Byte): ByteArray {
            return byteArrayOf(data)
        }

        override fun deserializeInternal(data: ByteArray): Byte {
            return data[0]
        }
    }),
    MAC_ADDRESS(0, object : Converter<String>("00:11:22:33:44:55") {
        override fun serialize(data: String, order: ByteOrder): ByteArray {
            return serializeInternal(fromString(data))
        }

        override fun deserialize(data: ByteArray, order: ByteOrder): String {
            if (data.size != length && length != -1) {
                throw IllegalArgumentException("Invalid buffer length, expected $length, got ${data.size}")
            } else {
                return deserializeInternal(data)
            }
        }

        override fun fromString(string: String): String = string

        override fun serializeInternal(data: String): ByteArray =
                data.split(':').map { it.toByte() }.toList().toByteArray()

        override fun deserializeInternal(data: ByteArray): String =
                data.joinToString(":") { String.format("%02x", it) }
    }),
    SINT8(0, object : Converter<Byte>(0) {
        override fun fromString(string: String): Byte {
            return string.toByte()
        }

        override fun serializeInternal(data: Byte): ByteArray {
            return byteArrayOf(data)
        }

        override fun deserializeInternal(data: ByteArray): Byte {
            return data[0]
        }
    }),
    UINT8(0, object : Converter<Short>(0) {
        override fun fromString(string: String): Short {
            return string.toShort()
        }

        override fun serializeInternal(data: Short): ByteArray {
            return byteArrayOf(if (data >= 128) (data - 256).toByte() else data.toByte())
        }

        override fun deserializeInternal(data: ByteArray): Short {
            val v: Short = data[0].toShort()
            return (if (v < 0) (v + 256).toShort() else v)
        }
    }),
    SINT16(0, object : Converter<Short>(0) {
        override fun fromString(string: String): Short {
            return string.toShort()
        }

        override fun serializeInternal(data: Short): ByteArray {
            return ByteBuffer.allocate(2).putShort(data).array()
        }

        override fun deserializeInternal(data: ByteArray): Short {
            return ByteBuffer.wrap(data, 0, 2).short
        }
    }),
    UINT16(0, object : Converter<Int>(0) {
        override fun fromString(string: String): Int {
            return string.toInt()
        }

        override fun serializeInternal(data: Int): ByteArray {
            return ByteBuffer.allocate(2).putShort((data).and(0xFFFF).toShort()).array()
        }

        override fun deserializeInternal(data: ByteArray): Int {
            val v = ByteBuffer.wrap(data, 0, 2).short
            return (if (v < 0) v.toInt() + 65536 else v.toInt())
        }

    }),
    SINT32(0, object : Converter<Int>(0) {
        override fun fromString(string: String): Int {
            return string.toInt()
        }

        override fun serializeInternal(data: Int): ByteArray {
            return ByteBuffer.allocate(4).putInt(data).array()
        }

        override fun deserializeInternal(data: ByteArray): Int {
            return ByteBuffer.wrap(data, 0, 4).int
        }

    }),
    UINT32(0, object : Converter<Long>(0) {
        override fun fromString(string: String): Long {
            return string.toLong()
        }

        override fun serializeInternal(data: Long): ByteArray {
            return ByteBuffer.allocate(4).putShort((data).and(0xFFFF).toShort()).array()
        }

        override fun deserializeInternal(data: ByteArray): Long {
            val v = ByteBuffer.wrap(data, 0, 4).int
            return (if (v < 0) v.toLong() + 4294967296L else v.toLong())
        }
    }),
    UTF8STRING("", object : Converter<String>("") {
        override fun fromString(string: String): String {
            return string
        }

        override fun serialize(data: String, order: ByteOrder): ByteArray {
            return super.serialize(data, ByteOrder.BIG_ENDIAN)
        }

        override fun serializeInternal(data: String): ByteArray {
            return data.toByteArray(Charset.forName("ASCII"))
        }

        override fun deserializeInternal(data: ByteArray): String {
            return data.toString(Charset.forName("ASCII")).trim(0x00.toChar())
        }

    }),
    TIME(0, object : Converter<Long>(0) {
        override fun fromString(string: String): Long {
            return string.toLong()
        }

        override fun serializeInternal(data: Long): ByteArray {
            return ByteBuffer.allocate(8).putLong(data).array().copyOfRange(0, 6)
        }

        override fun deserializeInternal(data: ByteArray): Long {
            val missingBytes: MutableList<Byte> = mutableListOf(0, 0)
            missingBytes.addAll(data.toTypedArray())
            return ByteBuffer.wrap(missingBytes.toByteArray(), 0, 8).long
        }

    });

    abstract class Converter<T>(val default: T, val length: Int = -1) {
        fun toString(value: T): String {
            return value.toString()
        }

        abstract fun fromString(string: String): T

        open fun serialize(data: String, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
            return serializeInternal(fromString(data)).apply {
                if (order == ByteOrder.LITTLE_ENDIAN) reverse()
            }
        }

        abstract fun serializeInternal(data: T): ByteArray

        open fun deserialize(data: ByteArray, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T {
            if (data.size != length && length != -1) {
                throw IllegalArgumentException("Invalid buffer length, expected $length, got ${data.size}")
            } else {
                data.apply {
                    if (order == ByteOrder.LITTLE_ENDIAN) reverse()
                }
                return deserializeInternal(data)
            }
        }

        abstract fun deserializeInternal(data: ByteArray): T

        fun bytesToHex(`in`: ByteArray): String {
            val builder = StringBuilder()
            for (b in `in`) {
                builder.append(String.format("%02x, ", b))
            }
            return builder.toString()
        }
    }
}