package com.aconno.blesniffer.domain.model

import java.util.*

data class ScanResult(val device: Device, val advertisement: Advertisement, var timestamp: Long, var rssi: Int) {
    override fun hashCode(): Int {
        var result = device.macAddress.hashCode()
        result = 31 * result + Arrays.hashCode(advertisement.rawData)
        return result
    }
}