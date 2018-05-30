package com.aconno.acnsensa.device.storage

import android.content.Context
import android.os.Environment
import com.aconno.acnsensa.device.R
import com.aconno.acnsensa.domain.JsonFileStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


class JsonFileStorageImpl<T>(storageDirectoryName: String) : JsonFileStorage<T> {

    private val typeToken: TypeToken<List<T>> = object : TypeToken<List<T>>() {}
    private val storageDirectory: File = File(
            "${Environment.getExternalStorageDirectory().absolutePath}/$storageDirectoryName"
    )
    private val gson: Gson by lazy { Gson() }

    constructor(context: Context) : this(context.getString(R.string.app_name))

    init {
        if (!storageDirectory.exists()) storageDirectory.mkdirs()
    }


    override fun storeItems(items: List<T>, fileName: String) {
        val file = File(storageDirectory, fileName)
        file.writeText(gson.toJson(items))
    }

    override fun readItems(fileName: String): List<T> {
        val file = File(storageDirectory, fileName)
        return gson.fromJson(file.readText(), typeToken.type)
    }
}