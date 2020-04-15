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

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/4/13 14:59.
 */
interface BlurFacade {
    /**
     * true 可以高斯模糊；false 不可高斯模糊 默认true
     */
    fun setBlurEnable(enable: Boolean): BlurFacade

    /**
     * true 自动更新模糊；默认为true
     */
    fun setBlurAutoUpdate(enable: Boolean): BlurFacade

    /**
     * 设置模糊半径
     */
    fun setBlurRadius(radius: Float): BlurFacade

    /**
     * 设置模糊算法
     */
    fun setBlurAlgorithm(algorithm: BlurAlgorithm): BlurFacade

    /**
     * 设置是否固定的canvas矩阵变换方式(true 矩阵变换只会初始化一次；false 每次绘制之前都会进行矩阵变换)
     */
    fun setHasFixedTransformationMatrix(hasFixedTransformationMatrix: Boolean): BlurFacade

    fun setOverlayColor(overlayColor: Int): BlurFacade

    /**
     * 设置透明区域(视频区域获取的像素全是透明的)，透明区域高斯模糊得到的还是清晰的透明图片
     * TODO(不太好解耦处理，区域可能是各种形状的区域)
     */
    fun setTransparentArea()
}