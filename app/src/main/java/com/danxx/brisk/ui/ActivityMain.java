package com.danxx.brisk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danxx.brisk.R;
import com.danxx.brisk.adapter.BaseRecyclerViewAdapter;
import com.danxx.brisk.adapter.BaseRecyclerViewHolder;
import com.danxx.brisk.adapter.FocusGridLayoutManager;
import com.danxx.brisk.base.BaseActivity;
import com.danxx.brisk.model.AppBean;
import com.danxx.brisk.utils.FocusAnimUtils;
import com.danxx.brisk.utils.ReadAllApp;
import com.danxx.brisk.widget.FocusListenerLinearLayout;
import com.danxx.brisk.widget.FocusRecyclerView;
import com.danxx.brisk.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页
 * created by danxingxi on 2016/4/14
 */
public class ActivityMain extends BaseActivity {

    private FocusRecyclerView appGridView;
    private List<AppBean> mAppData = new ArrayList<AppBean>();
    private AppGridAdapter mApater;
    private FocusGridLayoutManager gridLayoutManager;
    private ReadAllApp appRead;
    private FocusListenerLinearLayout focusListenerLinearLayout;
    private AppReceiver appReceiver;
    private View mOldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        appRead = new ReadAllApp(this);
        appGridView = (FocusRecyclerView) findViewById(R.id.appGridView);

        focusListenerLinearLayout = (FocusListenerLinearLayout) findViewById(R.id.layoutContent);
        focusListenerLinearLayout.setOnFocusSearchListener(focusSearchListener);
        focusListenerLinearLayout.setOnChildFocusChangeListener(focusChangeListener);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_space);

        appGridView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        appGridView.setItemAnimator(new DefaultItemAnimator());
        gridLayoutManager = new FocusGridLayoutManager(this , 5);
        appGridView.setLayoutManager(gridLayoutManager);
        appGridView.setScrollDy(220);

        mApater = new AppGridAdapter(this);

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

    /**
     *更新显示的app
     */
    private void upDateAllApp(){
        mAppData = appRead.getLaunchAppList();
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
        Intent intent = new Intent(ActivityMain.this ,ActivityQuicken.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
    }

    public void hotSpot(View view){

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

}
