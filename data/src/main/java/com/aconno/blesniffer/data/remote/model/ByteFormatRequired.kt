package com.aconno.blesniffer.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ByteFormatRequired(
    @SerializedName("index")
    @Expose
    var index: Int,
    @SerializedName("value")
    @Expose
    var value: String
) : Comparable<ByteFormatRequired> {

    override fun compareTo(other: ByteFormatRequired): Int {
        return this.index.compareTo(other.index)
    }

    override fun toString(): String {
        return this.value
    }
}