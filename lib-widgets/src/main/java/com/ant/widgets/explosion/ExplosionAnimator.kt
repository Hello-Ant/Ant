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

package com.example.widgets.explosion

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.example.widgets.explosion.factory.ParticleFactory

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/3 10:29.
 */
class ExplosionAnimator(
    private val container: View,
    bitmap: Bitmap?,
    bound: Rect,
    particleFactory: ParticleFactory
) :
    ValueAnimator() {

    private val paint = Paint()
    private val particles = particleFactory.generateParticle(bitmap, bound)

    init {
        setFloatValues(0f, 1f)
        duration = DEFAULT_DURATION
    }

    override fun start() {
        super.start()
        container.invalidate()
    }

    fun dance(canvas: Canvas) {
        if (!isStarted) {
            return
        }
        for (index in particles.indices) {
            particles[index].dance(animatedValue as Float, canvas, paint)
        }

        container.invalidate()
    }

    companion object {
        const val DEFAULT_DURATION = 0x400L
    }
}