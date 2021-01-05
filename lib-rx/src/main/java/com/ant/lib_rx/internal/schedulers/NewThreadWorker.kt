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

import com.ant.lib_rx.Scheduler
import com.ant.lib_rx.disposables.Disposable
import com.ant.lib_rx.internal.disposables.CompositeDisposable
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/25 16:53.
 */
open class NewThreadWorker(threadFactory: ThreadFactory) : Scheduler.Worker() {

    @Volatile
    private var disposed = false

    private val executor = SchedulerPoolFactory.create(threadFactory)

    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        return scheduleActual(run, delay, unit, null)
    }

    fun scheduleActual(
        run: Runnable,
        delay: Long,
        unit: TimeUnit,
        parent: CompositeDisposable?
    ): Disposable {

        val sr = ScheduledRunnable(run, parent)
        if (parent != null) {
            if (!parent.add(sr)) {
                return sr
            }
        }

        try {
            val f = if (delay <= 0) {
                executor.submit(sr)
            } else {
                executor.schedule(sr, delay, unit)
            }
            sr.setFuture(f)
        } catch (ex: RejectedExecutionException) {
            parent?.remove(sr)
            // TODO("错误处理")
        }

        return sr
    }

    override fun dispose() {
        if (!disposed) {
            disposed = true
            executor.shutdownNow()
        }
    }

    override fun isDisposed(): Boolean {
        return disposed
    }
}