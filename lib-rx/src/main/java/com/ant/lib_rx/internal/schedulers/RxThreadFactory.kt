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

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/17 14:52.
 */
class RxThreadFactory(
    private val prefix: String,
    private val priority: Int = Thread.NORM_PRIORITY,
    private val nonBlocking: Boolean = false
) : ThreadFactory {

    private val id: AtomicLong = AtomicLong()

    override fun newThread(r: Runnable): Thread {
        val nameBuilder = StringBuilder(prefix).append('-').append(id.incrementAndGet())
        val name = nameBuilder.toString()
        val t = if (nonBlocking) CustomThread(r, name) else Thread(r, name)
        return t.apply {
            priority = this@RxThreadFactory.priority
            isDaemon = true
        }
    }

    class CustomThread(run: Runnable, name: String) : Thread(run, name), NonBlockingThread
}