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

package com.example.widgets.exercise

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 *     View修改尺寸有两中实现方式：
 *     重写 onMeasure|layout !!!是layout而不是onLayout，onLayout是用来布局子控件的
 * <p>
 *     width/height & measuredWidth/measuredHeight 的区别
 *     width = mRight - mLeft 控件的左上右下是在layout执行完才有值的，即layout执行完才能拿到width/height
 *     measuredWidth/measuredHeight在测量完尺寸大小onMeasure执行完即可获取
 * <p>
 *
 * @author Ant
 * @date on 2020/5/5 9:40.
 */
class SquareImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    /*override fun layout(l: Int, t: Int, r: Int, b: Int) {
        val size = min(measuredWidth, measuredHeight)
        super.layout(l, t, l + size, t + size)
    }*/

}