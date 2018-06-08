package com.aconno.acnsensa.device.storage

import android.content.Context
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.FieldDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralFieldDeserializer
import com.google.gson.*
import com.google.gson.reflect.TypeToken


class DeserializerFileStorage(
        context: Context
) : JsonFileStorageImpl<Deserializer, GeneralDeserializer>(context, object : TypeToken<List<GeneralDeserializer>>() {}) {
    override val gson: Gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(FieldDeserializer::class.java, JsonDeserializer<GeneralFieldDeserializer> { json, typeOfT, context ->
                    context.deserialize<GeneralFieldDeserializer>(json, (object : TypeToken<GeneralFieldDeserializer>() {}).type)
                })
                .registerTypeAdapter(Deserializer::class.java, JsonSerializer<GeneralDeserializer> { src, typeOfSrc, context ->
                    val json = JsonObject()
                    src?.let {
                        json.addProperty("name", src.name)
                        json.addProperty("filter", src.filter)
                        json.addProperty("filterType", src.filterType.name)
                        json.add("fieldDeserializers", JsonArray().apply {
                            src.fieldDeserializers.map { context.serialize(it) }.forEach { this.add(it) }
                        })
                    }

                    json
                }).registerTypeAdapter(GeneralDeserializer::class.java, JsonSerializer<GeneralDeserializer> { src, typeOfSrc, context ->
                    val json = JsonObject()
                    src?.let {
                        json.addProperty("name", src.name)
                        json.addProperty("filter", src.filter)
                        json.addProperty("filterType", src.filterType.name)
                        json.add("fieldDeserializers", JsonArray().apply {
                            src.fieldDeserializers.map { context.serialize(it) }.forEach { this.add(it) }
                        })
                    }

                    json
                })
                .create()
    }
}