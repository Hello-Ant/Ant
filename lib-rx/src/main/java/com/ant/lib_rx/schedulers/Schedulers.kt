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

package com.ant.lib_rx.schedulers

import com.ant.lib_rx.Scheduler
import com.ant.lib_rx.internal.schedulers.IoScheduler
import com.ant.lib_rx.internal.schedulers.SingleScheduler
import java.util.function.Supplier

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/11 9:53.
 */
class Schedulers private constructor() {

    init {
        throw IllegalStateException("No instances!")
    }

    companion object {
        private val SINGLE = SingleTask().get()
        private val IO = IoTask().get()

        fun single(): Scheduler {
            return SINGLE
        }

        fun io(): Scheduler {
            return IO
        }
    }

    private object SingleHolder {
        val DEFAULT = SingleScheduler()
    }

    private object IoHolder {
        val DEFAULT = IoScheduler()
    }

    private class SingleTask : Supplier<Scheduler> {
        override fun get(): Scheduler {
            return SingleHolder.DEFAULT
        }
    }

    private class IoTask : Supplier<Scheduler> {
        override fun get(): Scheduler {
            return IoHolder.DEFAULT
        }

    }
}