package com.danxx.brisktvlauncher.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danxx.brisktvlauncher.R;
import com.danxx.brisktvlauncher.adapter.BaseRecyclerViewAdapter;
import com.danxx.brisktvlauncher.adapter.BaseRecyclerViewHolder;
import com.danxx.brisktvlauncher.base.BaseActivity;
import com.danxx.brisktvlauncher.model.AppBean;
import com.danxx.brisktvlauncher.utils.FocusAnimUtils;
import com.danxx.brisktvlauncher.utils.ReadAllApp;
import com.danxx.brisktvlauncher.widget.FocusListenerLinearLayout;
import com.danxx.brisktvlauncher.widget.SpaceItemDecoration;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.recycle.OnChildSelectedListener;
import com.open.androidtvwidget.recycle.RecyclerViewTV;
import com.open.androidtvwidget.recycle.SpacesItemDecoration;
import com.open.androidtvwidget.view.MainUpView;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * created by danxingxi on 2016/4/14
 */
public class ActivityMain extends BaseActivity implements View.OnFocusChangeListener{

    private RecyclerViewTV appGridView;
    private List<AppBean> mAppData = new ArrayList<AppBean>();
    private AppGridAdapter mApater;
    private ReadAllApp appRead;
    private FocusListenerLinearLayout focusListenerLinearLayout;
    private AppReceiver appReceiver;
    private View mOldView;

    GridLayoutManagerTV gridlayoutManager;
    MainUpView mainUpView1;
    RecyclerViewBridge mRecyclerViewBridge;
    SpacesItemDecoration spacesItemDecoration;
    private View oldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        appRead = new ReadAllApp(this);
        mAppData = appRead.getLaunchAppList();
        appGridView = (RecyclerViewTV) findViewById(R.id.appGridView);

        focusListenerLinearLayout = (FocusListenerLinearLayout) findViewById(R.id.layoutContent);
        focusListenerLinearLayout.setOnFocusSearchListener(focusSearchListener);
//        focusListenerLinearLayout.setOnChildFocusChangeListener(focusChangeListener);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_space);

        appGridView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        appGridView.setItemAnimator(new DefaultItemAnimator());

