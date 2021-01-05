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
import com.ant.lib_rx.internal.fuseable.EmptyDisposable
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicReference

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/17 14:28.
 */
class SingleScheduler(private val threadFactory: ThreadFactory = SINGLE_THREAD_FACTORY) :
    Scheduler() {

    /**
     * 线程安全 需要创建一个停止运行的线程池作为shutdown标记
     */
    private val executor = AtomicReference<ScheduledExecutorService>()

    companion object {
        private const val KEY_SINGLE_PRIORITY = "rx.single-priority"
        private const val THREAD_NAME_PREFIX = "RxSingleScheduler"

        val SINGLE_THREAD_FACTORY: ThreadFactory

        val SHUTDOWN: ScheduledExecutorService = Executors.newScheduledThreadPool(0)

        init {
            SHUTDOWN.shutdown()

            val priority = Thread.MAX_PRIORITY.coerceAtMost(
                Integer.getInteger(
                    KEY_SINGLE_PRIORITY,
                    Thread.NORM_PRIORITY
                )
            )
            SINGLE_THREAD_FACTORY = RxThreadFactory(THREAD_NAME_PREFIX, priority, true)
        }

        fun createExecutor(factory: ThreadFactory): ScheduledExecutorService {
            return SchedulerPoolFactory.create(factory)
        }
    }

    init {
        executor.lazySet(createExecutor(threadFactory))
    }

    override fun start() {
        var next: ScheduledExecutorService? = null
        while (true) {
            val current = executor.get()
            if (current != SHUTDOWN) {
                next?.shutdown()
                return
            }
            if (next == null) {
                next = createExecutor(threadFactory)
            }
            if (executor.compareAndSet(current, next)) {
                return
            }
        }
    }

    override fun shutdown() {
        var current = executor.get()
        if (current != SHUTDOWN) {
            current = executor.getAndSet(SHUTDOWN)
            if (current != SHUTDOWN) {
                current.shutdownNow()
            }
        }
    }

    override fun createWorker(): Worker {
        return ScheduledWorker(executor.get())
    }

    override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val task = ScheduledDirectTask(run)
        return try {
            val f = if (delay <= 0L)
                executor.get().submit(task)
            else
                executor.get().schedule(task, delay, unit)
            task.setFuture(f)
            task
        } catch (ex: RejectedExecutionException) {
            // 错误处理
            EmptyDisposable.INSTANCE
        }
    }

    class ScheduledWorker(private val executor: ScheduledExecutorService) : Worker() {
        private val tasks = CompositeDisposable()

        @Volatile
        var disposed: Boolean = false

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            if (disposed) {
                return EmptyDisposable.INSTANCE
            }

            val sr = ScheduledRunnable(run, tasks)
            tasks.add(sr)

            return try {
                val f = if (delay <= 0L)
                    executor.submit(sr)
                else
                    executor.schedule(sr, delay, unit)
                sr.setFuture(f)
                sr
            } catch (ex: RejectedExecutionException) {
                dispose()
                // 错误处理
                EmptyDisposable.INSTANCE
            }
        }

        override fun dispose() {
            if (!disposed) {
                disposed = true
                tasks.dispose()
            }
        }

        override fun isDisposed(): Boolean {
            return disposed
        }

    }
}