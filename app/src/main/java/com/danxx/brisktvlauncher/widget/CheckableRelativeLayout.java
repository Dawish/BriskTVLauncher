package com.danxx.brisktvlauncher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by Danxx on 2016/4/29.
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable{

    private static final int CHECKABLE_CHILD_INDEX = 1;
    private Checkable child;

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child = (Checkable) getChildAt(CHECKABLE_CHILD_INDEX);
    }

    @Override
    public boolean isChecked() {
        return child.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        child.setChecked(checked);
    }

    @Override
    public void toggle() {
        child.toggle();
    }
}
