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

package com.lee.blur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.graphics.withSave
import java.lang.ref.WeakReference
import kotlin.math.ceil

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/13 15:23.
 */

class BlockingBlurController(
    blurView: BlurView,
    rootView: View,
    private var overlayColor: Int = Color.TRANSPARENT
) : BlurController {

    private var blurRadius = DEFAULT_BLUR_RADIUS
    private var hasFixedTransformationMatrix = false

    private val blurViewLocation = intArrayOf(0, 0)
    private val rootViewLocation = intArrayOf(0, 0)

    private var blurEnable = true

    private lateinit var internalBitmap: Bitmap
    private lateinit var internalCanvas: Canvas
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var blurView = WeakReference(blurView)
    private var rootView = WeakReference(rootView)

    private var blurAlgorithm: BlurAlgorithm = NullBlurAlgorithm()

    private var initialized = false

    private var scaleX = 1f
    private var scaleY = 1f

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        /**
         * Not invalidating a View here, just updating the Bitmap.
         * This relies on the HW accelerated bitmap drawing behavior in Android
         * If the bitmap was drawn on HW accelerated canvas, it holds a reference to it and on next
         * drawing pass the updated content of the bitmap will be rendered on the screen
         */
        updateBlur()
        true
    }

    init {
        val blurWidth = blurView.measuredWidth
        val blurHeight = blurView.measuredHeight

        if (isZeroSized(blurWidth, blurHeight)) {
            deferBitmapCreation()
        } else {
            init(blurWidth, blurHeight)
        }
    }

    /**
     * 延迟到BlurView执行布局的时候（测量完成之后）初始化
     */
    private fun deferBitmapCreation() {
        blurView.get()?.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    blurView.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                } else {
                    @Suppress("DEPRECATION")
                    blurView.get()?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
                }
                init(blurView.get()!!.measuredWidth, blurView.get()!!.measuredHeight)
            }
        })
    }

    private fun init(width: Int, height: Int) {
        if (isZeroSized(width, height)) {
            blurView.get()?.setWillNotDraw(true)
            return
        }
        blurView.get()?.setWillNotDraw(false)
        allocateBitmap(width, height)
        internalCanvas = Canvas(internalBitmap)

        initialized = true

        if (hasFixedTransformationMatrix) {
            setupInternalCanvasMatrix()
        }
    }

    private fun allocateBitmap(width: Int, height: Int) {
        val nonRoundedScaleWidth = downScaleSize(width)
        val nonRoundedScaleHeight = downScaleSize(height)

        val scaleWidth = roundSize(nonRoundedScaleWidth)
        val scaleHeight = roundSize(nonRoundedScaleHeight)

        scaleX = nonRoundedScaleWidth.div(scaleWidth.toFloat()).times(DEFAULT_SCALE_FACTOR)
        scaleY = nonRoundedScaleHeight.div(scaleHeight.toFloat()).times(DEFAULT_SCALE_FACTOR)

        internalBitmap =
            Bitmap.createBitmap(scaleWidth, scaleHeight, blurAlgorithm.getSupportedBitmapConfig())
    }

    /**
     * Rounds a value to the nearest divisible by {@link #ROUNDING_VALUE} to meet stride requirement
     */
    private fun roundSize(value: Int): Int {
        if (value.rem(ROUNDING_VALUE) == 0) {
            return value
        }
        return value.minus(value.rem(ROUNDING_VALUE)).plus(ROUNDING_VALUE)
    }

    private fun downScaleSize(value: Int): Int {
        return ceil(value.div(DEFAULT_SCALE_FACTOR)).toInt()
    }

    private fun isZeroSized(width: Int, height: Int): Boolean {
        return downScaleSize(width) == 0 || downScaleSize(height) == 0
    }

    override fun draw(canvas: Canvas): Boolean {
        if (!blurEnable || !initialized) {
            return true
        }
        if (canvas == internalCanvas) {
            return false
        }

        updateBlur()

        canvas.withSave {
            scale(scaleX, scaleY)
            drawBitmap(internalBitmap, 0f, 0f, paint)
        }

        if (overlayColor != Color.TRANSPARENT) {
            canvas.drawColor(overlayColor)
        }

        return true
    }

    private fun updateBlur() {
        if (!blurEnable || !initialized) {
            return
        }

        // 将背景根view内容保存至 internalCanvas 配置的 internalBitmap中
        if (hasFixedTransformationMatrix) {
            rootView.get()?.draw(internalCanvas)
        } else {
            internalCanvas.withSave {
                setupInternalCanvasMatrix()
                rootView.get()?.draw(this)
            }
        }
        // 对 internalBitmap进行高斯模糊
        blurAndSave()
    }

    private fun blurAndSave() {
        processTransparentArea()

        internalBitmap = blurAlgorithm.blur(internalBitmap, blurRadius)
        if (!blurAlgorithm.canModifyBitmap()) {
            internalCanvas.setBitmap(internalBitmap)
        }
    }

    /**
     * 处理透明区域 可以模仿 hasFixedTransformationMatrix属性，避免每次绘制之前都处理一次透明区域
     */
    private fun processTransparentArea() {

    }

    private fun setupInternalCanvasMatrix() {
        if (blurView.get() == null || rootView.get() == null) {
            return
        }
        blurView.get()?.getLocationOnScreen(blurViewLocation)
        rootView.get()?.getLocationOnScreen(rootViewLocation)

        internalCanvas.translate(
            -blurViewLocation[0].minus(rootViewLocation[0]).div(scaleX),
            -blurViewLocation[1].minus(rootViewLocation[1]).div(scaleY)
        )
        internalCanvas.scale(1 / scaleX, 1 / scaleY)
    }

    override fun updateBlurViewSize() {
        if (blurView.get() == null) {
            return
        }
        init(blurView.get()!!.measuredWidth, blurView.get()!!.measuredHeight)
    }

    override fun destroy() {
        setBlurAutoUpdate(false)
        blurAlgorithm.destroy()
        initialized = false
    }

    override fun setBlurEnable(enable: Boolean): BlurFacade {
        this.blurEnable = enable
        this.blurView.get()?.invalidate()
        return this
    }

    override fun setBlurAutoUpdate(enable: Boolean): BlurFacade {
        blurView.get()?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
        if (enable) {
            blurView.get()?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
        }
        return this
    }

    override fun setBlurRadius(radius: Float): BlurFacade {
        this.blurRadius = radius
        return this
    }

    override fun setBlurAlgorithm(algorithm: BlurAlgorithm): BlurFacade {
        this.blurAlgorithm = algorithm
        return this
    }

    override fun setHasFixedTransformationMatrix(hasFixedTransformationMatrix: Boolean): BlurFacade {
        this.hasFixedTransformationMatrix = hasFixedTransformationMatrix
        return this
    }

    override fun setOverlayColor(overlayColor: Int): BlurFacade {
        this.overlayColor = overlayColor
        return this
    }

    override fun setTransparentArea() {
        TODO("Not yet implemented")
    }
}