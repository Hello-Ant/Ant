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
@file:JvmName("DefaultConfig")

package com.lee.blurlibrary

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/27 14:01.
 */

internal const val DEFAULT_SCALE_FACTOR = 8f
internal const val DEFAULT_BLUR_RADIUS = 16f

/**
 * Bitmap size should be divisible by ROUNDING_VALUE to meet stride requirement.
 * This will help avoiding an extra bitmap allocation when passing the bitmap to RenderScript for blur.
 * Usually it's 16, but on Samsung devices it's 64 for some reason.
 */
internal const val ROUNDING_VALUE = 64