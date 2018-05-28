package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.beacon.GeneralBeacon
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Single

/**
 * @author aconno
 */
class ProcessBeaconDataUseCase :
    SingleUseCaseWithParameter<Beacon, ScanResult> {

    override fun execute(parameter: ScanResult): Single<Beacon> =
        Single.just(parameter).map { toBeaconData(it) }

    private fun toBeaconData(scanResult: ScanResult): Beacon {
        return GeneralBeacon(
                scanResult.device.macAddress,
                scanResult.advertisement.rawData.let {
                    val bytes: MutableList<Byte> = mutableListOf()
                    var length: Int = 0
                    for (byte in it) {
                        if (length == 0 && byte == 0x00.toByte()) break
                        if (length == 0) length = byte.toInt() + 1
                        bytes.add(byte)
                        length--
                    }
                    bytes.toByteArray()
                },
                System.currentTimeMillis(),
                scanResult.device.name
        )
    }
}