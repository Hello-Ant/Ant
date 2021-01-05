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

import com.ant.lib_rx.Observable
import com.ant.lib_rx.ObservableEmitter
import com.ant.lib_rx.ObservableOnSubscribe
import com.ant.lib_rx.Observer
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
 * @date on  2020/11/10 10:44.
 */
class ObservableCreate<T>(private val source: ObservableOnSubscribe<T>) : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        // 创建数据发射器，传入观察者，用于将结果回调给观察者
        val parent = EmitterCreate(observer)
        // 回调Observer的onSubscribe方法，传入Disposable，用于外层主动取消任务
        observer.onSubscribe(parent)
        // 发射器调用subscribe方法，真正执行我们的代码逻辑，传入发射器，用于调用onNext各种方法
        try {
            source.subscribe(parent)
        } catch (ex: Throwable) {
            // 捕获代码执行错误，这样编辑业务逻辑的时候就不需要try catch了，但是这样就会有个大坑，取消订阅了，但是代码逻辑还是会执行的，如果出现异常，并且没有设置全局异常捕获处理，会导致异常抛出
            parent.onError(ex)
        }
    }

    class EmitterCreate<T>(private val observer: Observer<in T>) : AtomicReference<Disposable>(),
        ObservableEmitter<T>, Disposable {

        override fun onNext(value: T) {
            if (value == null) {
                onError(NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."))
                return
            }
            if (!isDisposed()) {
                observer.onNext(value)
            }
        }

        override fun onError(error: Throwable?) {
            val e = error
                ?: NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.")
            if (!isDisposed()) {
                try {
                    observer.onError(e)
                } finally {
                    dispose()
                }
            }
        }

        override fun onComplete() {
            if (!isDisposed()) {
                try {
                    observer.onComplete()
                } finally {
                    dispose()
                }
            }
        }

        override fun dispose() {
            DisposableHelper.dispose(this)
        }

        override fun isDisposed(): Boolean {
            return DisposableHelper.isDisposed(get())
        }
    }
}