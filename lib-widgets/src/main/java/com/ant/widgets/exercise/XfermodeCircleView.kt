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
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withScale
import com.example.widgets.R
import com.lee.utilslibrary.BitmapUtils
import com.ant.base.dp
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 *     使用Xfermode可以抗锯齿，使用Clip不能抗锯齿
 *     使用xfermode需要注意合成图的底色，透明的区域合成之后会看不见
 * <p>
 *
 * @author Ant
 * @date on 2020/5/3 8:45.
 */
class XfermodeCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ringWidth = 3.dp
    private var ringRadius = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = ringWidth.times(2f)
    }

    private val srcIn = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private var bitmap: Bitmap? = null

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var size = 0

    private var scaleFraction = 1f
        set(value) {
            if (field == value) {
                return
            }
            field = value
            invalidate()
        }

    private val anim by lazy {
        ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f).apply {
            duration = 1000
        }
    }

    fun startAnim() {
        anim.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.div(2f)
        centerY = h.div(2f)

        size = min(w - paddingLeft - paddingRight, h - paddingTop - paddingBottom)
        ringRadius = size.div(2f) - ringWidth
        radius = ringRadius - ringWidth
        bitmap?.recycle()
        bitmap =
            BitmapUtils.createBitmap(radius.times(2f), R.mipmap.ic_avatar, context)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.withScale(scaleFraction, scaleFraction, centerX, centerY) {
            val count =
                canvas.saveLayer(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    paint,
                    Canvas.ALL_SAVE_FLAG
                )
            paint.style = Paint.Style.FILL
            canvas.drawCircle(centerX, centerY, radius, paint)
            paint.xfermode = srcIn
            canvas.drawBitmap(
                bitmap!!,
                centerX - radius,
                centerY - radius,
                paint
            )
            canvas.restoreToCount(count)
            paint.xfermode = null
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(centerX, centerY, ringRadius, paint)
        }
    }
}