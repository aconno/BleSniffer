package com.aconno.blesniffer.domain.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class ScanResult(val device: Device, val advertisement: Advertisement, var timestamp: Long, var rssi: Int, var timeFromLastTimestamp: Long = -1) : Parcelable {

    constructor(parcel: Parcel) : this(
        Device(parcel.readString() ?: "",parcel.readString() ?: ""),
        Advertisement(ByteArray(parcel.readInt()).apply { parcel.readByteArray(this) }),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readLong()
    )

    override fun hashCode(): Int {
        var result = device.macAddress.hashCode()
        result = 31 * result + Arrays.hashCode(advertisement.rawData)
        return result
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(device.name)
        dest.writeString(device.macAddress)
        dest.writeInt(advertisement.rawData.size)
        dest.writeByteArray(advertisement.rawData)
        dest.writeLong(timestamp)
        dest.writeInt(rssi)
        dest.writeLong(timeFromLastTimestamp)
    }

    companion object CREATOR : Parcelable.Creator<ScanResult> {
        override fun createFromParcel(parcel: Parcel): ScanResult {
            return ScanResult(parcel)
        }

        override fun newArray(size: Int): Array<ScanResult?> {
            return arrayOfNulls(size)
        }
    }




}