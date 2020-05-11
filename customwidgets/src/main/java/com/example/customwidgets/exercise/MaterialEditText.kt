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

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.customwidgets.R
import com.lee.utilslibrary.dp

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/4 12:34.
 */
class MaterialEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    companion object {
        private val TEXT_SIZE = 12.dp
        private val TEXT_MARGIN = 8.dp
    }

    private var floatingLabelShow = false
    private var fraction = 0f
        set(value) {
            field = value
            verticalOffset = baseline.minus(startOffset).times(1 - value) + startOffset
            invalidate()
        }
    private var verticalOffset = 0f
    private var startOffset = 0f
    private val fontMetrics = Paint.FontMetrics()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.colorAccent)
        textSize = TEXT_SIZE
    }

    init {
        paint.getFontMetrics(fontMetrics)
        startOffset = paddingTop.minus(fontMetrics.top)
        verticalOffset = startOffset

        setPadding(
            paddingLeft,
            (paddingTop + TEXT_SIZE + TEXT_MARGIN).toInt(),
            paddingRight,
            paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.alpha = fraction.times(0xFF).toInt()
        canvas.drawText(
            hint.toString(),
            paddingLeft.toFloat(),
            verticalOffset,
            paint
        )
    }

    private val floatingLabelAnim: ValueAnimator by lazy {
        ObjectAnimator.ofFloat(this, "fraction", 1f).apply {
            duration = 1000
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (text.isNullOrEmpty()) {
            if (floatingLabelShow) {
                floatingLabelShow = false
                floatingLabelAnim.reverse()
            }
        } else {
            if (!floatingLabelShow) {
                floatingLabelShow = true
                floatingLabelAnim.start()
            }
        }
    }
}