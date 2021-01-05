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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.widgets.R
import com.lee.utilslibrary.BitmapUtils
import com.ant.base.dp

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/3 10:24.
 */
class BreakTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TEXT =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam condimentum at velit ut hendrerit. Praesent sed lorem a arcu venenatis congue nec in justo. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut varius porttitor risus, commodo egestas purus consectetur laoreet. Vivamus ac ipsum turpis. Pellentesque blandit quam id nisi lobortis consequat. Curabitur varius sed massa quis dapibus. Fusce elit tortor, eleifend id commodo ut, volutpat et sapien. Aliquam vitae elit et quam egestas mollis. Nullam tincidunt magna quis velit porttitor scelerisque."
    }

    private val fontMetrics = Paint.FontMetrics()
    private val startOffset: Float

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = 20.dp
    }

    private var centerX = 0f
    private var centerY = 0f

    private val measuredWidth = floatArrayOf(0f)
    private lateinit var bitmap: Bitmap

    init {
        paint.getFontMetrics(fontMetrics)
        startOffset = fontMetrics.top
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.shr(1).toFloat()
        centerY = h.shr(1).toFloat()

        bitmap =
            BitmapUtils.createBitmap(50.dp, R.mipmap.ic_avatar, context)
    }

    override fun onDraw(canvas: Canvas) {
        val bitmapLeft = width - bitmap.width.toFloat()
        val bitmapTop = 70.dp
        canvas.drawBitmap(bitmap, bitmapLeft, bitmapTop, paint)

        var start = 0
        var offset = -startOffset
        while (start < TEXT.length) {
            val textWidth =
                if (offset + fontMetrics.bottom > bitmapTop && offset + fontMetrics.top < bitmapTop + bitmap.height) {
                    bitmapLeft
                } else {
                    width.toFloat()
                }
            val count =
                paint.breakText(TEXT, start, TEXT.length, true, textWidth, measuredWidth)
            canvas.drawText(TEXT, start, start + count, 0f, offset, paint)
            start += count
            offset += paint.fontSpacing
        }
    }
}