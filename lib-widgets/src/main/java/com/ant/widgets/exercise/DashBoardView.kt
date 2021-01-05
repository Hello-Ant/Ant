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
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
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
 * @date on 2020/5/3 4:48.
 */
class DashBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TICK_MARK_NUM = 20
        const val OPEN_CORNER = 120f
        val RING_WIDTH = 2.dp

        val DASH_WIDTH = 2.dp
        val DASH_LENGTH = 10.dp

        val POINTER_WIDTH = 3.dp
    }

    private var startAngle = 0f
    private var sweepAngle = 0f

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = RING_WIDTH
    }
    private val rectF = RectF()
    private val path = Path()
    private val pathMeasure = PathMeasure()

    /**
     * 必须设置虚线的间距，不然无效
     */
    private lateinit var pathDashPathEffect: PathDashPathEffect

    private val dashPath = Path()

    private var pointerLength = 0f
    private var pointerExtraLength = 0f

    private var centerX = 0f
    private var centerY = 0f
    private val pointerPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = POINTER_WIDTH
    }

    init {
        startAngle = OPEN_CORNER.div(2f) + 90
        sweepAngle = 360 - OPEN_CORNER

        dashPath.addRect(0f, 0f, DASH_WIDTH, DASH_LENGTH, Path.Direction.CCW)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.shr(1).toFloat()
        centerY = h.shr(1).toFloat()

        rectF.top = paddingTop.toFloat() + RING_WIDTH
        rectF.left = paddingLeft.toFloat() + RING_WIDTH
        rectF.right = w - paddingRight.toFloat() - RING_WIDTH
        rectF.bottom = h - paddingBottom.toFloat() - RING_WIDTH
        path.addArc(rectF, startAngle, sweepAngle)

        pathMeasure.setPath(path, false)
        pathDashPathEffect = PathDashPathEffect(
            dashPath,
            pathMeasure.length.minus(DASH_WIDTH).div(TICK_MARK_NUM),
            0f,
            PathDashPathEffect.Style.ROTATE
        )

        pointerLength = (w - paddingRight - paddingLeft) / 2f * 0.8f
        pointerExtraLength = pointerLength * 0.1f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
        paint.pathEffect = pathDashPathEffect
        canvas.drawPath(path, paint)
        paint.pathEffect = null

        drawPointer(canvas, 20)
        canvas.drawCircle(centerX, centerY, RING_WIDTH, pointerPaint)
    }

    private fun drawPointer(canvas: Canvas, index: Int) {
        val corner = startAngle + index * sweepAngle.div(TICK_MARK_NUM).toDouble()
        val radians = Math.toRadians(corner)
        val cosValue = cos(radians).toFloat()
        val sinValue = sin(radians).toFloat()
        canvas.drawLine(
            centerX - cosValue * pointerExtraLength,
            centerY - sinValue * pointerExtraLength,
            centerX + cosValue * pointerLength,
            centerY + sinValue * pointerLength,
            pointerPaint
        )
    }
}