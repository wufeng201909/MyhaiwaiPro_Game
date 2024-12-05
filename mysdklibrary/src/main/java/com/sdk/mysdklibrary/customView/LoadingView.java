package com.sdk.mysdklibrary.customView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.sdk.mysdklibrary.Tools.ResourceUtil;

public class LoadingView extends LinearLayout {
    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int size = ResourceUtil.dip2px(context,20);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(size, size);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(param);
        setBackgroundResource(ResourceUtil.getDrawableId(context,"myths_load"));

        ObjectAnimator animator=ObjectAnimator.ofFloat(this,"rotation",0F,360F);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
    }
}
