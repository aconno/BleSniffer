package com.aconno.blesniffer

import android.support.test.runner.AndroidJUnit4
import com.aconno.blesniffer.device.storage.JsonFileStorageImpl
import com.aconno.blesniffer.domain.JsonFileStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class JsonFileStorageTest {
    val storage: JsonFileStorage<String> = object : JsonFileStorageImpl<String, String>("BleSnifferUnitTest", object : TypeToken<List<String>>() {}) {
        override val gson: Gson
            get() = Gson()

    }

    @Test
    fun testListStorage() {
        val list: List<String> = listOf("a", "b", "c")
        val fileName = "test.json"
        storage.storeItems(list, fileName)
        storage.readItems(File(fileName).inputStream()).subscribe { it ->
            assert(list == it)
        }

    }
}