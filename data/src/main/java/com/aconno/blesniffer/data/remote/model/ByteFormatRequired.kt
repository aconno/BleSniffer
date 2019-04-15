package com.aconno.blesniffer.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ByteFormatRequired(
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("index")
        @Expose
        var index: Int,
        @SerializedName("value")
        @Expose
        var value: String
)