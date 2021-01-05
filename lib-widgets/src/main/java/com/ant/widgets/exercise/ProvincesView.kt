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
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.ant.base.dp

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/4 9:58.
 */
class ProvincesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val provinces = arrayOf(
            "北京市",
            "上海市",
            "天津市",
            "重庆市",
            "香港特别行政区",
            "安徽省",
            "江西省",
            "浙江省",
            "山东省",
            "江苏省",
            "辽宁省",
            "黑龙江省",
            "贵州省",
            "河南省",
            "湖北省",
            "湖南省",
            "海南省",
            "广东省",
            "福建省",
            "广西壮族自治区",
            "吉林省",
            "内蒙古"
        )
    }

    private var province = ""
        set(value) {
            field = value
            invalidate()
        }

    private var cx = 0f
    private var baselineY = 0f
    private var offset = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 20.dp
        textAlign = Paint.Align.CENTER
    }
    private val fontMetrics = Paint.FontMetrics()

    init {
        paint.getFontMetrics(fontMetrics)
        offset = fontMetrics.ascent.plus(fontMetrics.descent).div(2f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cx = w.shr(1).toFloat()
        baselineY = h.shr(1).toFloat() - offset
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(province, cx, baselineY, paint)
    }

    class StringEvaluator : TypeEvaluator<String> {
        override fun evaluate(fraction: Float, startValue: String, endValue: String): String {
            val startIndex = provinces.indexOf(startValue)
            val endIndex = provinces.indexOf(endValue)
            return provinces[endIndex.minus(startIndex).times(fraction).toInt().plus(startIndex)]
        }
    }

    private var anim: ValueAnimator? = null
    fun startAnim() {
        anim?.cancel()
        anim = ObjectAnimator.ofObject(this, "province", StringEvaluator(), "北京市", "内蒙古").apply {
            duration = provinces.size.times(200).toLong()
            interpolator = AccelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                province = animatedValue as String
            }
        }
        anim?.start()
    }

    fun stopAnim() {
        anim?.cancel()
    }
}