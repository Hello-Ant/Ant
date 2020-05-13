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
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.lee.utilslibrary.dp
import java.util.*
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/9 15:53.
 */
class ClockView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var cx = 0f
    private var cy = 0f
    private var radius = 0f

    private val defaultWidth = 300f.dp.toInt()
    private val defaultHeight = 300f.dp.toInt()

    // 时钟圆环宽度
    private val ringWidth = 5f.dp
    private val ringColor = Color.RED

    private val hColor = Color.BLACK
    private val mColor = Color.BLACK
    private val sColor = Color.RED

    private var hWidth = 15f.dp
    private var mWidth = 10f.dp
    private var sWidth = 5f.dp

    private val defaultLength = 10f.dp
    private val specialLength = 20f.dp

    private val num = arrayOf("12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")

    private val textBound = Rect()

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val numPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mH = 30f
    private var mM = 60f
    private var mS = 120f

    private val calendar = Calendar.getInstance()

    init {
        circlePaint.style = Paint.Style.STROKE
        numPaint.style = Paint.Style.FILL
        numPaint.textSize = 60f
        pointPaint.style = Paint.Style.STROKE
        pointPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resolveSize(defaultWidth, widthMeasureSpec),
            resolveSize(defaultHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cx = w.div(2f)
        cy = h.div(2f)
        radius = min(cx, cy) * 0.8f
    }

    override fun onDraw(canvas: Canvas) {
        // 画布平移至中心
        canvas.translate(cx, cy)
        // 画圆环
        drawBasePlate(canvas)
        // 绘制数字
        drawNum(canvas)
        // 绘制指针
        drawPointer(canvas)
        // 绘制小圆点
        canvas.drawCircle(0f, 0f, hWidth.div(2), pointPaint)
        // 一秒绘制一次
        postDelayed({ invalidate() }, 1000L)
    }

    private fun drawPointer(canvas: Canvas) {
        // 计算日期时间 确定指针角度
        calculateDate()
        // 时针
        canvas.save()
        pointPaint.color = hColor
        pointPaint.strokeWidth = hWidth
        canvas.rotate(mH)
        canvas.drawLine(0f, 20f, 0f, -radius.times(0.45f), pointPaint)
        canvas.restore()
        // 分针
        canvas.save()
        pointPaint.color = mColor
        pointPaint.strokeWidth = mWidth
        canvas.rotate(mM)
        canvas.drawLine(0f, 20f, 0f, -radius.times(0.6f), pointPaint)
        canvas.restore()
        // 秒针
        canvas.save()
        pointPaint.color = sColor
        pointPaint.strokeWidth = sWidth
        canvas.rotate(mS)
        canvas.drawLine(0f, 40f, 0f, -radius.times(0.75f), pointPaint)
        canvas.restore()
    }

    private fun drawNum(canvas: Canvas) {
        for (index in 0 until 12) {
            canvas.save()
            // 在旋转30度的基础上平移
            canvas.translate(0f, specialLength.plus(defaultLength).plus(ringWidth).minus(radius))
            numPaint.getTextBounds(num[index], 0, num[index].length, textBound)

            // 还原旋转角度 绘制文字（为了让文字绘制的方向正确）
            canvas.rotate(-index.times(30f))

            canvas.drawText(
                num[index],
                -textBound.width().div(2f),
                textBound.height().div(2f),
                numPaint
            )
            canvas.restore()
            // 旋转30度 旋转了360度，所以不需要还原
            canvas.rotate(30f)
        }
    }

    private fun drawBasePlate(canvas: Canvas) {
        circlePaint.color = ringColor
        circlePaint.strokeWidth = ringWidth
        canvas.drawCircle(0f, 0f, radius, circlePaint)
        // 画刻度
        for (index in 0 until 60) {
            if (index.rem(5) == 0) {
                circlePaint.color = hColor
                canvas.drawLine(
                    0f,
                    ringWidth.div(2).minus(radius),
                    0f,
                    specialLength.minus(radius),
                    circlePaint
                )
            } else {
                circlePaint.color = sColor
                canvas.drawLine(
                    0f,
                    ringWidth.div(2).minus(radius),
                    0f,
                    defaultLength.minus(radius),
                    circlePaint
                )
            }
            // 旋转6度 总共60个刻度，刚好360度
            canvas.rotate(6f)
        }
    }

    private fun calculateDate() {
        calendar.timeInMillis = System.currentTimeMillis()
        setTime(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }

    private fun setTime(h: Int, m: Int, s: Int) {
        if (h >= 24 || h < 0 || m >= 60 || m < 0 || s >= 60 || s < 0) {
            return
        }
        mH = if (h >= 12) {
            h.plus(m.div(60f)).plus(s.div(3600f)).minus(12).times(30f)
        } else {
            h.plus(m.div(60f)).plus(s.div(3600f)).times(30f)
        }
        mM = m.plus(s.div(60f)).times(6)
        mS = s.times(6f)
    }
}