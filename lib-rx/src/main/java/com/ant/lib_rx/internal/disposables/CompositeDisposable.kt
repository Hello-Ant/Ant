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

package com.ant.lib_rx.internal.disposables

import com.ant.lib_rx.disposables.Disposable
import com.ant.lib_rx.internal.util.OpenHashSet

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/24 14:09.
 */
class CompositeDisposable() : Disposable, DisposableContainer {

    private var resources: OpenHashSet<Disposable>? = null

    @Volatile
    private var disposed = false

    /**
     * Creates a CompositeDisposables with the given array of initial elements.
     * @param resources the array of Disposables to start with
     */
    constructor(vararg resources: Disposable) : this() {
        this.resources = OpenHashSet(resources.size + 1)
        for (d in resources) {
            this.resources?.add(d)
        }
    }

    /**
     * Creates a CompositeDisposables with the given Iterable sequence of initial elements.
     * @param resources the Iterable sequence of Disposables to start with
     */
    constructor(resources: Iterable<Disposable>) : this() {
        this.resources = OpenHashSet()
        for (d in resources) {
            this.resources?.add(d)
        }
    }

    override fun dispose() {
        if (disposed) {
            return
        }
        val set: OpenHashSet<Disposable>?
        synchronized(this) {
            if (disposed) {
                return
            }
            disposed = true
            set = resources
            resources = null
        }
        dispose(set)
    }

    override fun isDisposed(): Boolean {
        return disposed
    }

    override fun add(d: Disposable): Boolean {
        if (!disposed) {
            synchronized(this) {
                if (!disposed) {
                    var set = resources
                    if (set == null) {
                        set = OpenHashSet()
                        resources = set
                    }
                    set.add(d)
                    return true
                }
            }
        }
        // 添加失败，取消任务
        d.dispose()
        return false
    }

    override fun remove(d: Disposable): Boolean {
        if (delete(d)) {
            d.dispose()
            return true
        }
        return false
    }

    override fun delete(d: Disposable): Boolean {
        if (disposed) {
            return false
        }
        synchronized(this) {
            if (disposed) {
                return false
            }

            val set = resources
            if (set == null || !set.remove(d)) {
                return false
            }
        }
        return true
    }

    fun dispose(set: OpenHashSet<Disposable>?) {
        if (set == null) {
            return
        }
        var errors: MutableList<Throwable>? = null
        val array = set.keys()
        for (d in array) {
            if (d is Disposable) {
                try {
                    d.dispose()
                } catch (ex: Throwable) {
                    if (errors == null) {
                        errors = ArrayList()
                    }
                    errors.add(ex)
                }
            }
        }
        // TODO("错误处理")
    }

}