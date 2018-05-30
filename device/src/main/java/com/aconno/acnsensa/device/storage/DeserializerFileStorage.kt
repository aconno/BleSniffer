package com.aconno.acnsensa.device.storage

import android.content.Context
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.FieldDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralFieldDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class DeserializerFileStorage(
        context: Context
) : JsonFileStorageImpl<Deserializer, GeneralDeserializer>(context, object : TypeToken<List<GeneralDeserializer>>() {}) {
    override val gson: Gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(FieldDeserializer::class.java, GenericDeserializer<GeneralFieldDeserializer>())
                .create()
    }
}