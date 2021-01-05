package com.lee.ant.route;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * 跳转之前需要执行前置任务的路由 所有的操作全在子线程处理 如果有操作ui的地方需要注意
 * <p>
 *
 * @author Ant
 * @date on  2020/9/24 16:52.
 */
public class Route {
    private static final String TAG = "Route";

    private static final int ROUTE_SET_ACTION = 1;
    private static final int ROUTE_ADD_VERIFY = 2;
    private static final int ROUTE_NAVIGATION = 3;

    private final HandlerThread mHandlerThread;
    private final RouteHandler mHandler;

    /**
     * 验证完之后执行的动作
     */
    private Action mAction;
    /**
     * 顺序验证 使用双端队列，执行验证的过程中可能需要临时插入前置任务
     */
    private Deque<Verify> mVerifies = new ArrayDeque<>();
    /**
     * 当前正在执行的验证
     */
    private Verify mCurVerify;

    public static Route getInstance() {
        return SingletonHolder.instance;
    }

    private Route() {
        mHandlerThread = new HandlerThread(TAG + "-HandlerThread");
        mHandlerThread.start();

        mHandler = new RouteHandler(mHandlerThread.getLooper());
    }

    private static final class SingletonHolder {
        private static final Route instance = new Route();
    }

    public Route setAction(Action action) {
        mHandler.obtainMessage(ROUTE_SET_ACTION, action).sendToTarget();
        return this;
    }

    public Route addVerify(Verify verify) {
        mHandler.obtainMessage(ROUTE_ADD_VERIFY, verify).sendToTarget();
        return this;
    }

    /**
     * 执行前置任务时，该前置任务需要插入临时的前置任务
     * 外部慎重使用，除非你明确知道自己要做什么
     */
    protected Route insetVerify(Verify verify) {
        if (mAction == null) {
            Log.e(TAG, "insetVerify: mAction is null.");
            return this;
        }
        if (mCurVerify != null) {
            // 将当前前置任务重新加入任务队列
            mVerifies.addFirst(mCurVerify);
            mCurVerify = null;
        }
        mVerifies.addFirst(verify);
        return this;
    }

    /**
     * 验证成功调用该方法继续下一步
     * 请确保验证成功再调用此方法，调用此方法默认当前验证已完成，有其他需求再改
     */
    public void navigation() {
        mCurVerify = null;
        mHandler.obtainMessage(ROUTE_NAVIGATION).sendToTarget();
    }

    private void navigationInternal() {
        Log.e(TAG, "navigationInternal: " + Thread.currentThread().getName());
        final Verify next = mCurVerify = mVerifies.poll();
        if (next == null) {
            // 所有验证执行完了 执行真正的操作
            if (mAction != null) {
                mAction.call();
            }
        } else {
            if (next.isVerified()) {
                navigationInternal();
            } else {
                next.invoke();
            }
        }
    }

    public void reset() {
        mVerifies.clear();
        mAction = null;
        mCurVerify = null;
    }

    private class RouteHandler extends Handler {
        RouteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ROUTE_SET_ACTION:
                    reset();
                    mAction = (Action) msg.obj;
                    break;
                case ROUTE_ADD_VERIFY:
                    if (mAction == null) {
                        Log.e(TAG, "handleMessage: Must call setAction() the first.");
                        return;
                    }
                    mVerifies.add((Verify) msg.obj);
                    break;
                case ROUTE_NAVIGATION:
                    navigationInternal();
                    break;
            }
        }
    }
}
