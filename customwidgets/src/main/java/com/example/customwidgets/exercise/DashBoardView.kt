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
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lee.utilslibrary.dp2px
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
 * @date on 2020/4/9 14:48.
 */
class DashBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val openCorner = 120f

    private val ringWidth = 2f.dp2px
    private val dashWidth = 2f.dp2px
    private val dashLength = 10f.dp2px
    private val pointWidth = 3f.dp2px

    private var dash: Path = Path()
    private lateinit var dashEffect: PathDashPathEffect
    private var path: Path = Path()
    private val pathMeasure = PathMeasure()

    private var cx = 0f
    private var cy = 0f
    private var radius = 0f

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pointPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = ringWidth
        pointPaint.style = Paint.Style.STROKE
        pointPaint.strokeWidth = pointWidth
        pointPaint.color = Color.RED
        pointPaint.strokeCap = Paint.Cap.ROUND
        dash.addRect(0f, 0f, dashWidth, dashLength, Path.Direction.CCW)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cx = w / 2f
        cy = h / 2f
        radius = min(cx, cy) * 0.8f

        path.addArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            90 + openCorner / 2,
            360 - openCorner
        )
        pathMeasure.setPath(path, false)
        dashEffect =
            PathDashPathEffect(
                dash,
                (pathMeasure.length - dashWidth) / 20,
                0f,
                PathDashPathEffect.Style.ROTATE
            )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
        paint.pathEffect = dashEffect
        canvas.drawPath(path, paint)
        paint.pathEffect = null

        drawPoint(canvas, 10)
        canvas.drawCircle(cx, cy, pointWidth, pointPaint)
    }

    private fun drawPoint(canvas: Canvas, index: Int) {
        canvas.drawLine(
            cx,
            cy + 20,
            cx + cos(Math.toRadians(90.toDouble() + openCorner / 2 + (360 - openCorner) / 20f * index)).toFloat() * radius * 0.7f,
            cy + sin(Math.toRadians(90.toDouble() + openCorner / 2 + (360 - openCorner) / 20f * index)).toFloat() * radius * 0.7f,
            pointPaint
        )
    }
}