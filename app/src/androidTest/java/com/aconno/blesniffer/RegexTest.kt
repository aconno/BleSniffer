package com.aconno.blesniffer

import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RegexTest {
    @Test
    fun useAppContext() {
        val t = Pattern.compile("0x02 0x01 0x06 0x13 0xff 0xa6 0x02 0x00 .*").toRegex()
        assert("0x02 0x01 0x06 0x13 0xff 0xa6 0x02 0x00 0x01 0x02 0x03".matches(t))
    }
}
