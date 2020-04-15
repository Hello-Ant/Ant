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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.properties.Delegates

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/10 14:45.
 */
class PieView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var radius by Delegates.notNull<Float>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cx = 0f
    private var cy = 0f

    private var selectIndex = 2

    private val angles = floatArrayOf(50f, 100f, 80f, 60f, 70f)
    private val colors = intArrayOf(Color.BLUE, Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cx = w.div(2).toFloat()
        cy = h.div(2).toFloat()
        radius = min(cx, cy) * 0.8f
    }

    override fun onDraw(canvas: Canvas) {
        var startAngle = 0f
        val select = selectIndex
        for ((index, angle) in angles.withIndex()) {
            if (index == select) {
                canvas.save()
                val radius = Math.toRadians(startAngle.plus(angle.div(2)).toDouble())
                canvas.translate(cos(radius).times(20).toFloat(), sin(radius).times(20).toFloat())
            }
            paint.color = colors[index]
            canvas.drawArc(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius,
                startAngle,
                angle,
                true,
                paint
            )
            if (index == select) {
                canvas.restore()
            }
            startAngle += angle
        }
    }

}