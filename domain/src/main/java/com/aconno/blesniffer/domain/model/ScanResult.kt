package com.aconno.blesniffer.domain.model

data class ScanResult(val device: Device, val advertisement: Advertisement, var timestamp: Long, var rssi: Int,var timeFromLastTimestamp: Long = -1) {

    override fun hashCode(): Int {
        var result = device.macAddress.hashCode()
        result = 31 * result + advertisement.rawData.contentHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}