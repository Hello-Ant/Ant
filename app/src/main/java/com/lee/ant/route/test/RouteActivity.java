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

package com.lee.ant.route.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lee.ant.R;
import com.lee.ant.route.AuthVerify;
import com.lee.ant.route.LoginVerify;
import com.lee.ant.route.OrderVerify;
import com.lee.ant.route.Route;

public class RouteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_act_route);
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            Route.getInstance()
                    .setAction(() -> startActivity(new Intent(RouteActivity.this, TargetActivity.class)))
                    .addVerify(new LoginVerify(RouteActivity.this))
                    .navigation();
        });
        findViewById(R.id.btn_order).setOnClickListener(v -> {
            Route.getInstance()
                    .setAction(() -> startActivity(new Intent(RouteActivity.this, TargetActivity.class)))
                    .addVerify(new LoginVerify(RouteActivity.this))
                    .addVerify(new OrderVerify(RouteActivity.this))
                    .navigation();
        });
        findViewById(R.id.btn_auth).setOnClickListener(v -> {
            Route.getInstance()
                    .setAction(() -> startActivity(new Intent(RouteActivity.this, TargetActivity.class)))
                    .addVerify(new AuthVerify(RouteActivity.this))
                    .navigation();
        });
        findViewById(R.id.btn_clear_login).setOnClickListener(v -> {
            VerifyTest.loginVerify = false;
        });
        findViewById(R.id.btn__clear_order).setOnClickListener(v -> {
            VerifyTest.orderVerify = false;
        });
        findViewById(R.id.btn__clear_token).setOnClickListener(v -> {
            VerifyTest.tokenVerify = false;
        });
    }
}
