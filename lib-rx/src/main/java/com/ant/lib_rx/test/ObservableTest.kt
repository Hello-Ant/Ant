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

package com.ant.lib_rx.test

import com.ant.lib_rx.Observable
import com.ant.lib_rx.ObservableEmitter
import com.ant.lib_rx.ObservableOnSubscribe
import com.ant.lib_rx.Observer
import com.ant.lib_rx.disposables.Disposable
import com.ant.lib_rx.schedulers.Schedulers

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/11 9:01.
 */
fun main() {
    ObservableTest().test()
    Thread.sleep(10000L)
}

class ObservableTest {

    fun test() {
        var dd: Disposable? = null
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                println("subscribe: ${Thread.currentThread()} id: ${Thread.currentThread().id}")
                /*try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    println(e.message)
                }*/
                println(emitter.isDisposed())
                for (i in 1..10) {
                    for (j in Int.MIN_VALUE..Int.MAX_VALUE) {
                    }
                    val start = System.currentTimeMillis()
                    println(start)
                    emitter.onNext("haha$i")
                    for (m in 1..10000000) {
                        for (k in Int.MIN_VALUE..Int.MAX_VALUE) {
                        }
                    }
                    println(System.currentTimeMillis() - start)
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe: ${Thread.currentThread()} id: ${Thread.currentThread().id}")
                    println(d)
                    dd = d
                }

                override fun onNext(value: String) {
                    println(value)
                    println("onNext: ${Thread.currentThread()} id: ${Thread.currentThread().id}")
                }

                override fun onError(error: Throwable) {
                    println(error)
                    println("onError: ${Thread.currentThread()}")
                }

                override fun onComplete() {
                    println("complete")
                    println("complete: ${Thread.currentThread()}")
                }
            })
        /*Thread.sleep(1000)
        dd?.dispose()*/
    }

}