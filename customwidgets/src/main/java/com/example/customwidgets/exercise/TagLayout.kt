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

package com.example.customwidgets.exercise

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import com.example.customwidgets.R

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/5 9:53.
 */
class TagLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var horizontalSpacing = 0
    private var verticalSpacing = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.TagLayout) {
            horizontalSpacing =
                getDimensionPixelSize(R.styleable.TagLayout_android_horizontalSpacing, 0)
            verticalSpacing =
                getDimensionPixelSize(R.styleable.TagLayout_android_verticalSpacing, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val remainingWidth = width - paddingLeft - paddingRight
        var totalHeight = 0

        var usedWidth = 0
        var largestChildHeight = 0

        for (index in 0 until childCount) {
            val child = getChildAt(index)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (usedWidth.plus(childWidth) <= remainingWidth) {
                usedWidth += childWidth + horizontalSpacing
                if (largestChildHeight < childHeight) {
                    largestChildHeight = childHeight
                }
            } else {
                usedWidth = childWidth + horizontalSpacing
                totalHeight += largestChildHeight + verticalSpacing
                largestChildHeight = childHeight
            }
        }
        totalHeight += largestChildHeight + paddingTop + paddingBottom

        setMeasuredDimension(width, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val remainingWidth = width - paddingRight

        var usedWidth = paddingLeft
        var usedHeight = paddingTop
        var largestChildHeight = 0

        for (index in 0 until childCount) {
            val child = getChildAt(index)

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (usedWidth.plus(childWidth) <= remainingWidth) {
                if (largestChildHeight < childHeight) {
                    largestChildHeight = childHeight
                }
            } else {
                usedWidth = paddingLeft
                usedHeight += largestChildHeight + verticalSpacing
                largestChildHeight = childHeight
            }

            child.layout(
                usedWidth,
                usedHeight,
                usedWidth.plus(childWidth),
                usedHeight.plus(childHeight)
            )
            usedWidth += childWidth + horizontalSpacing
        }
    }
}