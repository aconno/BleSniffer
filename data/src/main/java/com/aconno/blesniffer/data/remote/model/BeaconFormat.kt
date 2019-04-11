package com.aconno.blesniffer.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BeaconFormat(
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("format_required")
        @Expose
        var formatRequired: List<ByteFormatRequired>
)