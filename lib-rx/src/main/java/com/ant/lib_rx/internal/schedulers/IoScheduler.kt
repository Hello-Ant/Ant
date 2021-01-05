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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/25 15:40.
 */
class IoScheduler(private val threadFactory: RxThreadFactory = WORKER_THREAD_FACTORY) :
    Scheduler() {

    private val pool: AtomicReference<CachedWorkerPool>

    companion object {
        private const val WORKER_THREAD_NAME_PREFIX = "RxCacheThreadScheduler"
        val WORKER_THREAD_FACTORY: RxThreadFactory

        private const val EVICTOR_THREAD_NAME_PREFIX = "RxCacheWorkerPoolEvictor"
        val EVICTOR_THREAD_FACTORY: RxThreadFactory

        private const val KEY_KEEP_ALIVE_TIME = "rx.rx-keep-alive-time"
        private const val KEEP_ALIVE_TIME_DEFAULT = 60L

        private val KEEP_ALIVE_TIME: Long
        private val KEEP_ALIVE_UNIT = TimeUnit.SECONDS

        private val SHUTDOWN_THREAD_WORKER: ThreadWorker

        private const val KEY_IO_PRIORITY = "rx.io-priority"

        private val NONE: CachedWorkerPool

        init {
            KEEP_ALIVE_TIME = java.lang.Long.getLong(KEY_KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_DEFAULT)

            SHUTDOWN_THREAD_WORKER = ThreadWorker(RxThreadFactory("RxCacheThreadSchedulerShutdown"))
            SHUTDOWN_THREAD_WORKER.dispose()

            val priority = max(
                Thread.MIN_PRIORITY,
                min(Thread.MAX_PRIORITY, Integer.getInteger(KEY_IO_PRIORITY, Thread.NORM_PRIORITY))
            )

            WORKER_THREAD_FACTORY = RxThreadFactory(WORKER_THREAD_NAME_PREFIX, priority)

            EVICTOR_THREAD_FACTORY = RxThreadFactory(EVICTOR_THREAD_NAME_PREFIX, priority)

            NONE = CachedWorkerPool(0, null, WORKER_THREAD_FACTORY)
            NONE.shutdown()
        }
    }

    class CachedWorkerPool(
        private val keepAliveTime: Long,
        unit: TimeUnit?,
        private val threadFactory: ThreadFactory
    ) : Runnable {
        private val expiringWorkerQueue = ConcurrentLinkedQueue<ThreadWorker>()
        private val allWorkers = CompositeDisposable()

        private val evictorTask: Future<*>?
        private val evictorService: ScheduledExecutorService?

        init {
            var evictor: ScheduledExecutorService? = null
            var task: Future<*>? = null
            if (unit != null) {
                evictor = Executors.newScheduledThreadPool(1, EVICTOR_THREAD_FACTORY)
                task = evictor.scheduleWithFixedDelay(this, keepAliveTime, keepAliveTime, unit)
            }
            evictorService = evictor
            evictorTask = task
        }

        companion object {
            fun now(): Long {
                return System.nanoTime()
            }
        }

        fun get(): ThreadWorker {
            if (allWorkers.isDisposed()) {
                return SHUTDOWN_THREAD_WORKER
            }
            while (!expiringWorkerQueue.isEmpty()) {
                // 循环遍历
                val threadWorker = expiringWorkerQueue.poll()
                if (threadWorker != null) {
                    return threadWorker
                }
            }

            val w = ThreadWorker(threadFactory)
            allWorkers.add(w)
            return w
        }

        /**
         * 回收任务执行者 重复利用 有效期为 keepAliveTime
         */
        fun release(threadWorker: ThreadWorker) {
            threadWorker.expirationTime = now() + keepAliveTime
            expiringWorkerQueue.offer(threadWorker)
        }

        fun shutdown() {
            allWorkers.dispose()
        }

        override fun run() {
            evictExpiredWorkers(expiringWorkerQueue, allWorkers)
        }

        private fun evictExpiredWorkers(
            expiringWorkerQueue: ConcurrentLinkedQueue<ThreadWorker>,
            allWorkers: CompositeDisposable
        ) {
            if (!expiringWorkerQueue.isEmpty()) {
                val currentTimestamp = now()

                for (threadWorker in expiringWorkerQueue) {
                    if (threadWorker.expirationTime <= currentTimestamp) {
                        if (expiringWorkerQueue.remove(threadWorker)) {
                            allWorkers.remove(threadWorker)
                        }
                    } else {
                        // 因为回收的任务是按事件排序的，所以直接返回
                        break
                    }
                }
            }
        }
    }

    init {
        pool = AtomicReference(NONE)
        start()
    }

    override fun start() {
        val update = CachedWorkerPool(KEEP_ALIVE_TIME, KEEP_ALIVE_UNIT, threadFactory)
        if (!pool.compareAndSet(NONE, update)) {
            update.shutdown()
        }
    }

    override fun shutdown() {
        val curr = pool.getAndSet(NONE)
        if (curr != NONE) {
            curr.shutdown()
        }
    }

    override fun createWorker(): Worker {
        return EventLoopWorker(pool.get())
    }

    class EventLoopWorker(private val pool: CachedWorkerPool) : Worker() {
        private val tasks = CompositeDisposable()
        private val threadWorker = pool.get()

        private val once = AtomicBoolean()

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            if (tasks.isDisposed()) {
                return EmptyDisposable.INSTANCE
            }

            return threadWorker.scheduleActual(run, delay, unit, tasks)
        }

        override fun dispose() {
            if (once.compareAndSet(false, true)) {
                tasks.dispose()

                pool.release(threadWorker)
            }
        }

        override fun isDisposed(): Boolean {
            return once.get()
        }

    }

    class ThreadWorker(threadFactory: ThreadFactory) : NewThreadWorker(threadFactory) {
        var expirationTime = 0L
    }
}