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

package com.lee.ant.route;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lee.ant.route.test.VerifyTest;

public class AuthVerify implements Verify {
    private Context mCtx;

    public AuthVerify(@NonNull Context ctx) {
        this.mCtx = ctx;
    }

    @Override
    public boolean isVerified() {
        if (!VerifyTest.loginVerify) {
            Route.getInstance().insetVerify(new LoginVerify(mCtx));
            return true;
        }
        if (!VerifyTest.tokenVerify) {
            Route.getInstance().insetVerify(new TokenVerify());
            return true;
        }
        if (!VerifyTest.orderVerify) {
            Route.getInstance().insetVerify(new OrderVerify(mCtx));
            return true;
        }
        return true;
    }

    @Override
    public void invoke() {
        // 验证不成功怎么处理
    }
}
