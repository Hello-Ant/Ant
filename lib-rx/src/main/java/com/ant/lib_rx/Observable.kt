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

package com.ant.lib_rx

import com.ant.lib_rx.internal.operators.observable.ObservableCreate
import com.ant.lib_rx.internal.operators.observable.ObservableObserveOn
import com.ant.lib_rx.internal.operators.observable.ObservableSubscribeOn

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/10 10:25.
 */
abstract class Observable<T> : ObservableSource<T> {

    companion object {

        fun <T> create(source: ObservableOnSubscribe<T>): Observable<T> {
            return ObservableCreate(source)
        }

    }

    fun subscribeOn(scheduler: Scheduler): Observable<T> {
        return ObservableSubscribeOn(this, scheduler)
    }

    fun observeOn(scheduler: Scheduler): Observable<T> {
        return ObservableObserveOn(this, scheduler, false, 128)
    }

    final override fun subscribe(observer: Observer<in T>) {
        subscribeActual(observer)
    }

    protected abstract fun subscribeActual(observer: Observer<in T>)
}