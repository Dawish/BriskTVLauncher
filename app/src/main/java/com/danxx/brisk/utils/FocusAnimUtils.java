package com.danxx.brisk.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by Danxx on 2016/4/18.
 */
public class FocusAnimUtils {

    public static void focusAnim(View view){
        float toValue = 1.1f;
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", toValue);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", toValue);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();

    }

    public static void unFocusAnim(View view){
        float toValue = 1.0f;
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", toValue);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", toValue);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }


}
