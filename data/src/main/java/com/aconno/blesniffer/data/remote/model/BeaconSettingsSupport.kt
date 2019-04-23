package com.aconno.blesniffer.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BeaconSettingsSupport(
    @SerializedName("mask")
    @Expose
    var mask: String,
    @SerializedName("index")
    @Expose
    var index: Int
)