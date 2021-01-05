package com.lee.ant

import android.os.Handler
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.lee.ant", appContext.packageName)
    }

    @Test
    fun testLooper() {
        Thread {
            Looper.prepare()

            val h = Handler(Looper.myLooper()) { msg ->
                println("handleMessage what = ${msg.what}")
                true
            }

            h.sendEmptyMessage(1)
            println("start sleep")
            Thread.sleep(1000)
            println("end sleep, start loop")
            h.sendEmptyMessage(2)
            Looper.loop()
        }.start()

        Thread.sleep(10000)
    }
}
