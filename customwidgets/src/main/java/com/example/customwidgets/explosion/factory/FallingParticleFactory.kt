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

package com.example.customwidgets.explosion.factory

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.customwidgets.explosion.particle.FallingParticle
import com.example.customwidgets.explosion.particle.Particle
import java.util.*
import kotlin.collections.ArrayList

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/3 10:27.
 */
class FallingParticleFactory : ParticleFactory {

    override fun generateParticles(bitmap: Bitmap, bound: Rect): Array<Array<Particle?>> {
        val width = bound.width()
        val height = bound.height()

        val cols = width / PART_WH
        val rows = height / PART_WH

        val bitmapPartWidth = bitmap.width / cols
        val bitmapPartHeight = bitmap.height / rows

        val particles = Array(rows) { arrayOfNulls<Particle>(cols) }

        for (rowIndex in particles.indices) {
            for (colIndex in particles[rowIndex].indices) {
                // 获取粒子颜色
                val color = bitmap.getPixel(colIndex * bitmapPartWidth, rowIndex * bitmapPartHeight)
                val x = bound.left + colIndex * PART_WH
                val y = bound.top + rowIndex * PART_WH
                particles[rowIndex][colIndex] =
                    FallingParticle(x.toFloat(), y.toFloat(), color, bound)
            }
        }

        return particles
    }

    @Suppress("UNCHECKED_CAST")
    override fun generateParticle(bitmap: Bitmap?, bound: Rect): Array<Particle> {
        if (bitmap == null) {
            return Collections.emptyList<Particle>().toTypedArray()
        }

        val width = bound.width()
        val height = bound.height()

        val cols = (width / PART_WH).coerceAtLeast(1)
        val rows = (height / PART_WH).coerceAtLeast(1)

        val bitmapPartWidth = bitmap.width / cols
        val bitmapPartHeight = bitmap.height / rows

        val particles = ArrayList<Particle>(rows * cols)

        for (rowIndex in 0 until rows) {
            for (colIndex in 0 until cols) {
                val color = bitmap.getPixel(colIndex * bitmapPartWidth, rowIndex * bitmapPartHeight)
                val x = bound.left + colIndex * PART_WH
                val y = bound.top + rowIndex * PART_WH
                particles.add(FallingParticle(x.toFloat(), y.toFloat(), color, bound))
            }
        }

        return particles.toTypedArray()
    }

    companion object {
        const val PART_WH = 8
    }
}