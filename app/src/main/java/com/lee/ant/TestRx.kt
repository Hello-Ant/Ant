/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.lee.ant

import android.os.Handler
import android.os.Looper
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/12/10 14:02.
 */
private val emptyThrowable = Throwable("cache is null ${Thread.currentThread()}")
fun main() {
    testLooper()

    Thread.sleep(10000)
}

private fun testLooper() {
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
        Looper.loop()
        h.sendEmptyMessage(2)
    }.start()
}

private fun test8() {
    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .flatMap { t ->
            Observable.just("flatMap $t").subscribeOn(Schedulers.io()).delay(1L, TimeUnit.SECONDS)
        }.toList(9)
        .subscribe(object : SingleObserver<List<String>> {
            override fun onSubscribe(d: Disposable?) {
                println("onSubscribe")
            }

            override fun onSuccess(t: List<String>?) {
                println(t)
            }

            override fun onError(e: Throwable?) {
                println(e)
            }

        })
}

private fun test7() {
    Single.create(SingleOnSubscribe<String> { emitter ->
        emitter.onSuccess("aha")
    }).flatMap(Function<String, SingleSource<String>> { null })
        .subscribe(object : SingleObserver<String> {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: String?) {
                println(t)
            }

            override fun onError(e: Throwable?) {
                println(e)
            }

        })
}

private fun test6() {
    Single.create(SingleOnSubscribe<String> { emitter ->
        emitter.onError(emptyThrowable)
    }).onErrorResumeNext {
        println(it)
        SingleSource { observer ->
            observer.onSuccess("hahaha ${Thread.currentThread()}")
        }
    }.subscribe(object : SingleObserver<String> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onSuccess(t: String?) {
            println(t)
        }

        override fun onError(e: Throwable?) {
            println(e)
        }
    })
}

private fun test5() {
    val h = Observable.create(ObservableOnSubscribe<String> { emitter ->
        emitter.onNext("parent data is null")
    })
    Observable.create(ObservableOnSubscribe<String> { emitter ->
        emitter.onComplete()
    }).switchIfEmpty(h).subscribe(object : Observer<String> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(t: String?) {
            println(t)
        }

        override fun onError(e: Throwable?) {
            println(e)
        }

        override fun onComplete() {
            println("onComplete")
        }
    })
}

private fun test4() {
    val a = arrayOfNulls<String>(3)
    a[0] = null
    a[1] = "sdk"
    a[2] = "network"

    val memorySource = Observable.create(ObservableOnSubscribe<String> { emitter ->
        val value = a[0]
        if (value != null) {
            emitter.onNext(value)
        }
        emitter.onComplete()
    })
    val diskSource = Observable.create(ObservableOnSubscribe<String> { emitter ->
        val value = a[1]
        if (value != null) {
            emitter.onNext(value)
        }
        emitter.onComplete()
    })
    val networkSource = Observable.create(ObservableOnSubscribe<String> { emitter ->
        val value = a[2]
        if (value != null) {
            emitter.onNext(value)
        }
        emitter.onComplete()
    })
    Observable.concat(memorySource, diskSource, networkSource).firstElement()
        .subscribe(object : MaybeObserver<String?> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: String?) {
                println(t)
            }

            override fun onError(e: Throwable?) {
                println(e)
            }

            override fun onComplete() {
                println("onComplete")
            }
        })
}

private fun test3() {
    Single.create(SingleOnSubscribe<Any> { emitter ->
        emitter.onSuccess(null)
    }).onErrorReturnItem("hahaha").subscribeOn(Schedulers.io())
        .subscribe(object : SingleObserver<Any?> {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: Any?) {
                println(t)
            }

            override fun onError(e: Throwable?) {
                println(e)
            }
        })
}

private fun test2() {
    var disposable: Disposable? = null

    Single.create(SingleOnSubscribe<List<Int>> { emitter ->
        val al = ArrayList<Int>()
        for (i in 0..10) {
            al.add(i)
        }
        println("subscribe -- ${Thread.currentThread()}")
        emitter.onSuccess(al)
    }).observeOn(Schedulers.computation()).flatMap { it ->
        Single.zip(getSource(it), Function {
            val map = HashMap<Int, Any>()
            it.forEachIndexed { index, any ->
                if (index % 2 == 0)
                    map[index] = any
            }
            println("数据转换完成 -- ${Thread.currentThread()}")
            map
        })
    }.subscribeOn(Schedulers.io()).subscribe(object : SingleObserver<Map<Int, Any>> {
        override fun onSubscribe(d: Disposable?) {
            disposable = d;
            println("onSubscribe -- ${Thread.currentThread()}")
        }

        override fun onSuccess(t: Map<Int, Any>) {
            println("onSuccess -- ${Thread.currentThread()}")
            println(t)
        }

        override fun onError(t: Throwable?) {
            println(t)
        }
    })
    /*Thread.sleep(350)
    disposable?.dispose()*/
}

private fun getSource(value: List<Int>): ArrayList<Single<Int>> {
    val singles = ArrayList<Single<Int>>()
    value.forEach {
        val single = Single.create(SingleOnSubscribe<Int> { emitter ->
            println("数据请求：$it -- ${Thread.currentThread()}")
            try {
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                println("取消请求$it")
            }
            if (it == 0) {
                emitter.onSuccess(-1)
            } else {
                emitter.onSuccess(it * 10)
            }
        }).subscribeOn(Schedulers.io())
        singles.add(single)
    }
    return singles
}

private fun test1() {
    var dd: Disposable? = null
    val singles = ArrayList<Single<List<Any>>>()
    for (i in 0..10) {
        val single = Single.create(SingleOnSubscribe<List<Any>> { emitter ->
            println("数据请求：$i -- ${Thread.currentThread()}")
            val al = ArrayList<Any>()
            al.add(i)
            emitter.onSuccess(al)
        }).subscribeOn(Schedulers.computation())
        singles.add(single)
    }
    Single.merge(singles).toObservable()
        .subscribe(object : Observer<List<Any>> {
            override fun onSubscribe(d: Disposable?) {
                dd = d
                println("onSubscribe -- ${Thread.currentThread()}")
            }

            override fun onNext(t: List<Any>?) {
                println("onNext -- ${Thread.currentThread()}")
                println(t)
            }

            override fun onError(t: Throwable?) {
                println(t)
            }

            override fun onComplete() {
                println("onComplete")
            }
        })
}