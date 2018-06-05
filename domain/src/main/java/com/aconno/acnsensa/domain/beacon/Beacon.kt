package com.aconno.acnsensa.domain.beacon

/**
 * @author aconno
 */
interface Beacon {
    val address: String
    var name: String
    var advertisementData: ByteArray
    var lastseen: Long
}