package com.danxx.brisktvlauncher.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.danxx.brisktvlauncher.R;

/**
 * Created by Danxx on 2016/4/14.
 */
public class BaseActivity extends AppCompatActivity {

    protected PopupWindow ppWindow;
    protected View contentView;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPpWindow();
    }

    protected void initPpWindow(){
        contentView = findViewById(R.id.layoutContent);
        View popView = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow ,null ,false);
        ppWindow = new PopupWindow(popView , ViewGroup.LayoutParams.MATCH_PARENT ,getWindowManager().getDefaultDisplay().getHeight()/5);
        ppWindow.setAnimationStyle(R.style.popwin_anim_style);
        ppWindow.setFocusable(true);
        ppWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_popupwindow_bg));
//        UIUtils.setPopupWindowTouchModal(ppWindow, false);
        ((ViewGroup)ppWindow.getContentView()).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showPpWindow();
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ppWindow.isShowing()) {
                ppWindow.dismiss();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    protected void showPpWindow(){
        if(contentView != null){
            if(!ppWindow.isShowing()){
                ppWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
                ppWindow.getContentView().requestFocus();

            }else {
                ppWindow.dismiss();
            }
        }
        else {
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_up_in, R.anim.activity_up_out);
    }
}
