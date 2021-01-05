package com.lee.ant

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.app_activity_test.*

class MainActivity : AppCompatActivity() {
    var maxHeight = 0
    val mAnimator: ValueAnimator by lazy {
        ValueAnimator.ofInt(vTop.height, 0).apply {
            duration = 3000
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                Log.e("Ant", ": $value")
                /*val lp = vTop.layoutParams
                lp.height = value
                vTop.layoutParams = lp*/
                val translationY = value.toFloat() - maxHeight
                llHello.translationY = translationY
                svContent.translationY = translationY
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    Log.e("Ant", ": ${vTop.height}")
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_test)

        tvHello.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (maxHeight == 0) {
                    maxHeight = vTop.height
                }
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    mAnimator.cancel()
                    mAnimator.setFloatValues(vTop.height.toFloat(), 0f)
                    mAnimator.start()
                    return true
                }
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    mAnimator.cancel()
                    mAnimator.reverse()
                    return true
                }
                return false
            }
        })

        ivBackground.setOnClickListener {
            Glide.with(this)
                .load("http://125.210.163.207:8080/api/coupon/pos/verifyCode?stbId=030183762A8BD3A9F4568")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.mipmap.ic_background)
                .into(ivBackground)
        }

        /*sv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                sv.isScrollbarFadingEnabled = true
            }
        })
        ivTest.setOnClickListener { AdDialog.getInstance().showAd(supportFragmentManager) }*/
    }

}

/*
fun main() {
    var dd: Disposable? = null
    Observable.create(ObservableOnSubscribe<String> {
        println("subscribe: ${Thread.currentThread()} id: ${Thread.currentThread().id}")
        */
/*try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            println(e.message)
        }
        println(it.isDisposed)*//*

        for (i in 1..10) {
            for (j in Int.MIN_VALUE..Int.MAX_VALUE) {
            }
            val start = System.currentTimeMillis()
            println(start)
            it.onNext("haha$i")
            for (m in 1..10000000) {
                for (k in Int.MIN_VALUE..Int.MAX_VALUE) {
                }
            }
            Thread.sleep(500)
            println(System.currentTimeMillis() - start)
        }
    }).subscribeOn(Android).observeOn(Schedulers.io()).subscribe(object : Observer<String> {
        override fun onSubscribe(d: Disposable?) {
            println("onSubscribe: ${Thread.currentThread()} id: ${Thread.currentThread().id}")
            println(d)
            dd = d
        }

        override fun onNext(value: String?) {
            println(value)
            println("onNext: ${Thread.currentThread()} id: ${Thread.currentThread().id}")
        }

        override fun onError(e: Throwable?) {
            println(e)
            println("onError: ${Thread.currentThread()}")
        }

        override fun onComplete() {
            println("complete")
            println("complete: ${Thread.currentThread()}")
        }
    })
    */
/*Thread.sleep(1000)
    dd?.dispose()*//*

    Thread.currentThread().join()
}
*/
