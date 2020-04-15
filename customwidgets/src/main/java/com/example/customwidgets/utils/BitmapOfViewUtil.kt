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

package com.example.customwidgets.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/3 16:17.
 */
object BitmapOfViewUtil {

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
}