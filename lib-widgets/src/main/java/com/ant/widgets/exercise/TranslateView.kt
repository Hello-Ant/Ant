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

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/4 9:20.
 */
class TranslateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var RADIUS = 0f

        private var START_X = 0f
        private var START_Y = 0f

        private var END_X = 0f
        private var END_Y = 0f
    }

    var point = PointF()
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        color = Color.BLACK
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w > h) {
            RADIUS = h.times(0.05f)
            END_X = w.toFloat() - RADIUS
            END_Y = h.times(0.5f) - RADIUS
        } else {
            RADIUS = w.times(0.05f)
            END_X = w.times(0.5f) - RADIUS
            END_Y = h.toFloat() - RADIUS
        }
        START_X = RADIUS
        START_Y = RADIUS

        point.x = START_X
        point.y = START_Y

        paint.strokeWidth = RADIUS
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPoint(point.x, point.y, paint)
    }

    class PointEvaluator : TypeEvaluator<PointF> {
        override fun evaluate(fraction: Float, startValue: PointF, endValue: PointF): PointF {
            val startX = startValue.x
            val startY = startValue.y
            val endX = endValue.x
            val endY = endValue.y
            val curX = endX.minus(startX).times(fraction).plus(startX)
            val curY = endY.minus(startY).times(fraction).plus(startY)
            return PointF(curX, curY)
        }

    }

    private var anim: ValueAnimator? = null
    fun startAnim() {
        anim?.cancel()
        anim =
            ObjectAnimator.ofObject(this, "point", PointEvaluator(), PointF(END_X, END_Y)).apply {
                duration = 3000
                repeatCount = ValueAnimator.INFINITE
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    point = animatedValue as PointF
                }
            }
        anim?.start()
    }

    fun stopAnim() {
        anim?.cancel()
    }
}