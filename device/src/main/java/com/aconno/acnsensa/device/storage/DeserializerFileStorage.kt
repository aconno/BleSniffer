package com.aconno.acnsensa.device.storage

import android.content.Context
import com.aconno.acnsensa.device.bluetooth.toHex
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.FieldDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralFieldDeserializer
import com.google.common.io.BaseEncoding
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class DeserializerFileStorage(
        context: Context
) : JsonFileStorageImpl<Deserializer, GeneralDeserializer>(context, object : TypeToken<List<GeneralDeserializer>>() {}) {
    override val gson: Gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(FieldDeserializer::class.java, JsonDeserializer<GeneralFieldDeserializer> { json, typeOfT, context ->
                    context.deserialize<GeneralFieldDeserializer>(json, (object : TypeToken<GeneralFieldDeserializer>() {}).type)
                })
                .registerTypeAdapter(ByteArray::class.java, object : JsonSerializer<ByteArray>, JsonDeserializer<ByteArray> {
                    override fun serialize(src: ByteArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
                        return JsonPrimitive(src.toHex())
                    }

                    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ByteArray {
                        return BaseEncoding.base16().decode(json.asString.replace("0x", "").replace(" ", "").toUpperCase())
                    }
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
                        json.add("sampleData", context.serialize(src.sampleData))
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
                        json.add("sampleData", context.serialize(src.sampleData))
                    }
                    json
                })
                .create()
    }
}