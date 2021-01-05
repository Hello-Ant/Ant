package com.lee.ant.route;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.lee.ant.route.test.LoginActivity;
import com.lee.ant.route.test.VerifyTest;

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * 登录验证
 * <p>
 *
 * @author Ant
 * @date on  2020/9/24 16:38.
 */
public class LoginVerify implements Verify {
    private Context mCtx;

    public LoginVerify(@NonNull Context ctx) {
        this.mCtx = ctx;
    }

    @Override
    public boolean isVerified() {
        return VerifyTest.loginVerify;
    }

    @Override
    public void invoke() {
        // 跳转至登录页
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

}
