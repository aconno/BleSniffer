package com.aconno.blesniffer.domain.deserializing

import com.aconno.blesniffer.domain.model.Device

interface DeserializerFinder {
    fun findDeserializerForDevice(deserializers : List<Deserializer>, device: Device, deviceAdvertisement : ByteArray) : Deserializer?
}
