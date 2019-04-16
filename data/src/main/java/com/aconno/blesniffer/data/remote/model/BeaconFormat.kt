package com.aconno.blesniffer.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BeaconFormat(
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("format_required")
    @Expose
    var formatsRequired: List<ByteFormatRequired>,
    @SerializedName("format")
    @Expose
    var dataFormats: List<ByteFormat>,
    @SerializedName("settings_supoort")
    @Expose
    var settingsSupport: BeaconSettingsSupport?
)