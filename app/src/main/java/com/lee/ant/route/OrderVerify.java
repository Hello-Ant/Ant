package com.lee.ant.route;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.lee.ant.route.test.OrderActivity;
import com.lee.ant.route.test.VerifyTest;

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/9/24 16:41.
 */
public class OrderVerify implements Verify {
    private Context mCtx;

    public OrderVerify(@NonNull Context ctx) {
        this.mCtx = ctx;
    }

    @Override
    public boolean isVerified() {
        return VerifyTest.orderVerify;
    }

    @Override
    public void invoke() {
        // 跳转至订购页
        mCtx.startActivity(new Intent(mCtx, OrderActivity.class));
    }
}
