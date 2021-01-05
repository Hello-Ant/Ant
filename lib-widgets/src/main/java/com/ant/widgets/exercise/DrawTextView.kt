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
import android.util.AttributeSet
import android.view.View
import com.ant.base.dp
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 *     文字的绘制 位置的定位有两种方式
 *      1. Paint.getTextBounds 获取文字的边界，针对文字的边界，适合做静态对齐；
 *      2. Paint.getFontMetrics 获取文件的边界，针对字体大小的边界，适合做动态对齐
 * <p>
 *     切边：
 *      使用Paint.getTextBounds可以针对文字精确的切边，只保留字体的padding，该padding系统拿不到；
 *      使用Paint.getFontMetrics根据字体大小在固定范围切边，只能顶部/底部切边；左/右切边需要使用getTextBounds
 * <p>
 *
 * @author Ant
 * @date on 2020/5/3 10:00.
 */
class DrawTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val STROKE_WIDTH = 20.dp
        const val SPORT_PROGRESS = 210f
        const val TEXT = "abab"
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
        textAlign = Paint.Align.CENTER
        textSize = 60.dp
    }
    private val arcBounds = RectF()
    private val textBounds = Rect()

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.div(2f)
        centerY = h.div(2f)
        radius = min(
            w - paddingLeft - paddingRight,
            h - paddingTop - paddingBottom
        ).div(2f) - STROKE_WIDTH

        arcBounds.top = centerX - radius
        arcBounds.left = centerY - radius
        arcBounds.right = centerY + radius
        arcBounds.bottom = centerX + radius
    }

    override fun onDraw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        canvas.drawCircle(centerX, centerY, radius, paint)
        paint.color = Color.MAGENTA
        canvas.drawArc(arcBounds, 270f, SPORT_PROGRESS, false, paint)
        paint.style = Paint.Style.FILL
        paint.getTextBounds(TEXT, 0, TEXT.length, textBounds)
        canvas.drawText(
            TEXT,
            centerX,
            centerY - textBounds.top.plus(textBounds.bottom).div(2f),
            paint
        )
    }
}