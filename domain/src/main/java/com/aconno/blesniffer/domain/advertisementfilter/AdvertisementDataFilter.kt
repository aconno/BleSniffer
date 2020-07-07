package com.aconno.blesniffer.domain.advertisementfilter

interface AdvertisementDataFilter {
    fun filterAdvertisementData(advertisementData : ByteArray) : ByteArray
}