package com.aconno.blesniffer.device.storage

import android.content.Context
import android.os.Environment
import com.aconno.blesniffer.device.R
import com.aconno.blesniffer.domain.JsonFileStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import io.reactivex.Single
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


val utf8charset: Charset = Charset.forName("UTF-8")

abstract class JsonFileStorageImpl<T, K : T>(
        storageDirectoryName: String,
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

    override fun readItems(inputStream: InputStream, charset: String): Single<List<T>> {
        return try {
            Single.just(gson.fromJson(JsonReader(InputStreamReader(inputStream, charset)), typeToken.type))
        } catch (e: Exception) {
            Single.error<List<T>>(e)
        }
    }
}