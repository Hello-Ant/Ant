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

package com.ant.lib_rx.internal.operators.observable

import com.ant.lib_rx.ObservableSource
import com.ant.lib_rx.Observer
import com.ant.lib_rx.Scheduler
import com.ant.lib_rx.disposables.Disposable
import com.ant.lib_rx.disposables.DisposableHelper
import java.util.concurrent.atomic.AtomicReference

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/11 13:59.
 */
class ObservableSubscribeOn<T>(source: ObservableSource<T>, private val scheduler: Scheduler) :
    AbstractObservableWithUpstream<T, T>(source) {

    override fun subscribeActual(observer: Observer<in T>) {
        // 创建观察者，传入下游的观察者(即我们代码写的观察者)，当前观察者可以视为上游观察者，用于回调数据请求结果，然后透传给下游
        val parent = SubscribeOnObservable<T>(observer)
        // 回调Observer的onSubscribe方法，传入Disposable，用于外层主动取消任务
        observer.onSubscribe(parent)
        // 1.切线程执行任务，任务为传入的被观察对象调用subscribe方法，在设置的线程内启动传入的被观察者的订阅流程
        // 2.将可取消订阅的任务设置给观察者，用于外层调用dispose取消订阅，执行任务的取消操作
        parent.setDisposable(scheduler.scheduleDirect(SubscribeTask(parent)))
    }

    class SubscribeOnObservable<T>(private val downstream: Observer<in T>) :
        AtomicReference<Disposable>(), Observer<T>, Disposable {

        private val upstream = AtomicReference<Disposable>()

        override fun onSubscribe(d: Disposable) {
            DisposableHelper.setOnce(upstream, d)
        }

        override fun onNext(value: T) {
            downstream.onNext(value)
        }

        override fun onError(error: Throwable) {
            downstream.onError(error)
        }

        override fun onComplete() {
            downstream.onComplete()
        }

        override fun dispose() {
            // 取消上游的订阅 onSubscribe回调传回的上游Disposable
            DisposableHelper.dispose(upstream)
            // 取消自己的订阅 这个作用在哪？哪里用到了当前取消订阅的标识  ObservableOnSubscribe的subscribe方法里面的代码逻辑会中断，怎么做到的？
            DisposableHelper.dispose(this)
        }

        override fun isDisposed(): Boolean {
            return DisposableHelper.isDisposed(get())
        }

        fun setDisposable(d: Disposable) {
            DisposableHelper.setOnce(this, d)
        }
    }

    inner class SubscribeTask(private val parent: SubscribeOnObservable<T>) : Runnable {

        override fun run() {
            source.subscribe(parent)
        }
    }
}