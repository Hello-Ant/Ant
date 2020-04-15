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
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/8 17:23.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class RenderScriptBlur(ctx: Context) : BlurAlgorithm {

    private var lastBitmapWidth = -1
    private var lastBitmapHeight = -1

    private var aout: Allocation? = null

    private val rs = RenderScript.create(ctx)
    private val sib = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

    override fun blur(bitmap: Bitmap, radius: Float): Bitmap {
        val ain = Allocation.createFromBitmap(rs, bitmap)

        if (!canReuseAllocation(bitmap)) {
            aout?.destroy()
            aout = Allocation.createTyped(rs, ain.type)
            lastBitmapWidth = bitmap.width
            lastBitmapHeight = bitmap.height
        }

        sib.setRadius(radius)
        sib.setInput(ain)
        sib.forEach(aout)

        aout?.copyTo(bitmap)
        ain.destroy()

        return bitmap
    }

    override fun destroy() {
        sib.destroy()
        rs.destroy()
        aout?.destroy()
    }

    override fun canModifyBitmap(): Boolean {
        return true
    }

    override fun getSupportedBitmapConfig(): Bitmap.Config {
        return Bitmap.Config.ARGB_8888
    }

    private fun canReuseAllocation(bitmap: Bitmap): Boolean {
        return bitmap.width == lastBitmapWidth && bitmap.height == lastBitmapHeight
    }

}