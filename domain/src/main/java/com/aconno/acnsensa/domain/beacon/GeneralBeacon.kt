package com.aconno.acnsensa.domain.beacon

import java.util.*

/**
 * @author aconno
 */
class GeneralBeacon(
        override val address: String,
        override var advertisementData: ByteArray,
        override var lastseen: Long,
        override var name: String
) : Beacon {
    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + Arrays.hashCode(advertisementData)
        result = 31 * result + name.hashCode()
        return result
    }

}