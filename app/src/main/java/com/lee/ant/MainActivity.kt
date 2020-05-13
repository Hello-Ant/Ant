package com.lee.ant

import android.os.Build
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.app_activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_main)

        sv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                sv.isScrollbarFadingEnabled = true
            }
        })
        ivTest.setOnClickListener { AdDialog.getInstance().showAd(supportFragmentManager) }
    }

}
