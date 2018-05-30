package com.aconno.acnsensa.device.storage

import android.content.Context
import android.os.Environment
import com.aconno.acnsensa.device.R
import com.aconno.acnsensa.domain.JsonFileStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


abstract class JsonFileStorageImpl<T, K : T>(
        private var storageDirectoryName: String,
        private var typeToken: TypeToken<List<K>>
) : JsonFileStorage<T> {
    private val storageDirectory: File = File(
            "${Environment.getExternalStorageDirectory().absolutePath}/$storageDirectoryName"
    )
    abstract val gson: Gson

    constructor(context: Context, typeToken: TypeToken<List<K>>) : this(context.getString(R.string.app_name), typeToken)

    init {
        if (!storageDirectory.exists()) storageDirectory.mkdirs()
    }

    override fun storeItems(items: List<T>, fileName: String): String {
        val file = File(storageDirectory, fileName)
        file.writeText(gson.toJson(items))
        return file.absolutePath
    }

    override fun readItems(fileName: String): List<T> {
        val file = File(fileName)
        return gson.fromJson(file.readText(), typeToken.type)
    }
}