package com.aconno.acnsensa.device.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

fun ByteArray.toHex() = this.joinToString(separator = "") { "0x" + it.toInt().and(0xff).toString(16).padStart(2, '0') + " " }
//TODO: This needs refactoring.
class BluetoothScanCallback(
        val scanResults: PublishSubject<ScanResult>,
        val scanEvents: PublishSubject<ScanEvent>
) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
        super.onScanResult(callbackType, result)
        val scanResult = createScanResult(result)
        scanResults.onNext(scanResult)
    }

    @SuppressLint("MissingPermission")
    private fun createScanResult(result: android.bluetooth.le.ScanResult?): ScanResult {
        val device = result?.device
        val deviceName = device?.name ?: "Unknown"
        val deviceAddress = device?.address ?: "Unknown"
        val scannedDevice = Device(deviceName, deviceAddress)
        val bytes = (result?.scanRecord?.bytes ?: byteArrayOf())
        val advertisement = Advertisement(trimRawData(bytes))
        val timestamp = System.currentTimeMillis()

        return ScanResult(scannedDevice, advertisement, timestamp)
    }

    private fun trimRawData(rawData: ByteArray): ByteArray {
        val bytes: MutableList<Byte> = mutableListOf()
        var length: Int = 0
        for (byte in rawData) {
            if (length == 0 && byte == 0x00.toByte()) break
            if (length == 0) length = byte.toInt() + 1
            bytes.add(byte)
            length--
        }
        return bytes.toByteArray()
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Timber.e("Scan failed with error code %d", errorCode)
        when (errorCode) {
            SCAN_FAILED_ALREADY_STARTED ->
                scanEvents.onNext(
                        ScanEvent(
                                ScanEvent.SCAN_FAILED_ALREADY_STARTED,
                                "Scan Failed with error code $errorCode"
                        )
                )
            else ->
                scanEvents.onNext(
                        ScanEvent(ScanEvent.SCAN_FAILED, "Scan failed with error code $errorCode")
                )
        }
    }
}