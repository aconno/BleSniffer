package com.aconno.blesniffer.device.storage

import com.google.gson.*
import java.lang.reflect.Type


class GenericDeserializer<T> : JsonDeserializer<T> {
    val gson: Gson by lazy { Gson() }

    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): T {
        return jdc.deserialize(je, type)
    }
}