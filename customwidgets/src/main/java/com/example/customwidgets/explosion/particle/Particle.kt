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

package com.example.customwidgets.explosion.particle

import android.graphics.Canvas
import android.graphics.Paint

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/3 10:19.
 */
abstract class Particle constructor(
    protected var x: Float,
    protected var y: Float,
    protected var color: Int
) {

    fun dance(factor: Float, canvas: Canvas, paint: Paint) {
        calculate(factor)
        draw(canvas, paint)
    }

    protected abstract fun calculate(factor: Float)
    protected abstract fun draw(canvas: Canvas, paint: Paint)
}