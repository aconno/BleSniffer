package com.aconno.acnsensa.domain.beacon

/**
 * @author aconno
 */
class GeneralBeacon(
    override val address: String,
    override var advertisementData: ByteArray,
    override var lastseen: Long,
    override var name: String
) : Beacon