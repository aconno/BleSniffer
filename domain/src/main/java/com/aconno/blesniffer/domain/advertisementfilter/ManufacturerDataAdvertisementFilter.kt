package com.aconno.blesniffer.domain.advertisementfilter

import com.aconno.blesniffer.domain.util.ByteOperations

class ManufacturerDataAdvertisementFilter : AdvertisementDataFilter {

    override fun filterAdvertisementData(advertisementData: ByteArray): ByteArray {
        return ByteOperations.isolateMsd(advertisementData)
    }

}