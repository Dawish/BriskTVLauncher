package com.danxx.brisktvlauncher.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Base RecyclerViewHolder
 * Created by Danxingxi on 2016/3/31.
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract View getView();

    /**
     * 为item添加动画
     * @param view 需要添加动画的item
     * @param scale 是否得到焦点
     */
    public void animItem(View view,boolean scale){
        float toValue = 1.0f;
        if(scale) {  //得到焦点
            toValue = 1.1f;
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", toValue);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", toValue);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animatorX, animatorY);
            animatorSet.start();
        }else{   //失去焦点
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", toValue);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", toValue);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animatorX, animatorY);
            animatorSet.start();
        }
    }
}
