package com.danxx.brisktvlauncher.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 * 拦截焦点分发
 * Created by danxixing on 2016/4/14
 */
public class FocusListenerLinearLayout extends LinearLayout {
    /**
     * 焦点寻找接口
     */
    public interface OnFocusSearchListener {
        public View onFocusSearch(View focused, int direction);
    }

    /**
     * 子view请求焦点接口
     */
    public interface OnChildFocusListener {
        public boolean onRequestFocusInDescendants(int direction,
                                                   Rect previouslyFocusedRect);
        public void onRequestChildFocus(View child, View focused);
    }

    /**
     * 子view焦点变化监听接口
     */
    public interface OnChildFocusChangeListener {
        public void focusChange(View oldView , View newView);
    }

    private OnFocusSearchListener mListener;
    private OnChildFocusListener mOnChildFocusListener;
    private OnChildFocusChangeListener mOnChildFocusChangeListener;

    public void setOnFocusSearchListener(OnFocusSearchListener listener) {
        mListener = listener;
    }

    public OnFocusSearchListener getOnFocusSearchListener() {
        return mListener;
    }

    public void setOnChildFocusListener(OnChildFocusListener listener) {
        mOnChildFocusListener = listener;
    }

    public OnChildFocusListener getOnChildFocusListener() {
        return mOnChildFocusListener;
    }

    public void setOnChildFocusChangeListener(OnChildFocusChangeListener childFocusChangeListener){
        this.mOnChildFocusChangeListener  = childFocusChangeListener;
    }

    public FocusListenerLinearLayout(Context context) {
        super(context);
        init();
    }

    public FocusListenerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusListenerLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化添加焦点监听器
     */
    private void init(){
        this.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldView, View newView) {
                if(mOnChildFocusChangeListener != null){
                    mOnChildFocusChangeListener.focusChange(oldView ,newView);
                }
            }
        });
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                                                  Rect previouslyFocusedRect) {
        if (mOnChildFocusListener != null) {
            return mOnChildFocusListener.onRequestFocusInDescendants(direction,
                    previouslyFocusedRect);
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        if (mListener != null) {
            View view = mListener.onFocusSearch(focused, direction);
            if (view != null) {
                return view;
            }
        }
        return super.focusSearch(focused, direction);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (mOnChildFocusListener != null) {
            mOnChildFocusListener.onRequestChildFocus(child, focused);
        }
    }
}
