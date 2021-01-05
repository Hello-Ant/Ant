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

package com.ant.lib_rx.disposables

import java.util.concurrent.atomic.AtomicReference

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/11 9:22.
 */
enum class DisposableHelper : Disposable {
    DISPOSED;

    companion object {

        fun dispose(field: AtomicReference<Disposable>): Boolean {
            var current = field.get()
            val d = DISPOSED
            if (current != d) {
                current = field.getAndSet(d)
                if (current != d) {
                    current?.dispose()
                }
                return true
            }
            return false
        }

        fun isDisposed(d: Disposable?): Boolean {
            return d == DISPOSED
        }

        fun setOnce(field: AtomicReference<Disposable>, d: Disposable): Boolean {
            if (!field.compareAndSet(null, d)) {
                d.dispose()
                if (field.get() != DISPOSED) {
                    reportDisposableSet()
                }
                return false
            }
            return true
        }

        /**
         * 校验 current==null，next！=null
         */
        fun validate(current: Disposable?, next: Disposable?): Boolean {
            if (next == null) {
                return false
            }
            if (current != null) {
                next.dispose()
                reportDisposableSet()
                return false
            }
            return true
        }

        private fun reportDisposableSet() {
            println("Disposable already set!")
        }

    }

    override fun dispose() {
        // deliberately no-op
    }

    override fun isDisposed(): Boolean {
        return true
    }
}