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

package com.ant.widgets.exercise

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import com.example.widgets.R
import com.lee.utilslibrary.BitmapUtils
import kotlin.math.max

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/24 14:08.
 */
class ZoomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private var bigScaleMode = false
    private var bigScale = 1f

    private var bitmap: Bitmap? = null
    private var gestureDetector: GestureDetectorCompat? = null

    private var scroller: OverScroller? = null

    init {
        gestureDetector = GestureDetectorCompat(context, this)
        scroller = OverScroller(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = BitmapUtils.createBitmap(w, h, R.mipmap.ic_avatar, context)
        bigScale = max(w / bitmap!!.width.toFloat(), h / bitmap!!.height.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (bitmap == null) {
            return
        }

        val left: Float
        val top: Float
        if (bigScaleMode) {
            left = (width - bitmap!!.width * bigScale) / 2f
            top = (height - bitmap!!.height * bigScale) / 2f
            canvas.scale(bigScale, bigScale)
        } else {
            left = (width - bitmap!!.width) / 2f
            top = (height - bitmap!!.height) / 2f
        }

        canvas.drawBitmap(bitmap!!, left, top, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector == null) {
            // We're being destroyed; ignore any touch events
            return true
        }
        gestureDetector?.onTouchEvent(event)
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        scroller?.startScroll(0, 0, distanceX.toInt(), distanceY.toInt())
        return false
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        bigScaleMode = !bigScaleMode
        invalidate()
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

}