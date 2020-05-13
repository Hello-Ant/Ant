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

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.widgets.R
import com.lee.utilslibrary.BitmapUtils
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/3 12:59.
 */
class FlipBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val camera = Camera()
    private lateinit var bitmap: Bitmap
    private var bitmapSize = 0f

    private var centerX = 0f
    private var centerY = 0f

    private var cameraRotateX = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var cameraRotateRight = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var canvasRotate = 0f
        set(value) {
            field = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.shr(1).toFloat()
        centerY = h.shr(1).toFloat()

        bitmapSize = min(
            w - paddingLeft - paddingRight,
            h - paddingTop - paddingBottom
        ).times(0.7f)
        bitmap = BitmapUtils.createBitmap(bitmapSize, R.mipmap.ic_avatar, context)

        rectTop.left = -centerX
        rectTop.right = centerX
        rectTop.top = -centerY
        rectTop.bottom = 0f

        rectBottom.left = -centerX
        rectBottom.right = centerX
        rectBottom.top = 0f
        rectBottom.bottom = centerY
    }

    private val rectTop = RectF()
    private val rectBottom = RectF()

    override fun onDraw(canvas: Canvas) {
        val rotate = canvasRotate

        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(rotate)

        camera.save()
        camera.rotateX(cameraRotateRight)
        camera.applyToCanvas(canvas)
        camera.restore()

        canvas.clipRect(rectTop)
        canvas.rotate(-rotate)
        canvas.translate(-centerY, -centerY)
        canvas.drawBitmap(
            bitmap,
            centerX - bitmap.width.div(2f),
            centerY - bitmap.height.div(2f),
            null
        )
        canvas.restore()

        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(rotate)

        camera.save()
        camera.rotateX(cameraRotateX)
        camera.applyToCanvas(canvas)
        camera.restore()

        canvas.clipRect(rectBottom)
        canvas.rotate(-rotate)
        canvas.translate(-centerY, -centerY)
        canvas.drawBitmap(
            bitmap,
            centerX - bitmap.width.div(2f),
            centerY - bitmap.height.div(2f),
            null
        )
        canvas.restore()
    }

    fun startAnim() {
        canvasRotate = 0f
        cameraRotateX = 0f
        cameraRotateRight = 0f

        val cameraAngle = 40f

        val animEnd = ObjectAnimator.ofFloat(this, "cameraRotateRight", 0f, -cameraAngle).apply {
            duration = 1000
        }
        val animCamera = ObjectAnimator.ofFloat(this, "canvasRotate", 0f, -270f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                cameraRotateX = if (animatedFraction >= 0.5) {
                    cameraAngle * (animatedFraction - 0.5f) + cameraAngle / 2f
                } else {
                    cameraAngle * (1 - animatedFraction)
                }
            }
        }
        val animStart = ObjectAnimator.ofFloat(this, "cameraRotateX", 0f, cameraAngle).apply {
            duration = 1000
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(animStart, animCamera, animEnd)
        animatorSet.start()
    }
}