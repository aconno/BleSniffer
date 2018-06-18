package com.aconno.blesniffer.domain.model

import java.util.*

data class Advertisement(val rawData: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Advertisement

        if (!Arrays.equals(rawData, other.rawData)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(rawData)
    }
}