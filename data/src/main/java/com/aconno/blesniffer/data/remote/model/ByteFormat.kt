package com.aconno.blesniffer.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ByteFormat(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("start_index_inclusive")
    @Expose
    var startIndexInclusive: Int,
    @SerializedName("end_index_exclusive")
    @Expose
    var endIndexExclusive: Int,
    @SerializedName("data_type")
    @Expose
    var dataType: String,
    @SerializedName("formula")
    @Expose
    var formula: String?
)