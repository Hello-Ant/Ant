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

package com.ant.lib_rx.internal.util

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 *     A simple open hash set with add, remove and clear capabilities o
 *     Doesn't support nor checks for `null`s.
 *     @param <T> the element type
 * <p>
 *
 * @author Ant
 * @date on  2020/11/24 13:52.
 */
class OpenHashSet<T> @JvmOverloads constructor(capacity: Int = 16, loadFactor: Float = 0.75f) {
    val loadFactor: Float
    var mask: Int
    var size = 0
    var maxSize: Int
    var keys: Array<T?>
    fun add(value: T): Boolean {
        val a = keys
        val m = mask
        var pos = mix(value.hashCode()) and m
        var curr = a[pos]
        if (curr != null) {
            if (curr == value) {
                return false
            }
            while (true) {
                pos = pos + 1 and m
                curr = a[pos]
                if (curr == null) {
                    break
                }
                if (curr == value) {
                    return false
                }
            }
        }
        a[pos] = value
        if (++size >= maxSize) {
            rehash()
        }
        return true
    }

    fun remove(value: T): Boolean {
        val a = keys
        val m = mask
        var pos = mix(value.hashCode()) and m
        var curr: T? = a[pos] ?: return false
        if (curr == value) {
            return removeEntry(pos, a, m)
        }
        while (true) {
            pos = pos + 1 and m
            curr = a[pos]
            if (curr == null) {
                return false
            }
            if (curr == value) {
                return removeEntry(pos, a, m)
            }
        }
    }

    fun removeEntry(pos: Int, a: Array<T?>, m: Int): Boolean {
        size--

        var index = pos
        var last: Int
        var slot: Int
        var curr: T?
        while (true) {
            last = index
            index = index + 1 and m
            while (true) {
                curr = a[index]
                if (curr == null) {
                    a[last] = null
                    return true
                }
                slot = mix(curr.hashCode()) and m
                if (if (last <= index) last >= slot || slot > index else slot in (index + 1)..last) {
                    break
                }
                index = index + 1 and m
            }
            a[last] = curr
        }
    }

    fun rehash() {
        val a = keys
        var i = a.size
        val newCap = i shl 1
        val m = newCap - 1
        val b = arrayOfNulls<Any>(newCap) as Array<T?>
        var j = size
        while (j-- != 0) {
            while (a[--i] == null) {
            } // NOPMD
            var pos = mix(a[i].hashCode()) and m
            if (b[pos] != null) {
                while (true) {
                    pos = pos + 1 and m
                    if (b[pos] == null) {
                        break
                    }
                }
            }
            b[pos] = a[i]
        }
        mask = m
        maxSize = (newCap * loadFactor).toInt()
        keys = b
    }

    fun keys(): Array<Any?> {
        return keys as Array<Any?> // NOPMD
    }

    fun size(): Int {
        return size
    }

    companion object {
        private const val INT_PHI = -0x61c88647
        fun mix(x: Int): Int {
            val h = x * INT_PHI
            return h xor (h ushr 16)
        }
    }

    /**
     * Creates an OpenHashSet with the initial capacity and load factor of 0.75f.
     * @param capacity the initial capacity
     */
    init {
        this.loadFactor = loadFactor
        val c = Pow2.roundToPowerOfTwo(capacity)
        mask = c - 1
        maxSize = (loadFactor * c).toInt()
        keys = arrayOfNulls<Any>(c) as Array<T?>
    }
}