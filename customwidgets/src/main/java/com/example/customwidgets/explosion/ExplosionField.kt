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

package com.example.customwidgets.explosion

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.get
import com.example.customwidgets.explosion.factory.FallingParticleFactory
import com.example.customwidgets.utils.BitmapUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/3 10:31.
 */
@SuppressLint("ViewConstructor")
class ExplosionField(
    context: Context,
    private val particleFactory: FallingParticleFactory
) : View(context) {

    private var mOnClickListener: OnClickListener? = null

    val explosionAnims = ArrayList<ExplosionAnimator>()
    val explosionAnimMap = HashMap<View, ExplosionAnimator>()

    init {
        attachToWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (index in explosionAnims.indices) {
            explosionAnims[index].dance(canvas)
        }
    }

    private fun attachToWindow() {
        val decorView = (context as Activity).window.decorView as ViewGroup
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        decorView.addView(this, layoutParams)
    }

    private fun preExplodeAnim(view: View) {
        view.animate().apply {
            duration = 150
            scaleX(0f)
            scaleY(0f)
            alpha(0f)
        }.start()
    }

    private fun afterExplodeAnim(view: View) {
        view.animate().apply {
            duration = 150
            scaleX(1f)
            scaleY(1f)
            alpha(1f)
        }.start()
    }

    fun explode(view: View) {
        if (explosionAnimMap[view] != null && explosionAnimMap[view]!!.isStarted) {
            return
        }
        if (view.visibility != VISIBLE || view.alpha == 0f) {
            return
        }

        val bound = Rect()
        // 相对于整个屏幕的高度
        view.getGlobalVisibleRect(bound)

        // 标题栏高度
        val titleBarHeight = (parent as ViewGroup).top
        val frame = Rect()
        (context as Activity).window.decorView.getWindowVisibleDisplayFrame(frame)
        // 状态栏高度
        val statusBarHeight = frame.top
        // 去掉不属于自己的区域
        bound.offset(0, -titleBarHeight - statusBarHeight)
        if (bound.width() == 0 || bound.height() == 0) {
            return
        }

        // 开启震动动画
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 150
            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                val random = Random()
                override fun onAnimationUpdate(animation: ValueAnimator?) {
                    view.translationX = (random.nextFloat() - 0.5f) * view.width * 0.05f
                    view.translationY = (random.nextFloat() - 0.5f) * view.height * 0.05f
                }
            })
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    explode(view, bound)
                }
            })
        }.start()
    }

    private fun explode(view: View, bound: Rect) {
        val anim =
            ExplosionAnimator(
                this,
                BitmapUtil.createBitmapFromView(view),
                bound,
                particleFactory
            )
        explosionAnims.add(anim)
        explosionAnimMap[view] = anim

        anim.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    preExplodeAnim(view)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    afterExplodeAnim(view)

                    explosionAnims.remove(anim)
                    explosionAnimMap.remove(view)
                }
            })
        }.start()
    }

    fun addListener(view: View) {
        if (view is ViewGroup) {
            val count = view.childCount
            for (index in 0..count) {
                addListener(view[index])
            }
        } else {
            view.setOnClickListener(getClickListener())
        }
    }

    private fun getClickListener(): OnClickListener? {
        if (mOnClickListener == null) {
            mOnClickListener = OnClickListener { v -> explode(v) }
        }
        return mOnClickListener
    }
}