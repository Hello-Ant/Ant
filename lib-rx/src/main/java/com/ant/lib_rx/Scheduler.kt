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

import com.ant.lib_rx.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/11 9:52.
 */
abstract class Scheduler {

    fun scheduleDirect(run: Runnable): Disposable {
        return scheduleDirect(run, 0, TimeUnit.NANOSECONDS)
    }

    open fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val w = createWorker()
        val task = DisposableTask(run, w)
        w.schedule(task, delay, unit)
        return task
    }

    abstract fun createWorker(): Worker

    open fun start() {}

    open fun shutdown() {}

    abstract class Worker : Disposable {

        fun schedule(run: Runnable): Disposable {
            return schedule(run, 0, TimeUnit.NANOSECONDS)
        }

        abstract fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable
    }

    class DisposableTask(val decorateRun: Runnable, val w: Worker) : Disposable, Runnable {
        var runner: Thread? = null

        override fun run() {
            runner = Thread.currentThread()
            try {
                decorateRun.run()
            } finally {
                dispose()
                runner = null
            }
        }

        override fun dispose() {

        }

        override fun isDisposed(): Boolean {
            return w.isDisposed()
        }
    }
}