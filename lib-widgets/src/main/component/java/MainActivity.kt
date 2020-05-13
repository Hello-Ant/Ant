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

package com.example.widgets.java

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.widgets.R
import com.example.widgets.explosion.ExplosionField

class MainActivity : AppCompatActivity() {
    private lateinit var explosionField: ExplosionField

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        /*explosionField = ExplosionField(this, FallingParticleFactory())
        explosionField.addListener(iv_explosion)
        explosionField.addListener(dbv)
        explosionField.addListener(cv)
        explosionField.addListener(pv)
        zv.post {
            zv.startAnim()
        }
        tv.post {
            tv.startAnim()
        }
        utv.post {
            utv.startAnim()
        }
        xcv.setOnClickListener { xcv.startAnim() }
        fbv.setOnClickListener { fbv.startAnim() }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        /*zv.stopAnim()
        tv.stopAnim()
        utv.stopAnim()*/
    }
}
