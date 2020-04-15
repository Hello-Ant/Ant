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
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class AdDialog extends DialogFragment {
    private static final String TAG_DIALOG = "AdDialog";

    private ValueAnimator mAnimator;

    private static volatile AdDialog mInstance;

    @SuppressLint("ValidFragment")
    private AdDialog() {
    }

    public static AdDialog getInstance() {
        if (mInstance == null) {
            synchronized (AdDialog.class) {
                if (mInstance == null) {
                    mInstance = new AdDialog();
                }
            }
        }
        return mInstance;
    }

    /**
     * 展示广告
     */
    public void showAd(@NonNull FragmentManager fm) {
        show(fm, TAG_DIALOG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dialog_advertising, container, false);
        bindViewData(view);
        return view;
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

        Glide.with(getContext()).load(R.mipmap.ic_small).asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<Integer, GifDrawable>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<GifDrawable> target, boolean isFirstResource) {
                dismiss();
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Integer model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e(TAG_DIALOG, "run: " + mWidth + "&" + mHeight);
                Log.e(TAG_DIALOG, "onResourceReady: ");
                ivCenter.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWidth = ivCenter.getWidth();
                        mHeight = ivCenter.getHeight();
                        mTranslationWidth = lp.leftMargin + mWidth;
                        mTranslationHeight = container.getHeight() >> 1;
                        Log.e(TAG_DIALOG, "run: " + mWidth + "&" + mHeight);
                        startAnimator(ivCenter, Location.CENTER);
                    }
                }, 50L);
                return false;
            }
        }).into(ivCenter);
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
