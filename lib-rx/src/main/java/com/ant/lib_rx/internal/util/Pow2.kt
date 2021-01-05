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
 * <p>
 *
 * @author Ant
 * @date on  2020/11/20 16:34.
 */
class Pow2 private constructor() {
    companion object {
        /**
         * Find the next larger positive power of two value up from the given value. If value is a power of two then
         * this value will be returned.
         *
         * @param value from which next positive power of two will be found.
         * @return the next positive power of 2 or this value if it is a power of 2.
         */
        fun roundToPowerOfTwo(value: Int): Int {
            return 1 shl 32 - Integer.numberOfLeadingZeros(value - 1)
        }

        /**
         * Is this value a power of two.
         *
         * @param value to be tested to see if it is a power of two.
         * @return true if the value is a power of 2 otherwise false.
         */
        fun isPowerOfTwo(value: Int): Boolean {
            return value and value - 1 == 0
        }
    }

    init {
        throw IllegalStateException("No instances!")
    }
}