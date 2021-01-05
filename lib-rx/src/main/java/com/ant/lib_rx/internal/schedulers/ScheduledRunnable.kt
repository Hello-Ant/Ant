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
import com.ant.lib_rx.internal.disposables.DisposableContainer
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReferenceArray

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/24 14:53.
 */
class ScheduledRunnable(private val actual: Runnable, parent: DisposableContainer?) :
    AtomicReferenceArray<Any>(3), Runnable, Callable<Any>, Disposable {

    companion object {
        val PARENT_DISPOSED = Any()
        val SYNC_DISPOSED = Any()
        val ASYNC_DISPOSED = Any()

        val DONE = Any()

        const val PARENT_INDEX = 0
        const val FUTURE_INDEX = 1
        const val THREAD_INDEX = 2
    }

    init {
        lazySet(PARENT_INDEX, parent)
    }

    override fun call(): Any? {
        run()
        return null
    }

    override fun run() {
        // 标记执行任务的线程
        lazySet(THREAD_INDEX, Thread.currentThread())
        try {
            try {
                actual.run()
            } catch (e: Throwable) {
                // TODO("执行过程出现错误")
            }
        } finally {
            // 任务执行完成标记任务执行的线程为null
            lazySet(THREAD_INDEX, null)
            var o = get(PARENT_INDEX)
            // 如果没有取消，标记任务执行完成
            if (o != PARENT_DISPOSED && compareAndSet(PARENT_INDEX, o, DONE) && o != null) {
                // 删除任务
                (o as DisposableContainer).delete(this)
            }
            while (true) {
                o = get(FUTURE_INDEX)
                // 如果没有取消，标记任务执行完成
                if (o == SYNC_DISPOSED || o == ASYNC_DISPOSED || compareAndSet(
                        FUTURE_INDEX,
                        o,
                        DONE
                    )
                ) {
                    break
                }
            }
        }
    }

    fun setFuture(f: Future<*>) {
        val o = get(FUTURE_INDEX)
        while (true) {
            if (o == DONE) {
                return
            }
            if (o == SYNC_DISPOSED) {
                f.cancel(false)
                return
            }
            if (o == ASYNC_DISPOSED) {
                f.cancel(true)
                return
            }
            if (compareAndSet(FUTURE_INDEX, o, f)) {
                return
            }
        }
    }

    override fun dispose() {
        while (true) {
            val o = get(FUTURE_INDEX)
            if (o == DONE || o == SYNC_DISPOSED || o == ASYNC_DISPOSED) {
                break
            }
            // 调用取消任务的线程和执行任务的线程不一致，则异步
            val async = get(THREAD_INDEX) != Thread.currentThread()
            if (compareAndSet(FUTURE_INDEX, o, if (async) ASYNC_DISPOSED else SYNC_DISPOSED)) {
                if (o != null) {
                    // 异步会触发 InterruptedException
                    (o as Future<*>).cancel(async)
                }
                break
            }
        }
        while (true) {
            val o = get(PARENT_INDEX)
            if (o == DONE || o == PARENT_DISPOSED || o == null) {
                return
            }
            if (compareAndSet(PARENT_INDEX, o, PARENT_DISPOSED)) {
                // 这里使用delete而不是dispose，因为这只是取消单个任务，其他任务并没有取消
                (o as DisposableContainer).delete(this)
                return
            }
        }
    }

    override fun isDisposed(): Boolean {
        val o = get(PARENT_INDEX)
        return o == PARENT_DISPOSED || o == DONE
    }
}