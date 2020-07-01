package com.aconno.blesniffer.data.deserializing

import android.os.Parcel
import android.os.Parcelable

class ParcelableDeserializerField(
    var name: String,
    var startIndexInclusive: Int,
    var endIndexExclusive: Int,
    var type: String,
    var color: Int,
    var formula: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(startIndexInclusive)
        parcel.writeInt(endIndexExclusive)
        parcel.writeString(type)
        parcel.writeInt(color)
        parcel.writeString(formula)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableDeserializerField> {
        override fun createFromParcel(parcel: Parcel): ParcelableDeserializerField {
            return ParcelableDeserializerField(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableDeserializerField?> {
            return arrayOfNulls(size)
        }
    }

}