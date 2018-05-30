package com.aconno.acnsensa

import android.support.test.runner.AndroidJUnit4
import com.aconno.acnsensa.device.storage.JsonFileStorageImpl
import com.aconno.acnsensa.domain.JsonFileStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JsonFileStorageTest {
    val storage: JsonFileStorage<String> = object : JsonFileStorageImpl<String, String>("AcnSensaUnitTest", object : TypeToken<List<String>>() {}) {
        override val gson: Gson
            get() = Gson()

    }

    @Test
    fun testListStorage() {
        val list: List<String> = listOf("a", "b", "c")
        val fileName = "test.json"
        storage.storeItems(list, fileName)
        val storedList: List<String> = storage.readItems(fileName)

        assert(list == storedList)
    }
}