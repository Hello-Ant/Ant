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

package com.ant.lib_rx.internal.schedulers

import com.ant.lib_rx.disposables.Disposable
import com.ant.lib_rx.internal.functions.Functions
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicReference

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/20 9:47.
 */
abstract class AbstractDirectTask(protected val runnable: Runnable) :
    AtomicReference<Future<*>>(), Disposable {

    protected var runner: Runnable? = null

    companion object {
        val FINISHED = FutureTask<Void>(Functions.EMPTY_RUNNABLE, null)
        val DISPOSED = FutureTask<Void>(Functions.EMPTY_RUNNABLE, null)
    }

    override fun dispose() {
        val f = get()
        if (f != FINISHED && f != DISPOSED) {
            if (compareAndSet(f, DISPOSED)) {
                f?.cancel(runner != Thread.currentThread())
            }
        }
    }

    override fun isDisposed(): Boolean {
        val f = get()
        return f == FINISHED || f == DISPOSED
    }

    fun setFuture(future: Future<*>) {
        while (true) {
            val f = get()
            if (f == FINISHED) {
                break
            }
            if (f == DISPOSED) {
                future.cancel(runner != Thread.currentThread())
                break
            }
            if (compareAndSet(f, future)) {
                break
            }
        }
    }
}