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

package com.lee.blurlibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/13 14:36.
 */
class BlurView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BlurFacade {

    private var blurController: BlurController = NullBlurController()

    private var overlayColor: Int

    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0)
        overlayColor = a.getColor(R.styleable.BlurView_blurOverlayColor, Color.TRANSPARENT)
        a.recycle()
    }

    override fun draw(canvas: Canvas) {
        if (!blurController.draw(canvas))
            super.draw(canvas)
    }

    fun setupWith(rootView: View): BlurFacade {
        blurController.destroy()
        blurController = BlockingBlurController(this, rootView, overlayColor)
        return blurController
    }

    override fun setBlurEnable(enable: Boolean): BlurFacade {
        return blurController.setBlurEnable(enable)
    }

    override fun setBlurAutoUpdate(enable: Boolean): BlurFacade {
        return blurController.setBlurAutoUpdate(enable)
    }

    override fun setBlurRadius(radius: Float): BlurFacade {
        return blurController.setBlurRadius(radius)
    }

    override fun setBlurAlgorithm(algorithm: BlurAlgorithm): BlurFacade {
        return blurController.setBlurAlgorithm(algorithm)
    }

    override fun setHasFixedTransformationMatrix(hasFixedTransformationMatrix: Boolean): BlurFacade {
        return blurController.setHasFixedTransformationMatrix(hasFixedTransformationMatrix)
    }

    override fun setOverlayColor(overlayColor: Int): BlurFacade {
        this.overlayColor = overlayColor
        return blurController.setOverlayColor(overlayColor)
    }

    override fun setTransparentArea() {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        blurController.updateBlurViewSize()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        blurController.setBlurAutoUpdate(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        blurController.setBlurAutoUpdate(false)
    }
}