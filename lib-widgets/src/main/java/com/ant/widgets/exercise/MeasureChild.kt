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

package com.example.widgets.exercise

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams
import kotlin.math.max


/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 *     ViewGroup.getChildMeasureSpec方法不会resolveSize
 *     自定义ViewGroup的时候需要注意修正子控件的尺寸
 * <p>
 *
 * @author Ant
 * @date on 2020/5/11 10:51.
 */
open class MeasureChild {

    protected fun measureChild(
        child: View,
        parentWidthMeasureSpec: Int,
        parentHeightMeasureSpec: Int
    ) {
        val lp = child.layoutParams

        val childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, 0, lp.width)
        val childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, 0, lp.height)

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    companion object {

        @JvmStatic
        fun getChildMeasureSpec(spec: Int, padding: Int, childDimension: Int): Int {
            val specMode = MeasureSpec.getMode(spec)
            val specSize = MeasureSpec.getSize(spec)

            val size = max(0, specSize - padding)

            var resultMode = 0
            var resultSize = 0

            when (specMode) {
                MeasureSpec.EXACTLY ->
                    when {
                        childDimension >= 0 -> {
                            resultMode = MeasureSpec.EXACTLY
                            resultSize = childDimension
                        }
                        childDimension == LayoutParams.MATCH_PARENT -> {
                            resultMode = MeasureSpec.EXACTLY
                            resultSize = size
                        }
                        childDimension == LayoutParams.WRAP_CONTENT -> {
                            resultMode = MeasureSpec.AT_MOST
                            resultSize = size
                        }
                    }

                MeasureSpec.AT_MOST -> {
                    when {
                        childDimension >= 0 -> {
                            resultMode = MeasureSpec.EXACTLY
                            resultSize = childDimension
                        }
                        childDimension == LayoutParams.MATCH_PARENT -> {
                            resultMode = MeasureSpec.AT_MOST
                            resultSize = size
                        }
                        childDimension == LayoutParams.WRAP_CONTENT -> {
                            resultMode = MeasureSpec.AT_MOST
                            resultSize = size
                        }
                    }
                }

                MeasureSpec.UNSPECIFIED -> {
                    when {
                        childDimension >= 0 -> {
                            resultMode = MeasureSpec.EXACTLY
                            resultSize = childDimension
                        }
                        childDimension == LayoutParams.MATCH_PARENT -> {
                            resultMode = MeasureSpec.UNSPECIFIED
                            resultSize = 0
                        }
                        childDimension == LayoutParams.WRAP_CONTENT -> {
                            resultMode = MeasureSpec.UNSPECIFIED
                            resultSize = 0
                        }
                    }
                }
            }
            return MeasureSpec.makeMeasureSpec(resultSize, resultMode)
        }

        @JvmStatic
        fun getDefaultSize(size: Int, measureSpec: Int): Int {
            val specMode = MeasureSpec.getMode(measureSpec)
            val specSize = MeasureSpec.getSize(measureSpec)

            return when (specMode) {
                MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> specSize
                else -> size
            }
        }

        private const val MEASURED_STATE_MASK = 0xff000000.toInt()

        private const val MEASURED_STATE_TOO_SMALL = 0x01000000

        @JvmStatic
        fun resolveSize(size: Int, measureSpec: Int): Int {
            return resolveSizeAndState(size, measureSpec, 0) and MEASURED_STATE_MASK
        }

        @JvmStatic
        fun resolveSizeAndState(size: Int, measureSpec: Int, childMeasuredState: Int): Int {
            val specMode = MeasureSpec.getMode(measureSpec)
            val specSize = MeasureSpec.getSize(measureSpec)

            val result = when (specMode) {
                MeasureSpec.AT_MOST -> if (size > specSize) specSize or MEASURED_STATE_TOO_SMALL else size
                MeasureSpec.EXACTLY -> specSize
                else -> size
            }
            return result or (childMeasuredState and MEASURED_STATE_MASK)
        }

    }

}