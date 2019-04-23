package com.aconno.blesniffer

import androidx.test.runner.AndroidJUnit4
import android.util.Log
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        var start = System.currentTimeMillis()
        val testvar = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05)
        for (i in 1..10000000) {
            var time: Long = 0
            for (i in 5 downTo 0) {
                time = time or (testvar[i].toLong() shl ((5 - i) * 8))
            }
        }
        Log.e("TEST", (System.currentTimeMillis() - start).toString())
        start = System.currentTimeMillis()
        for (i in 1..10000000) {
            ByteBuffer.wrap(byteArrayOf(0, 0) + testvar, 0, 8).long
        }
        Log.e("TEST", (System.currentTimeMillis() - start).toString())
    }
}
