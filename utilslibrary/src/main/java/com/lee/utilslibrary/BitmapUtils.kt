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

package com.lee.utilslibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/3 8:48.
 */
object BitmapUtils {

    /**
     * 获取view里面的内容
     */
    fun createBitmapFromView(v: View): Bitmap? {
        if (v is ImageView && v.drawable is BitmapDrawable) {
            return (v.drawable as BitmapDrawable).bitmap
        }

        // 恢复原始样子
        v.clearFocus()

        val bm = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)

        if (bm != null) {
            val canvas = Canvas()
            canvas.setBitmap(bm)
            v.draw(canvas)
        }

        return bm
    }

    fun createBitmap(size: Float, resId: Int, context: Context): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, resId, options)
//        val scale = max(options.outWidth, options.outHeight).div(size).toInt()
        options.inJustDecodeBounds = false
//        options.inSampleSize = max(1, scale)
        options.inDensity = min(options.outWidth, options.outHeight)
        options.inTargetDensity = size.toInt()

        return BitmapFactory.decodeResource(context.resources, resId, options)
    }

}