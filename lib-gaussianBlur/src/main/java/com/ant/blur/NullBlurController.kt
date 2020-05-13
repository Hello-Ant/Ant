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

import android.graphics.Canvas

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/13 15:50.
 */
class NullBlurController : BlurController {
    override fun draw(canvas: Canvas): Boolean {
        return false
    }

    override fun updateBlurViewSize() {
    }

    override fun destroy() {
    }

    override fun setBlurEnable(enable: Boolean): BlurFacade {
        return this
    }

    override fun setBlurAutoUpdate(enable: Boolean): BlurFacade {
        return this
    }

    override fun setBlurRadius(radius: Float): BlurFacade {
        return this
    }

    override fun setBlurAlgorithm(algorithm: BlurAlgorithm): BlurFacade {
        return this
    }

    override fun setHasFixedTransformationMatrix(hasFixedTransformationMatrix: Boolean): BlurFacade {
        return this
    }

    override fun setOverlayColor(overlayColor: Int): BlurFacade {
        return this
    }

    override fun setTransparentArea() {
    }
}