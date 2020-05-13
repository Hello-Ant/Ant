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

package com.example.widgets.explosion.particle

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.example.widgets.explosion.factory.FallingParticleFactory
import java.util.*

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/7 16:29.
 */
class FallingParticle(x: Float, y: Float, color: Int, private val bound: Rect) :
    Particle(x, y, color) {
    private var alpha = 1f
    private var radius = FallingParticleFactory.PART_WH.toFloat()

    override fun calculate(factor: Float) {
        x += factor * RANDOM.nextInt(bound.width()) * (RANDOM.nextFloat() - 0.5f)
        y += factor * RANDOM.nextInt(bound.height() / 2)

        radius -= factor * RANDOM.nextInt(2)

        alpha = (1f - factor) * (1f + RANDOM.nextFloat())
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        paint.color = color
        paint.alpha = (Color.alpha(color) * alpha).toInt()
        canvas.drawCircle(x, y, radius, paint)
    }

    companion object {
        val RANDOM = Random()
    }
}