package com.aconno.acnsensa.device.storage

import com.google.gson.*
import java.lang.reflect.Type


class GenericDeserializer<T> : JsonDeserializer<T> {
    val gson: Gson by lazy { Gson() }

    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): T {
        val content = je.asJsonObject.get("content")
        return gson.fromJson(content, type)
    }
}