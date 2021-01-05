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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.ant.base.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/3 7:31.
 */
class PieView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val SELECT_INDEX = 2;
        private val OFFSET = 10.dp
    }

    private val colors = arrayOf(Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.GREEN)
    private val arcs = arrayOf(60f, 100f, 120f, 80f)

    private val rectF = RectF()
    private val paint = Paint(ANTI_ALIAS_FLAG)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rectF.top = paddingTop.toFloat()
        rectF.left = paddingLeft.toFloat()
        rectF.right = w - paddingRight.toFloat()
        rectF.bottom = h - paddingBottom.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        var startCorner = 0f
        for ((index, sweepCorner) in arcs.withIndex()) {
            paint.color = colors[index]

            if (index == SELECT_INDEX) {
                canvas.save()
                val radians = Math.toRadians(startCorner + sweepCorner.div(2f).toDouble()).toFloat()
                canvas.translate(cos(radians) * OFFSET, sin(radians) * OFFSET)
            }

            canvas.drawArc(rectF, startCorner, sweepCorner, true, paint)

            if (index == SELECT_INDEX) {
                canvas.restore()
            }

            startCorner += sweepCorner
        }
    }

}