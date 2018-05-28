package com.aconno.acnsensa.domain.beacon

import com.aconno.acnsensa.domain.ifttt.outcome.Outcome

/**
 * @author aconno
 */
interface Beacon {
    val address: String
    var name: String
    var advertisementData: ByteArray
    var lastseen: Long
}