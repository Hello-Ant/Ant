/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.lee.ant;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class AdDialogTestSize extends DialogFragment {
    private static final String TAG = "AdDialogTestSize";

    private ValueAnimator mAnimator;

    private static volatile AdDialogTestSize mInstance;

    private AdDialogTestSize() {
    }

    public static AdDialogTestSize getInstance() {
        if (mInstance == null) {
            synchronized (AdDialogTestSize.class) {
                if (mInstance == null) {
                    mInstance = new AdDialogTestSize();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void show(@NotNull FragmentManager manager, String tag) {
        try {
            Field mDismissed = getClass().getSuperclass().getDeclaredField("mDismissed");
            Field mShownByMe = getClass().getSuperclass().getDeclaredField("mShownByMe");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "show: " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, "show: " + e.getMessage());
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 展示广告
     */
    public void showAd(@NonNull FragmentManager fm) {
        show(fm, TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dialog_advertising_test, container, false);
//        bindViewData(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*view.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onViewCreated: width: " + view.getWidth() + "#height: " + view.getHeight());
                final View iv = view.findViewById(R.id.ivAd);
                Log.d(TAG, "onViewCreated: " + iv.getWidth() + "#" + iv.getHeight());
            }
        }, 50L);*/
        final View iv = view.findViewById(R.id.ivAd);
        iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "onViewCreated: width: " + view.getWidth() + "#height: " + view.getHeight());
                Log.d(TAG, "onViewCreated: " + iv.getWidth() + "#" + iv.getHeight());
            }
        });
    }

    private void bindViewData(final ViewGroup container) {
        final ImageView ivCenter = new ImageView(getActivity());
//        ivCenter.setAlpha(0f);
        ivCenter.setScaleType(ImageView.ScaleType.FIT_XY);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
//        lp.bottomMargin = (int) getResources().getDimension(R.dimen.ad_bottom_margin);
//        lp.leftMargin = (int) getResources().getDimension(R.dimen.ad_left_margin);
        container.addView(ivCenter, lp);

        /*Glide.with(getContext()).load(R.mipmap.ic_small).asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<Integer, GifDrawable>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<GifDrawable> target, boolean isFirstResource) {
                dismiss();
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Integer model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e(TAG, "run: " + mWidth + "&" + mHeight);
                Log.e(TAG, "onResourceReady: ");
                ivCenter.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWidth = ivCenter.getWidth();
                        mHeight = ivCenter.getHeight();
                        mTranslationWidth = lp.leftMargin + mWidth;
                        mTranslationHeight = container.getHeight() >> 1;
                        Log.e(TAG, "run: " + mWidth + "&" + mHeight);
                        startAnimator(ivCenter, Location.CENTER);
                    }
                }, 50L);
                return false;
            }
        }).into(ivCenter);*/
    }

    private float mFraction;
    private int mWidth;
    private int mHeight;
    private int mTranslationWidth;
    private int mTranslationHeight;

    private void startAnimator(final View view, final Location location) {
        mAnimator = ValueAnimator.ofFloat(0, 1).setDuration(5000L);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = animation.getAnimatedFraction();
                if (location == Location.CENTER) {
                    centerAnimator(view, mFraction);
                } else {
                    cornerAnimator(view, mFraction);
                }
            }
        });
        mAnimator.start();
    }

    private void centerAnimator(View view, float fraction) {
//        view.setAlpha(fraction);
        view.setScaleX(fraction * 0.7f + 0.3f);
        view.setScaleY(fraction * 0.7f + 0.3f);
        view.setTranslationY(mTranslationHeight * fraction - (mHeight >> 1));
    }

    private void cornerAnimator(View view, float fraction) {
        /*view.setAlpha(fraction);
        view.setScaleX(fraction);
        view.setScaleY(fraction);*/
        view.setTranslationX(-mTranslationWidth * (1 - fraction));
//        view.setTranslationY(mTranslationWidth * (1 - fraction));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        final Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(.3f);
    }

    @Override
    public void onDestroyView() {
        if (mAnimator != null) {
            mAnimator.end();
            mAnimator.cancel();
            mAnimator = null;
        }
        View container = getView();
        if (container instanceof ViewGroup) {
            ((ViewGroup) container).removeAllViews();
        }
        super.onDestroyView();
    }

    @Override
    public void dismiss() {
        Log.e("AdDialog", "dismiss: " + mInstance.getFragmentManager());
        if (mInstance.getFragmentManager() == null) {
            return;
        }
        super.dismiss();
    }

    enum Location {
        CENTER, CONNER
    }
}