        appGridView.setLayoutManager(gridlayoutManager);
        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView1);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        //
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.border_highlight);
        mRecyclerViewBridge.setTranDurAnimTime(200);
        mRecyclerViewBridge.setShadowResource(R.drawable.item_shadow);
        if(mAppData.size()<=20){
            gridlayoutManager = new GridLayoutManagerTV(this, 2);
            spacesItemDecoration = new SpacesItemDecoration(4,2);
        }else{
            gridlayoutManager = new GridLayoutManagerTV(this, 3);
            spacesItemDecoration = new SpacesItemDecoration(4,3);
        }
        gridlayoutManager.setOnChildSelectedListener(new OnChildSelectedListener() {
            @Override
            public void onChildSelected(RecyclerView parent, View focusview, int position, int dy) {
                focusview.bringToFront();
                if (oldView == null) {
                    Log.d("danxx", "oldView == null");
                }
                mRecyclerViewBridge.setFocusView(focusview, oldView, 1.1f);
                oldView = focusview;
            }
        });

        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        appGridView.setLayoutManager(gridlayoutManager);
        appGridView.setFocusable(false);
        mApater = new AppGridAdapter(this);
        appGridView.addItemDecoration(spacesItemDecoration);
        appGridView.setLayoutManager(gridlayoutManager);
        mApater.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                PackageManager manager = getPackageManager();
                String packageName = ((AppBean) data).getPackageName();
                Intent intent = new Intent();
                intent = manager.getLaunchIntentForPackage(packageName);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, Object data) {
            }
        });
        initFocus();
        upDateAllApp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        appReceiver = new AppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        registerReceiver(appReceiver, intentFilter);
    }

    private void initFocus(){
        findViewById(R.id.setting).setOnFocusChangeListener(this);
        findViewById(R.id.about).setOnFocusChangeListener(this);
        findViewById(R.id.managerApp).setOnFocusChangeListener(this);
        findViewById(R.id.clean).setOnFocusChangeListener(this);
        findViewById(R.id.hotSpot).setOnFocusChangeListener(this);
    }

    /**
     *更新显示的app
     */
    private void upDateAllApp(){
//        List<AppBean> mData =appRead.getLaunchAppList();
//        for(int i=0;i<mData.size();i++){
//            mAppData.add(mData.get(i));
//        }
        mApater.setData(mAppData);
        appGridView.setAdapter(mApater);
        mApater.notifyDataSetChanged();
    }

    private final FocusListenerLinearLayout.OnChildFocusChangeListener focusChangeListener = new FocusListenerLinearLayout.OnChildFocusChangeListener() {
        @Override
        public void focusChange(View oldView, View newView) {

            if(mOldView != null){
                FocusAnimUtils.unFocusAnim(mOldView);
            }
            if(newView != null){
                FocusAnimUtils.focusAnim(newView);
            }
            mOldView = null;
            mOldView = newView;  //4.3以下版本需要自己保存上一次的焦点.
        }
    };

    private final FocusListenerLinearLayout.OnFocusSearchListener focusSearchListener = new FocusListenerLinearLayout.OnFocusSearchListener() {
        @Override
        public View onFocusSearch(View focused, int direction) {
            View res = null;
            switch (direction){
                case View.FOCUS_DOWN:
                    break;
                case  View.FOCUS_UP:
                    if(appGridView.hasFocus()){
                        res = focused;
                    }
                    break;
                case View.FOCUS_LEFT:
                    break;
                case  View.FOCUS_RIGHT:
                    break;
            }
            return res;
        }
    };

    /**
     * Called when the focus state of a view has changed.
     *
     * @param v        The view whose state has changed.
     * @param hasFocus The new focus state of v.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mRecyclerViewBridge.setFocusView(v, oldView, 1.1f);
        oldView = v;
    }

    static class AppGridAdapter extends BaseRecyclerViewAdapter <AppBean> {

        private Context mContext;

        public AppGridAdapter(Context mContext) {
            this.mContext = mContext;
        }

        /**
         * 创建item view
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_app_launcher,null);
            AppViewHolder viewHolder = new AppViewHolder(itemView);

            viewHolder.appIcon = (ImageView) itemView.findViewById(R.id.appIcon);
            viewHolder.appName = (TextView) itemView.findViewById(R.id.appName);

            return viewHolder;
        }

        /**
         * 绑定数据
         *
         * @param holder
         * @param position
         */
        @Override
        protected void bindData(BaseRecyclerViewHolder holder, int position) {
            ((AppViewHolder)holder).appIcon.setImageDrawable(getItemData(position).getIcon());

            ((AppViewHolder)holder).appName.setText(getItemData(position).getName());

        }

        static class AppViewHolder extends BaseRecyclerViewHolder{
            View mView;
            ImageView appIcon;
            TextView appName;
            public AppViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }

            @Override
            protected View getView() {
                return mView;
            }

        }
    }

    public  void setting(View view){
        Intent intent = new Intent(Settings.ACTION_SETTINGS );
        startActivity(intent);
        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
    }

    public void managerApp(View view){
        Intent intent = new Intent(ActivityMain.this ,ActivityManagerApp.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
    }

    public void clean(View view){
        Log.d("danxx","clean---->");
        Intent intent = new Intent(ActivityMain.this ,ActivityQuicken.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
    }

    public void hotSpot(View view){
        Log.d("danxx","hotSpot---->");
        LiveVideoActivity.intentTo(ActivityMain.this,"","");
        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
    }

    public void about(View view){
        Intent intent = new Intent(ActivityMain.this ,ActivitySetting.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
    }

    private class AppReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //安装广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                upDateAllApp();
            }
            //卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                upDateAllApp();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){ //屏蔽返回键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
