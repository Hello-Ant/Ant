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

import android.graphics.Canvas

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
internal const val DEFAULT_SCALE_FACTOR = 8f
internal const val DEFAULT_BLUR_RADIUS = 16f

interface BlurController : BlurFacade {
    /**
     * 高斯模糊，并将模糊的图片绘制到画布上
     */
    fun draw(canvas: Canvas): Boolean

    /**
     * 更新高斯模糊图片尺寸
     */
    fun updateBlurViewSize()

    /**
     * 释放资源
     */
    fun destroy()
}