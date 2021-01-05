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
import com.ant.lib_rx.internal.fuseable.SimpleQueue
import com.ant.lib_rx.internal.queue.SpscLinkedArrayQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 *     如果切换线程，就会涉及到生产者与消费者不平衡问题 单生产者，单消费者
 * <p>
 *
 * @author Ant
 * @date on  2020/11/20 15:24.
 */
class ObservableObserveOn<T>(
    source: ObservableSource<T>,
    val scheduler: Scheduler,
    val delayError: Boolean,
    val bufferSize: Int
) : AbstractObservableWithUpstream<T, T>(source) {

    override fun subscribeActual(observer: Observer<in T>) {
        val w = scheduler.createWorker()
        source.subscribe(ObserveOnObserver(observer, w, delayError, bufferSize))
    }

    class ObserveOnObserver<T>(
        val downstream: Observer<in T>,
        val worker: Scheduler.Worker,
        val delayError: Boolean,
        val bufferSize: Int
    ) : Disposable, Observer<T>, Runnable {

        private var queue: SimpleQueue<T>? = null

        private var upstream: Disposable? = null

        private var error: Throwable? = null

        @Volatile
        private var done = false

        @Volatile
        private var disposed = false

        private val ai = AtomicInteger()

        override fun onSubscribe(d: Disposable) {
            if (DisposableHelper.validate(upstream, d)) {
                upstream = d

                queue = SpscLinkedArrayQueue(bufferSize)

                downstream.onSubscribe(this)
            }
        }

        override fun onNext(value: T) {
            if (done) {
                return
            }
            queue?.offer(value)
            schedule()
        }

        override fun onError(error: Throwable) {
            if (done) {
                return
            }
            this.error = error
            done = true
            schedule()
        }

        override fun onComplete() {
            if (done) {
                return
            }
            done = true
            schedule()
        }

        override fun dispose() {
            if (!disposed) {
                disposed = true
                upstream?.dispose()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
                worker.dispose()
                if (ai.getAndIncrement() == 0) {
                    // TODO(啥意思)
                    queue?.clear()
                }
            }
        }

        override fun isDisposed(): Boolean {
            return disposed
        }

        private fun schedule() {
            if (ai.getAndIncrement() == 0) {
                worker.schedule(this)
            }
        }

        override fun run() {
            drain()
        }

        private fun drain() {
            var missed = 1

            val q = queue
            val a = downstream
            while (true) {
                if (checkTerminated(done, q?.isEmpty() != false, a)) {
                    return
                }

                while (true) {
                    val d = done

                    val v: T?
                    try {
                        v = q?.poll()
                    } catch (ex: Throwable) {
                        disposed = true
                        upstream?.dispose()
                        q?.clear()
                        a.onError(ex)
                        worker.dispose()
                        return
                    }
                    val empty = v == null

                    if (checkTerminated(d, empty, a)) {
                        return
                    }

                    if (empty) {
                        break
                    }

                    v?.let { a.onNext(it) }
                }
                missed = ai.addAndGet(-missed)
                if (missed == 0) {
                    // TODO(啥意思)
                    break
                }
            }
        }

        private fun checkTerminated(d: Boolean, empty: Boolean, a: Observer<in T>): Boolean {
            if (disposed) {
                queue?.clear()
                return true
            }
            if (d) {
                // d == true 说明回调了onComplete或者onError 在这里进行事件终结的分发
                val e = error
                if (e != null) {
                    disposed = true
                    queue?.clear()
                    a.onError(e)
                    worker.dispose()
                    return true
                } else if (empty) {
                    disposed = true
                    a.onComplete()
                    worker.dispose()
                    return true
                }
            }
            return false
        }

    }
}