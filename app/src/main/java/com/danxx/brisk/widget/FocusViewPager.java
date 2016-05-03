package com.danxx.brisk.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Danxx on 2016/4/12.
 */
public class FocusViewPager extends ViewPager {

    private boolean left = false;
    private boolean right = false;
    private boolean isScrolling = false;
    private int lastValue = -1;
    private ChangePagerCallback changePagerCallback = null;

    public FocusViewPager(Context context) {
        super(context);
        init();
    }

    public FocusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        addOnPageChangeListener(listener);
    }

    @Override
    public View focusSearch(int direction) {
        Log.d("danxx", "FVP focusSearch");
        return super.focusSearch(direction);
    }

    @Override
    public View findFocus() {
        Log.d("danxx", "FVP findFocus");
        return super.findFocus();
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        Log.d("danxx", "FVP requestChildFocus");
        super.requestChildFocus(child, focused);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Log.d("danxx", "FVP requestFocus");
        return super.requestFocus(direction, previouslyFocusedRect);
    }



    /**
     * listener ,to get move direction .
     */
    public OnPageChangeListener listener = new OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 1) { //为1说明正在滚动
                isScrolling = true;
            } else {
                isScrolling = false;
            }
            //notify ....
            if (changePagerCallback != null) {
                changePagerCallback.changePager(left, right);
            }
            right = left = false;
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (isScrolling) {
                if (lastValue > positionOffsetPixels) {
                    // 递减，向右侧滑动
                    right = true;
                    left = false;
                } else if (lastValue < positionOffsetPixels) {
                    // 递减，向右侧滑动
                    right = false;
                    left = true;
                } else if (lastValue == positionOffsetPixels) {
                    right = left = false;
                }
            }
            lastValue = positionOffsetPixels;
        }

        @Override
        public void onPageSelected(int position) {
            if (changePagerCallback != null) {
                changePagerCallback.getCurrentPagerIndex(position);
            }
        }
    };


    /**
     * 得到是否向右侧滑动
     *
     * @return true 为右滑动
     */
    public boolean getMoveRight() {
        return right;
    }

    /**
     * 得到是否向左侧滑动
     *
     * @return true 为左做滑动
     */
    public boolean getMoveLeft() {
        return left;
    }

    /**
     * 滑动状态改变回调
     *
     * @author danxingxi
     */
    public interface ChangePagerCallback {
        /**
         * 切换视图 ？决定于left和right 。
         *
         * @param left
         * @param right
         */
        public void changePager(boolean left, boolean right);

        public void getCurrentPagerIndex(int index);
    }

    /**
     * set ...
     *
     * @param callback
     */
    public void setChangeViewCallback(ChangePagerCallback callback) {
        changePagerCallback = callback;
    }
}
