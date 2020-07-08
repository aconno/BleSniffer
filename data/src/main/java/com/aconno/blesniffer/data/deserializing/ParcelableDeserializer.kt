package com.aconno.blesniffer.data.deserializing

import android.os.Parcel
import android.os.Parcelable
import com.aconno.blesniffer.domain.ValueConverter
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.FieldDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer

class ParcelableDeserializer() : Deserializer, Parcelable {
    override var id: Long? = null
    override lateinit var name: String
    override lateinit var filter: String
    override lateinit var filterType: Deserializer.Type
    override lateinit var fieldDeserializers: MutableList<FieldDeserializer>
    override lateinit var pattern: Regex
    override lateinit var sampleData: ByteArray

    constructor(deserializer: Deserializer) : this() {
        id = deserializer.id
        name = deserializer.name
        filter = deserializer.filter
        filterType = deserializer.filterType
        fieldDeserializers = deserializer.fieldDeserializers
        pattern = deserializer.pattern
        sampleData = deserializer.sampleData
    }

    constructor(parcel: Parcel) : this() {
        val id = parcel.readLong()
        this.id = if(id == -1L) null else id

        name = parcel.readString() ?: ""
        filter = parcel.readString() ?: ""
        filterType = parcel.readSerializable() as Deserializer.Type

        val fieldsParcelable = parcel.readParcelableArray(ParcelableDeserializerField::class.java.classLoader)
        fieldDeserializers = fieldsParcelable?.map {
            val field = it as ParcelableDeserializerField
            GeneralFieldDeserializer(
                field.name,
                field.startIndexInclusive,
                field.endIndexExclusive,
                ValueConverter.valueOf(field.type),
                field.color,
                field.formula
            ) as FieldDeserializer
        }?.toMutableList() ?: mutableListOf()

        pattern = parcel.readSerializable() as Regex

        val fieldsNumber = parcel.readInt()
        val fieldsArray = ByteArray(fieldsNumber)
        parcel.readByteArray(fieldsArray)
        sampleData = fieldsArray
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id ?: -1)
        parcel.writeString(name)
        parcel.writeString(filter)
        parcel.writeSerializable(filterType)

        val fields = fieldDeserializers.map {
            ParcelableDeserializerField(it.name,it.startIndexInclusive,it.endIndexExclusive,it.type.name,it.color,it.formula)
        }.toTypedArray()
        parcel.writeParcelableArray(fields, 0)

        parcel.writeSerializable(pattern)
        parcel.writeInt(sampleData.size)
        parcel.writeByteArray(sampleData)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableDeserializer> {
        override fun createFromParcel(parcel: Parcel): ParcelableDeserializer {
            return ParcelableDeserializer(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableDeserializer?> {
            return arrayOfNulls(size)
        }
    }


}