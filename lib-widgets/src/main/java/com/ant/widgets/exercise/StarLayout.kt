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

package com.ant.widgets.exercise

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/20 13:53.
 */
class StarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    /**
     * 最小行星相对于最大行星的缩放值
     */
    private val maxScaleOffset = 0.3f

    private var maxOffsetY = 0

    /**
     * 行星环绕恒星的半径
     */
    private var radius = 0;

    /**
     * 恒星的半径
     */
    private var fixedStarRadius = 0;

    /**
     * 行星的半径
     */
    private var planetsRadius = 0;

    /**
     * 相邻的星球角度偏移
     */
    private var angleOffset = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec)
        )

        val count = childCount

        angleOffset = 360f / count
        val halfSize =
            min(
                measuredWidth - paddingLeft - paddingRight,
                measuredHeight - paddingTop - paddingBottom
            ).shr(1)
        planetsRadius = halfSize / 9
        radius = halfSize - planetsRadius

        maxOffsetY = sin(Math.toRadians(50.toDouble())).times(radius).toInt()

        val childMeasureSpec = MeasureSpec.makeMeasureSpec(planetsRadius, MeasureSpec.EXACTLY);
        for (index in 0 until count) {
            val child = getChildAt(index)
            child.measure(childMeasureSpec, childMeasureSpec)
        }
    }

    var startAngle = 0.toDouble()
        set(value) {
            field = value
//            ViewCompat.postInvalidateOnAnimation(this)
            requestLayout()
        }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val cx = measuredWidth / 2
        val cy = measuredWidth / 2

        for (index in 0 until count) {
            val child = getChildAt(index)
            val radians = Math.toRadians(startAngle)
            val planetCx = cx + radius.times(cos(radians)).toInt()
            val planetCy = cy + radius.times(sin(radians)).toInt()
            val offsetY = sin(radians).times(maxOffsetY).toInt()
            child.layout(
                planetCx - planetsRadius,
                planetCy - planetsRadius - offsetY,
                planetCx + planetsRadius,
                planetCy + planetsRadius - offsetY
            )
            val scale = 1 + maxScaleOffset.times(sin(radians)).toFloat()
            child.scaleX = scale
            child.scaleY = scale
            startAngle += angleOffset
        }
    }
}